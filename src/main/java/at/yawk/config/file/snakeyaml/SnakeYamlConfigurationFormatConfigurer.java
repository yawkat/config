/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.config.file.snakeyaml;

import at.yawk.config.file.ConfigurationFormat;
import at.yawk.config.file.ConfigurationFormatConfigurer;
import java.util.Properties;
import lombok.Getter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.parser.Parser;

/**
 * @author yawkat
 */
public class SnakeYamlConfigurationFormatConfigurer implements ConfigurationFormatConfigurer {
    @Getter private static final ConfigurationFormatConfigurer instance = new SnakeYamlConfigurationFormatConfigurer();

    static {
        try {
            Class.forName(Parser.class.getName());
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    public ConfigurationFormat buildFormat(Properties properties) {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setAllowReadOnlyProperties(Boolean.parseBoolean(
                properties.getProperty("yaml.allowReadOnlyProperties", "false")));
        dumperOptions.setAllowUnicode(Boolean.parseBoolean(
                properties.getProperty("yaml.allowUnicode", "false")));
        dumperOptions.setCanonical(Boolean.parseBoolean(
                properties.getProperty("yaml.canonical", "false")));
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.valueOf(
                properties.getProperty("yaml.flowStyle", "BLOCK")));
        dumperOptions.setDefaultScalarStyle(DumperOptions.ScalarStyle.valueOf(
                properties.getProperty("yaml.scalarStyle", "PLAIN")));
        dumperOptions.setExplicitEnd(Boolean.parseBoolean(
                properties.getProperty("yaml.explicitEnd", "false")));
        dumperOptions.setExplicitStart(Boolean.parseBoolean(
                properties.getProperty("yaml.explicitStart", "false")));
        dumperOptions.setIndent(Integer.parseInt(
                properties.getProperty("yaml.indent", "2")));
        dumperOptions.setLineBreak(DumperOptions.LineBreak.valueOf(
                properties.getProperty("yaml.allowReadOnlyProperties", "UNIX")));
        dumperOptions.setPrettyFlow(Boolean.parseBoolean(
                properties.getProperty("yaml.prettyFlow", "true")));
        dumperOptions.setWidth(Integer.parseInt(
                properties.getProperty("yaml.width", "80")));
        return new SnakeYamlConfigurationFormat(dumperOptions);
    }
}
