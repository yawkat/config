/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.config.document;

/**
 * @author yawkat
 */
public interface TypeAdapter<T> {
    void write(WriterContext context, T obj);

    T read(ReaderContext context);

    default void writeKey(WriterContext context, T obj) {
        throw new UnsupportedOperationException();
    }

    default T readKey(ReaderContext context) {
        throw new UnsupportedOperationException();
    }
}
