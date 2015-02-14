/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.config.file;

/**
 * @author yawkat
 */
public enum TokenType {
    ENTER_OBJECT,
    EXIT_OBJECT,

    ENTER_LIST,
    EXIT_LIST,

    KEY,

    STRING,
    INT,
    LONG,
    FLOAT,
    DOUBLE,
    BOOLEAN,
}
