/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.config.file.gson;

import at.yawk.config.file.ConfigurationFormat;
import at.yawk.config.file.ConfigurationFormatConfigurer;
import com.google.gson.stream.JsonWriter;
import java.util.Properties;
import lombok.Getter;

/**
 * @author yawkat
 */
public class GsonConfigurationFormatConfigurer implements ConfigurationFormatConfigurer {
    @Getter private static final ConfigurationFormatConfigurer instance = new GsonConfigurationFormatConfigurer();

    static {
        try {
            Class.forName(JsonWriter.class.getName());
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    public ConfigurationFormat buildFormat(Properties properties) {
        String indent = properties.getProperty("json.indent", "    ");
        boolean lenient = Boolean.parseBoolean(properties.getProperty("json.lenient", "true"));
        int commentWrapWidth = Integer.parseInt(properties.getProperty("commentWrapWidth", "80"));

        return new GsonConfigurationFormat(indent, lenient, commentWrapWidth);
    }
}
