/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.config.file;

import java.util.Properties;

/**
 * @author yawkat
 */
public interface ConfigurationFormatConfigurer {
    ConfigurationFormat buildFormat(Properties properties);
}
