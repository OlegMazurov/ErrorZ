/*
 * Copyright 2017 Oleg Mazurov
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

package org.mazurov.errorz;

import org.junit.Assert;
import org.junit.Test;

/**
 * Project ErrorZ
 *
 * https://github.com/OlegMazurov/ErrorZ
 *
 */

public class BlockCodeTest {

    @Test
    public void testRS() {
        BlockCode code = new ReedSolomon(256, 248);
        int nRuns = 100;

        // Positive
        int decoded = code.testErrors(nRuns, 4);
        Assert.assertEquals(nRuns, decoded);

        // Negative
        decoded = code.testErrors(nRuns, 5);
        Assert.assertEquals(0, decoded);
    }

    @Test
    public void testRS2() {
        BlockCode code = new ReedSolomon(256, 240);
        int nRuns = 100;

        // Positive
        int decoded = code.testErrors(nRuns, 8);
        Assert.assertEquals(nRuns, decoded);

        // Negative
        decoded = code.testErrors(nRuns, 9);
        Assert.assertEquals(0, decoded);
    }

    @Test
    public void testRS3() {
        BlockCode code = new ReedSolomon(256, 232);
        int nRuns = 100;

        // Positive
        int decoded = code.testErrors(nRuns, 12);
        Assert.assertEquals(nRuns, decoded);

        // Negative
        decoded = code.testErrors(nRuns, 13);
        Assert.assertEquals(0, decoded);
    }

    @Test
    public void testMazurov() {
        BlockCode code = new Mazurov(256, 248);
        int nRuns = 100;

        // Positive only
        int decoded = code.testErrors(nRuns, 7);
        Assert.assertEquals(nRuns, decoded);
    }

    @Test
    public void testMazurov2() {
        BlockCode code = new Mazurov(256, 240);
        int nRuns = 100;

        // Positive
        int decoded = code.testErrors(nRuns, 14);
        Assert.assertEquals(nRuns, decoded);

        // Negative
        decoded = code.testErrors(nRuns, 15);
        Assert.assertEquals(0, decoded);
    }

    @Test
    public void testMazurov3() {
        BlockCode code = new Mazurov(256, 232);
        int nRuns = 100;

        // Positive
        int decoded = code.testErrors(nRuns, 21);
        Assert.assertEquals(nRuns, decoded);

        // Negative
        decoded = code.testErrors(nRuns, 22);
        Assert.assertEquals(0, decoded);
    }

}