/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.config.file;

/**
 * @author yawkat
 */
public interface ObjectWriter {
    ObjectWriter key(String key);

    ObjectWriter enterObject();

    ObjectWriter enterList();

    ObjectWriter exitObject();

    ObjectWriter exitList();

    ObjectWriter comment(String comment);

    ObjectWriter item(String value);

    ObjectWriter item(int value);

    ObjectWriter item(long value);

    ObjectWriter item(float value);

    ObjectWriter item(double value);

    ObjectWriter item(boolean value);
}
