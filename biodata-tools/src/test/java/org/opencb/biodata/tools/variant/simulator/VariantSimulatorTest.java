/*
 * Copyright 2015 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.biodata.tools.variant.simulator;

import org.junit.jupiter.api.Test;
import org.opencb.biodata.models.variant.Variant;

/**
 * Created by imedina on 27/10/15.
 */
public class VariantSimulatorTest {

    @Test
    public void testSimulate() throws Exception {
        VariantSimulatorConfiguration variantSimulatorConfiguration = new VariantSimulatorConfiguration();
        VariantSimulator variantSimulator = new VariantSimulator(variantSimulatorConfiguration);
        Variant variant = variantSimulator.simulate();
//        System.out.println(variant);
        System.out.println(variant.toJson());
    }

    @Test
    public void testSimulate1() throws Exception {

    }

    @Test
    public void testSimulate2() throws Exception {

    }
}