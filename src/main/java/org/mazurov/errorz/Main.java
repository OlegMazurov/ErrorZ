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

/**
 * Project ErrorZ
 *
 * https://github.com/OlegMazurov/ErrorZ
 *
 */

public class Main {

    public static void main(String[] args) {

        long seed = System.currentTimeMillis();
        Random.reset(seed);
        System.out.println("seed: " + seed);

        BlockCode code;
        int nRuns, decoded;

        code = new Vandermonde(256, 248);
        nRuns = 1000;
        decoded = TestUtils.testErrors(code, nRuns, 4);
        System.out.println("Test result: " + (decoded == nRuns ? "PASS" : "FAIL"));
        decoded = TestUtils.testErrors(code, nRuns, 5);
        System.out.println("Test result: " + (decoded == 0 ? "PASS" : "FAIL"));

        code = new Vandermonde(256, 240);
        nRuns = 1000;
        decoded = TestUtils.testErrors(code, nRuns, 8);
        System.out.println("Test result: " + (decoded == nRuns ? "PASS" : "FAIL"));
        decoded = TestUtils.testErrors(code, nRuns, 9);
        System.out.println("Test result: " + (decoded == 0 ? "PASS" : "FAIL"));

        code = new Vandermonde(256, 232);
        nRuns = 1000;
        decoded = TestUtils.testErrors(code, nRuns, 12);
        System.out.println("Test result: " + (decoded == nRuns ? "PASS" : "FAIL"));
        decoded = TestUtils.testErrors(code, nRuns, 13);
        System.out.println("Test result: " + (decoded == 0 ? "PASS" : "FAIL"));

        code = new Mazurov(256, 248);
        nRuns = 1000;
        decoded = TestUtils.testErrors(code, nRuns, 7);
        System.out.println("Test result: " + (decoded == nRuns ? "PASS" : "FAIL"));

        code = new Mazurov(256, 240);
        nRuns = 1000;
        decoded = TestUtils.testErrors(code, nRuns, 14);
        System.out.println("Test result: " + (decoded == nRuns ? "PASS" : "FAIL"));
        decoded = TestUtils.testErrors(code, nRuns, 15);
        System.out.println("Test result: " + (decoded == 0 ? "PASS" : "FAIL"));

        code = new Mazurov(256, 232);
        nRuns = 1000;
        decoded = TestUtils.testErrors(code, nRuns, 21);
        System.out.println("Test result: " + (decoded == nRuns ? "PASS" : "FAIL"));
        decoded = TestUtils.testErrors(code, nRuns, 22);
        System.out.println("Test result: " + (decoded == 0 ? "PASS" : "FAIL"));

        code = new BlockCode2D(256, 224, 256, 224, new Vandermonde());
        TestUtils.testErrors(code, 25);

        code = new BlockCode2D(256, 224, 256, 224, new Mazurov());
        TestUtils.testErrors(code, 25);
    }

}
