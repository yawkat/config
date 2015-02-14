/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.config.file.gson;

import at.yawk.config.ConfigurationException;
import at.yawk.config.Util;
import at.yawk.config.file.ConfigurationFormat;
import at.yawk.config.file.ObjectReader;
import at.yawk.config.file.ObjectWriter;
import at.yawk.config.file.TokenType;
import at.yawk.reflect.Methods;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;

/**
 * @author yawkat
 */
@RequiredArgsConstructor
class GsonConfigurationFormat implements ConfigurationFormat {
    private final String indent;
    private final boolean lenient;
    private final int commentWrapWidth; // -1 for no wrap

    @Override
    public String getExtension() {
        return ".json";
    }

    @Override
    public String getMimeType() {
        return "application/json";
    }

    @Override
    public ObjectWriter createWriter(Writer writer) {
        JsonWriter handle = new JsonWriter(writer);
        handle.setLenient(lenient);
        handle.setIndent(indent);
        return new ObjectWriter() {
            int depth = 0;

            private void flush() {
                if (depth == 0) {
                    try {
                        writer.flush();
                    } catch (IOException e) {
                        throw new ConfigurationException(e);
                    }
                }
            }

            @Override
            public ObjectWriter key(String key) {
                try {
                    handle.name(key);
                } catch (IOException e) {
                    throw new ConfigurationException(e);
                }
                return this;
            }

            @Override
            public ObjectWriter enterObject() {
                try {
                    handle.beginObject();
                } catch (IOException e) {
                    throw new ConfigurationException(e);
                }
                depth++;
                flush();
                return this;
            }

            @Override
            public ObjectWriter enterList() {
                try {
                    handle.beginArray();
                } catch (IOException e) {
                    throw new ConfigurationException(e);
                }
                depth++;
                flush();
                return this;
            }

            @Override
            public ObjectWriter exitObject() {
                try {
                    handle.endObject();
                } catch (IOException e) {
                    throw new ConfigurationException(e);
                }
                depth--;
                flush();
                return this;
            }

            @Override
            public ObjectWriter exitList() {
                try {
                    handle.endArray();
                } catch (IOException e) {
                    throw new ConfigurationException(e);
                }
                depth--;
                flush();
                return this;
            }

            @Override
            public ObjectWriter comment(String comment) {
                try {
                    for (String line : (
                            commentWrapWidth >= 0 ?
                                    Util.wrap(comment, commentWrapWidth) :
                                    Arrays.asList(comment)
                    )) {
                        Methods.of(handle).name("newline").invoke();
                        writer.write("// " + line);
                    }
                } catch (IOException e) {
                    throw new ConfigurationException(e);
                }
                flush();
                return this;
            }

            @Override
            public ObjectWriter item(String value) {
                try {
                    handle.value(value);
                } catch (IOException e) {
                    throw new ConfigurationException(e);
                }
                flush();
                return this;
            }

            @Override
            public ObjectWriter item(int value) {
                try {
                    handle.value(value);
                } catch (IOException e) {
                    throw new ConfigurationException(e);
                }
                flush();
                return this;
            }

            @Override
            public ObjectWriter item(long value) {
                try {
                    handle.value(value);
                } catch (IOException e) {
                    throw new ConfigurationException(e);
                }
                flush();
                return this;
            }

            @Override
            public ObjectWriter item(float value) {
                try {
                    handle.value(value);
                } catch (IOException e) {
                    throw new ConfigurationException(e);
                }
                flush();
                return this;
            }

            @Override
            public ObjectWriter item(double value) {
                try {
                    handle.value(value);
                } catch (IOException e) {
                    throw new ConfigurationException(e);
                }
                flush();
                return this;
            }

            @Override
            public ObjectWriter item(boolean value) {
                try {
                    handle.value(value);
                } catch (IOException e) {
                    throw new ConfigurationException(e);
                }
                flush();
                return this;
            }
        };
    }

    @Override
    public ObjectReader createReader(Reader reader) {
        JsonReader handle = new JsonReader(reader);
        handle.setLenient(lenient);
        return new ObjectReader() {
            @Override
            public TokenType peek() {
                JsonToken peek;
                try {
                    peek = handle.peek();
                } catch (IOException e) {
                    throw new ConfigurationException(e);
                }
                switch (peek) {
                case BEGIN_ARRAY:
                    return TokenType.ENTER_LIST;
                case END_ARRAY:
                    return TokenType.EXIT_LIST;
                case BEGIN_OBJECT:
                    return TokenType.ENTER_OBJECT;
                case END_OBJECT:
                    return TokenType.EXIT_OBJECT;
                case NAME:
                    return TokenType.KEY;
                case STRING:
                    return TokenType.STRING;
                case NUMBER:
                    return TokenType.DOUBLE;
                case BOOLEAN:
                    return TokenType.BOOLEAN;
                default:
                    throw new ConfigurationException("Unsupported peek type " + peek);
                }
            }

            @Override
            public void skipDeep() {
                try {
                    handle.skipValue();
                } catch (IOException e) {
                    throw new ConfigurationException(e);
                }
            }

            @Override
            public void enterObject() {
                try {
                    handle.beginObject();
                } catch (IOException e) {
                    throw new ConfigurationException(e);
                }
            }

            @Override
            public void exitObject() {
                try {
                    handle.endObject();
                } catch (IOException e) {
                    throw new ConfigurationException(e);
                }
            }

            @Override
            public void enterList() {
                try {
                    handle.beginArray();
                } catch (IOException e) {
                    throw new ConfigurationException(e);
                }
            }

            @Override
            public void exitList() {
                try {
                    handle.endArray();
                } catch (IOException e) {
                    throw new ConfigurationException(e);
                }
            }

            @Override
            public String key() {
                try {
                    return handle.nextName();
                } catch (IOException e) {
                    throw new ConfigurationException(e);
                }
            }

            @Override
            public String stringValue() {
                try {
                    return handle.nextString();
                } catch (IOException e) {
                    throw new ConfigurationException(e);
                }
            }

            @Override
            public int intValue() {
                try {
                    return handle.nextInt();
                } catch (IOException e) {
                    throw new ConfigurationException(e);
                }
            }

            @Override
            public long longValue() {
                try {
                    return handle.nextLong();
                } catch (IOException e) {
                    throw new ConfigurationException(e);
                }
            }

            @Override
            public float floatValue() {
                return (float) doubleValue();
            }

            @Override
            public double doubleValue() {
                try {
                    return handle.nextDouble();
                } catch (IOException e) {
                    throw new ConfigurationException(e);
                }
            }

            @Override
            public boolean booleanValue() {
                try {
                    return handle.nextBoolean();
                } catch (IOException e) {
                    throw new ConfigurationException(e);
                }
            }
        };
    }
}
