/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.config.file;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author yawkat
 */
public interface ConfigurationFormat {
    String getExtension();

    String getMimeType();

    default ObjectWriter createWriter(OutputStream outputStream) {
        return createWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
    }

    ObjectWriter createWriter(Writer writer);

    default ObjectReader createReader(InputStream inputStream) {
        return createReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }

    ObjectReader createReader(Reader reader);
}
