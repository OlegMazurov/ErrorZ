/*
 * Copyright 2017,2020 Oleg Mazurov
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
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * Project ErrorZ
 *
 * https://github.com/OlegMazurov/ErrorZ
 *
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BlockCodeTest {

    @Test
    public void testA_RS() {
        BlockCode code = new Vandermonde(256, 248);
        int nRuns = 100;

        // Positive
        int decoded = TestUtils.testErrors(code, nRuns, 4);
        Assert.assertEquals(nRuns, decoded);

        // Negative
        decoded = TestUtils.testErrors(code, nRuns, 5);
        Assert.assertEquals(0, decoded);
    }

    @Test
    public void testA_RS2() {
        BlockCode code = new Vandermonde(256, 240);
        int nRuns = 100;

        // Positive
        int decoded = TestUtils.testErrors(code, nRuns, 8);
        Assert.assertEquals(nRuns, decoded);

        // Negative
        decoded = TestUtils.testErrors(code, nRuns, 9);
        Assert.assertEquals(0, decoded);
    }

    @Test
    public void testA_RS3() {
        BlockCode code = new Vandermonde(256, 232);
        int nRuns = 100;

        // Positive
        int decoded = TestUtils.testErrors(code, nRuns, 12);
        Assert.assertEquals(nRuns, decoded);

        // Negative
        decoded = TestUtils.testErrors(code, nRuns, 13);
        Assert.assertEquals(0, decoded);
    }

    @Test
    public void testB_Mazurov() {
        BlockCode code = new Mazurov(256, 248);
        int nRuns = 100;

        // Positive only
        int decoded = TestUtils.testErrors(code, nRuns, 7);
        Assert.assertEquals(nRuns, decoded);
    }

    @Test
    public void testB_Mazurov2() {
        BlockCode code = new Mazurov(256, 240);
        int nRuns = 100;

        // Positive
        int decoded = TestUtils.testErrors(code, nRuns, 14);
        Assert.assertEquals(nRuns, decoded);

        // Negative
        decoded = TestUtils.testErrors(code, nRuns, 15);
        Assert.assertEquals(0, decoded);
    }

    @Test
    public void testB_Mazurov3() {
        BlockCode code = new Mazurov(256, 232);
        int nRuns = 100;

        // Positive
        int decoded = TestUtils.testErrors(code, nRuns, 21);
        Assert.assertEquals(nRuns, decoded);

        // Negative
        decoded = TestUtils.testErrors(code, nRuns, 22);
        Assert.assertEquals(0, decoded);
    }

    @Test
    public void testB_Mazurov4() {
        BlockCode code = new BlockCode2D(256, 250, 256, 250, new Mazurov());
        int nRuns = 1;

        // Positive only
        int decoded = TestUtils.testErrors(code, nRuns, 2100);
        Assert.assertEquals(nRuns, decoded);
    }

    @Test
    public void testC_Lagrange1() {
        BlockCode code = new Lagrange(256, 232, new Vandermonde());
        int nRuns = 100;

        // Positive
        int decoded = TestUtils.testErrors(code, nRuns, 12);
        Assert.assertEquals(nRuns, decoded);

        // Negative
        decoded = TestUtils.testErrors(code, nRuns, 13);
        Assert.assertEquals(0, decoded);

    }

    @Test
    public void testC_Lagrange2() {
        BlockCode code = new Lagrange(256, 232, new Mazurov());
        int nRuns = 100;

        // Positive
        int decoded = TestUtils.testErrors(code, nRuns, 21);
        Assert.assertEquals(nRuns, decoded);

        // Negative
        decoded = TestUtils.testErrors(code, nRuns, 22);
        Assert.assertEquals(0, decoded);
    }

    @Test
    public void testD_Cauchy1() {
        BlockCode code = new Cauchy(256, 232, new Vandermonde());
        int nRuns = 100;

        // Positive
        int decoded = TestUtils.testErrors(code, nRuns, 12);
        Assert.assertEquals(nRuns, decoded);

        // Negative
        decoded = TestUtils.testErrors(code, nRuns, 13);
        Assert.assertEquals(0, decoded);

    }

    @Test
    public void testD_Cauchy2() {
        BlockCode code = new Cauchy(256, 232, new Mazurov());
        int nRuns = 100;

        // Positive
        int decoded = TestUtils.testErrors(code, nRuns, 21);
        Assert.assertEquals(nRuns, decoded);

        // Negative
        decoded = TestUtils.testErrors(code, nRuns, 22);
        Assert.assertEquals(0, decoded);
    }

}