/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.config.document;

import at.yawk.config.file.ObjectWriter;
import java.lang.reflect.Type;

/**
 * @author yawkat
 */
public interface WriterContext extends ObjectWriter {
    void writeObject(Type type, Object object);

    void writeObjectKey(Type type, Object object);
}
