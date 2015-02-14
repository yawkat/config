/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.config.file;

import at.yawk.config.file.gson.GsonConfigurationFormatConfigurer;
import at.yawk.config.file.snakeyaml.SnakeYamlConfigurationFormatConfigurer;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yawkat
 */
@Slf4j
public class ConfigurationFormatConfigurerRegistry {
    private static final Multimap<String, ConfigurationFormatConfigurer> configurers =
            Multimaps.synchronizedMultimap(HashMultimap.create());

    private static final ConfigurationFormatConfigurer PROPERTY_DETECTING_CONFIGURER = properties -> {
        String formatName = properties.getProperty("format");
        return findConfigurer(formatName).buildFormat(properties);
    };

    static {
        try {
            ConfigurationFormatConfigurer gcfc = GsonConfigurationFormatConfigurer.getInstance();
            configurers.put("json", gcfc);
            configurers.put("gson", gcfc);
        } catch (NoClassDefFoundError | ExceptionInInitializerError ignored) {}
        try {
            ConfigurationFormatConfigurer gcfc = SnakeYamlConfigurationFormatConfigurer.getInstance();
            configurers.put("yaml", gcfc);
            configurers.put("snakeyaml", gcfc);
        } catch (NoClassDefFoundError | ExceptionInInitializerError ignored) {}
    }

    private ConfigurationFormatConfigurerRegistry() {}

    public static Collection<ConfigurationFormatConfigurer> findConfigurers(String name) {
        Collection<ConfigurationFormatConfigurer> registered = configurers.get(name);
        if (name.contains(".")) {
            try {
                Class<?> clazz = Class.forName(name);
                registered.add((ConfigurationFormatConfigurer) clazz.newInstance());
            } catch (ClassNotFoundException ignored) {
            } catch (InstantiationException | IllegalAccessException | ClassCastException e) {
                log.warn("Failed to instantiate " + name, e);
            }
        }
        return Collections.unmodifiableCollection(registered);
    }

    public static ConfigurationFormatConfigurer findConfigurer(String name) {
        Collection<ConfigurationFormatConfigurer> configurers = findConfigurers(name);
        if (configurers.isEmpty()) {
            throw new NoSuchElementException("Could not find configurer for format " + name);
        }
        return configurers.iterator().next();
    }

    public static ConfigurationFormatConfigurer propertyDetectingConfigurer() {
        return PROPERTY_DETECTING_CONFIGURER;
    }
}
