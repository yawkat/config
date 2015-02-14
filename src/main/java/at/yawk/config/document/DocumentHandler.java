/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.config.document;

import at.yawk.config.file.ObjectReader;
import at.yawk.config.file.ObjectWriter;

/**
 * @author yawkat
 */
public interface DocumentHandler {
    void write(ObjectWriter target, Object o);

    <T> T read(ObjectReader source, Class<T> type);
}
