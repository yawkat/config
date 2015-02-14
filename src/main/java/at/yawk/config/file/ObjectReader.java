/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.config.file;

import at.yawk.config.file.TokenType;

/**
 * @author yawkat
 */
public interface ObjectReader {
    TokenType peek();

    void skipDeep();

    void enterObject();

    void exitObject();

    void enterList();

    void exitList();

    String key();

    String stringValue();

    int intValue();

    long longValue();

    float floatValue();

    double doubleValue();

    boolean booleanValue();
}
