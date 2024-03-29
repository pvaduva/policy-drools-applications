/*-
 * ============LICENSE_START=======================================================
 * guard
 * ================================================================================
 * Copyright (C) 2018 Ericsson. All rights reserved.
 * Modifications Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.policy.guard;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.drools.core.util.IoUtils;

/**
 * The Class TextFileUtils is class that provides useful functions for handling text files.
 * Functions to read and wrtie text files to strings and strings are provided.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class SupportTextFileUtils {

    private SupportTextFileUtils() {
        // do nothing
    }

    /**
     * Method to return the contents of a text file as a string.
     *
     * @param textFilePath The path to the file as a string
     * @return A string containing the contents of the file
     * @throws IOException on errors reading text from the file
     */
    public static String getTextFileAsString(final String textFilePath) {
        return IoUtils.readFileAsString(new File(textFilePath));
    }

    /**
     * Method to write contents of a string to a text file.
     *
     * @param outString The string to write
     * @param textFile The file to write the string to
     * @throws IOException on errors reading text from the file
     */
    public static void putStringAsFile(final String outString, final File textFile) throws IOException {
        try (final FileOutputStream textFileOutputStream = new FileOutputStream(textFile)) {
            textFileOutputStream.write(outString.getBytes(StandardCharsets.UTF_8));
        }
    }
}
