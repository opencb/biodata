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

package org.opencb.biodata.models.variant.exceptions;

/**
 * Created by fjlopez on 10/04/15.
 */
public class NotAVariantException extends RuntimeException {

    /**
     * Constructs an instance of <code>NotAVariantException</code>
     * for a field, and with the specified detail message.
     */
    public NotAVariantException() {
        super();
    }

    /**
     * Constructs an instance of <code>NotAVariantException</code>
     * for a field, and with the specified detail message.
     *
     * @param msg the detail message.
     */
    public NotAVariantException(String msg) {
        super(msg);
    }

}
