/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.config;

import at.yawk.config.document.DocumentHandler;
import at.yawk.config.document.DocumentHandlerBuilder;
import at.yawk.config.file.ConfigurationFormat;
import at.yawk.config.file.ConfigurationFormatConfigurerRegistry;
import at.yawk.config.file.ObjectReader;
import at.yawk.config.file.ObjectWriter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author yawkat
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Configuration {
    private final DocumentHandler documentHandler;
    private final ConfigurationFormat format;

    public static Configuration create(Properties properties) {
        ConfigurationFormat format = ConfigurationFormatConfigurerRegistry
                .propertyDetectingConfigurer()
                .buildFormat(properties);

        DocumentHandlerBuilder builder = new DocumentHandlerBuilder();
        DocumentHandler documentHandler = builder.build();

        return new Configuration(documentHandler, format);
    }

    public static Configuration create() {
        Properties properties = new Properties();
        try (InputStream configStream = Configuration.class.getResourceAsStream("/config-format.properties")) {
            properties.load(configStream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return create(properties);
    }

    public <T> T load(Class<T> type, Reader reader) {
        return load(type, format.createReader(reader));
    }

    public <T> T load(Class<T> type, InputStream inputStream) {
        return load(type, format.createReader(inputStream));
    }

    public <T> T load(Class<T> type, Path path) {
        try (InputStream inputStream = Files.newInputStream(path)) {
            return load(type, inputStream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private <T> T load(Class<T> type, ObjectReader reader) {
        return documentHandler.read(reader, type);
    }

    public void save(Object o, Writer writer) {
        save(o, format.createWriter(writer));
    }

    public void save(Object o, OutputStream outputStream) {
        save(o, format.createWriter(outputStream));
    }

    public void save(Object o, Path path) {
        try (OutputStream outputStream = Files.newOutputStream(path)) {
            save(o, outputStream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void save(Object o, ObjectWriter writer) {
        documentHandler.write(writer, o);
    }
}
