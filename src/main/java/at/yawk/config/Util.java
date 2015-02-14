/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.config;

import com.google.common.base.Splitter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.text.WordUtils;

/**
 * @author yawkat
 */
public class Util {
    private static final Splitter LINE_SPLITTER = Splitter.on('\n');

    private Util() {}

    public static List<String> wrap(String text, int length) {
        List<String> list = new ArrayList<>();
        for (String line : LINE_SPLITTER.split(text)) {
            list.addAll(LINE_SPLITTER.splitToList(WordUtils.wrap(line, length)));
        }
        return list;
    }
}
