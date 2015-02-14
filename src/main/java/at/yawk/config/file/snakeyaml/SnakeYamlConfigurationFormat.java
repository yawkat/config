/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.config.file.snakeyaml;

import at.yawk.config.ConfigurationException;
import at.yawk.config.Util;
import at.yawk.config.file.ConfigurationFormat;
import at.yawk.config.file.ObjectReader;
import at.yawk.config.file.ObjectWriter;
import at.yawk.config.file.TokenType;
import at.yawk.reflect.Methods;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import lombok.RequiredArgsConstructor;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.emitter.Emitter;
import org.yaml.snakeyaml.events.*;
import org.yaml.snakeyaml.parser.Parser;
import org.yaml.snakeyaml.parser.ParserImpl;
import org.yaml.snakeyaml.reader.StreamReader;

/**
 * @author yawkat
 */
@RequiredArgsConstructor
class SnakeYamlConfigurationFormat implements ConfigurationFormat {
    private final DumperOptions dumperOptions;

    @Override
    public String getExtension() {
        return ".yml";
    }

    @Override
    public String getMimeType() {
        return "application/x-yaml";
    }

    @Override
    public ObjectWriter createWriter(Writer writer) {
        Emitter emitter = new Emitter(writer, dumperOptions);
        return new ObjectWriter() {
            boolean start = true;
            int depth = 0;

            private ObjectWriter emit(Event event) {
                if (start) {
                    start = false;
                    emit(new StreamStartEvent(null, null));
                    emit(new DocumentStartEvent(null, null, false, null, null));
                }
                try {
                    emitter.emit(event);
                } catch (IOException e) {
                    throw new ConfigurationException(e);
                }
                if (event instanceof CollectionStartEvent) { depth++; }
                if (event instanceof CollectionEndEvent) { depth--; }
                if (depth == 0) {
                    try {
                        writer.flush();
                    } catch (IOException e) {
                        throw new ConfigurationException(e);
                    }
                }
                return this;
            }

            @Override
            public ObjectWriter key(String key) {
                return item(key);
            }

            @Override
            public ObjectWriter enterObject() {
                return emit(new MappingStartEvent(
                        null,
                        null,
                        true,
                        null,
                        null,
                        dumperOptions.getDefaultFlowStyle().getStyleBoolean()
                ));
            }

            @Override
            public ObjectWriter enterList() {
                return emit(new SequenceStartEvent(
                        null,
                        null,
                        true,
                        null,
                        null,
                        dumperOptions.getDefaultFlowStyle().getStyleBoolean()));
            }

            @Override
            public ObjectWriter exitObject() {
                return emit(new MappingEndEvent(null, null));
            }

            @Override
            public ObjectWriter exitList() {
                return emit(new SequenceEndEvent(null, null));
            }

            @Override
            public ObjectWriter comment(String comment) {
                for (String line : Util.wrap(comment, dumperOptions.getWidth())) {
                    Methods.of(emitter).name("writeIndent").invoke();
                    try {
                        writer.write("# " + line);
                    } catch (IOException e) {
                        throw new ConfigurationException(e);
                    }
                    Methods.of(emitter).name("writeLineBreak").invoke((Object) null);
                }
                return this;
            }

            @Override
            public ObjectWriter item(String value) {
                return emit(new ScalarEvent(
                        null,
                        null,
                        new ImplicitTuple(true, true),
                        value,
                        null,
                        null,
                        null
                ));
            }

            @Override
            public ObjectWriter item(int value) {
                return item(String.valueOf(value));
            }

            @Override
            public ObjectWriter item(long value) {
                return item(String.valueOf(value));
            }

            @Override
            public ObjectWriter item(float value) {
                return item(String.valueOf(value));
            }

            @Override
            public ObjectWriter item(double value) {
                return item(String.valueOf(value));
            }

            @Override
            public ObjectWriter item(boolean value) {
                return item(String.valueOf(value));
            }
        };
    }

    @Override
    public ObjectReader createReader(Reader reader) {
        Parser parser = new ParserImpl(new StreamReader(reader));
        return new ObjectReader() {
            private Event poll() {
                Event evt;
                do {
                    evt = parser.getEvent();
                } while (evt instanceof StreamStartEvent ||
                         evt instanceof StreamEndEvent ||
                         evt instanceof DocumentStartEvent ||
                         evt instanceof DocumentEndEvent);
                return evt;
            }

            @SuppressWarnings("ResultOfMethodCallIgnored")
            @Override
            public TokenType peek() {
                Event event = parser.peekEvent();
                if (event instanceof SequenceStartEvent) { return TokenType.ENTER_LIST; }
                if (event instanceof SequenceEndEvent) { return TokenType.EXIT_LIST; }
                if (event instanceof MappingStartEvent) { return TokenType.ENTER_OBJECT; }
                if (event instanceof MappingEndEvent) { return TokenType.EXIT_OBJECT; }
                if (event instanceof ScalarEvent) {
                    String value = ((ScalarEvent) event).getValue();
                    try {
                        Long.parseLong(value);
                        return TokenType.LONG;
                    } catch (NumberFormatException ignored) {}
                    try {
                        Double.parseDouble(value);
                        return TokenType.DOUBLE;
                    } catch (NumberFormatException ignored) {}
                    if (value.equals("true") || value.equals("false")) {
                        return TokenType.BOOLEAN;
                    }
                    return TokenType.STRING;
                }
                throw new ConfigurationException("Unsupported event " + event);
            }

            @Override
            public void skipDeep() {
                int depth = 0;
                do {
                    Event event = poll();
                    if (event instanceof CollectionStartEvent) { depth++; }
                    if (event instanceof CollectionEndEvent) { depth--; }
                } while (depth > 0);
            }

            @Override
            public void enterObject() {
                Event event = poll();
                if (!(event instanceof MappingStartEvent)) {
                    throw new ConfigurationException("Unexpected " + event);
                }
            }

            @Override
            public void exitObject() {
                Event event = poll();
                if (!(event instanceof MappingEndEvent)) {
                    throw new ConfigurationException("Unexpected " + event);
                }
            }

            @Override
            public void enterList() {
                Event event = poll();
                if (!(event instanceof SequenceStartEvent)) {
                    throw new ConfigurationException("Unexpected " + event);
                }
            }

            @Override
            public void exitList() {
                Event event = poll();
                if (!(event instanceof SequenceEndEvent)) {
                    throw new ConfigurationException("Unexpected " + event);
                }
            }

            @Override
            public String key() {
                return stringValue();
            }

            @Override
            public String stringValue() {
                Event event = poll();
                return ((ScalarEvent) event).getValue();
            }

            @Override
            public int intValue() {
                return Integer.parseInt(stringValue());
            }

            @Override
            public long longValue() {
                return Long.parseLong(stringValue());
            }

            @Override
            public float floatValue() {
                return Float.parseFloat(stringValue());
            }

            @Override
            public double doubleValue() {
                return Double.parseDouble(stringValue());
            }

            @Override
            public boolean booleanValue() {
                return Boolean.parseBoolean(stringValue());
            }
        }

                ;
    }
}
