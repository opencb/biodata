/*
 * <!--
 *   ~ Copyright 2015-2017 OpenCB
 *   ~
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at
 *   ~
 *   ~     http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License.
 *   -->
 *
 */

package org.opencb.biodata.tools.variant;

import org.opencb.biodata.models.variant.VariantFileMetadata;
import org.opencb.biodata.models.variant.metadata.VariantFileHeaderComplexLine;

import java.util.*;

public class VcfUtils {

    private static final Map<String, List<String>> SUPPORTED_CALLERS;

    static {
        SUPPORTED_CALLERS = new LinkedHashMap<>();
        SUPPORTED_CALLERS.put("CAVEMAN", Arrays.asList("ASMD", "CLPM"));
        SUPPORTED_CALLERS.put("PINDEL", Arrays.asList("PC", "VT"));
        SUPPORTED_CALLERS.put("BRASS", Arrays.asList("BAS", "BKDIST"));
    }

    public static String getCaller(VariantFileMetadata fileMetadata) {
        // Try to find the variant caller if it is not provided
        // Check if any of the supported callers contains the INFO fields needed
        String caller = null;
        for (Map.Entry<String, List<String>> entry : SUPPORTED_CALLERS.entrySet()) {
            if (checkCaller(entry.getKey(), entry.getValue(), fileMetadata)) {
                caller = entry.getKey();
                break;
            }
        }
        return caller;
    }

    public static boolean checkCaller(String caller, VariantFileMetadata fileMetadata) {
        if (SUPPORTED_CALLERS.containsKey(caller)) {
            return checkCaller(caller, SUPPORTED_CALLERS.get(caller), fileMetadata);
        }
        return false;
    }

    /**
     * Checks if all header fields passed exists.
     * @param caller Variant caller name to check
     * @param keys VCF header fields to check
     * @return true if the name is found or all the field exist
     */
    private static boolean checkCaller(String caller, List<String> keys, VariantFileMetadata fileMetadata) {
        // TODO we need to use caller param for some callers
        Set<String> keySet = new HashSet<>(keys);
        int counter = 0;
        for (VariantFileHeaderComplexLine complexLine : fileMetadata.getHeader().getComplexLines()) {
            if (complexLine.getKey().equals("INFO") && keySet.contains(complexLine.getId())) {
                counter++;

                // Return when all needed keys have been found
                if (keys.size() == counter) {
                    return true;
                }
            }
        }
        return false;
    }

}
