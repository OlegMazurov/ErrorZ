/*
 * Copyright 2020 Oleg Mazurov
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

public class TestUtils {

    /**
     * Generate exactly @E random errors in the code word
     * @param E number of errors
     * @return array of error indices
     */
    public static int[] addErrors(BlockCode code, int E) {
        int[] idx = new int[E];
        int N = code.getBlockLength();
        boolean[] marks = new boolean[N];
        for (int i = 0; i < E;) {
            int next = Random.nextInt(N);
            if (marks[next]) continue;
            long val = Random.nextLong();
            if (code.getAt(next) == val) continue;
            code.setAt(next, val);
            marks[next] = true;
            idx[i++] = next;
        }
        return idx;
    }

    /**
     * Test error recovery
     * @param code     block code
     * @param nRuns    number of iterations
     * @param errors   number of errors
     */
    public static int testErrors(BlockCode code, int nRuns, int errors) {
        int rejected = 0;       // rejected decoding
        int failed = 0;         // erroneous decoding
        for (int t = 0; t < nRuns; ++t) {
            BlockCode testCode = code.clone();
            addErrors(testCode, errors);    // ignore error locations
            if (!testCode.decode()) {
                rejected += 1;
            }
            else {
                for (int i = 0; i < code.getBlockLength(); ++i) {
                    if (testCode.getAt(i) != code.getAt(i)) {
                        failed += 1;
                        break;
                    }
                }
            }
        }
        int redundancy = code.getBlockLength() - code.getMessageLength();
        int decoded = nRuns - rejected - failed;
        System.out.printf("%s, redundancy: %d, errors: %d, runs: %d, decoded: %d, rejected: %d, failed: %d%n",
                code, redundancy, errors, nRuns, decoded, rejected, failed);
        return decoded;
    }

    /**
     * Iteratively finds code's decoding ability in terms of the minimum number of errors
     * where decoding fails and the maximum number of error where decoding succeeds.
     * @param nRuns maximum number of iterations
     */
    public static void testErrors(BlockCode code, int nRuns) {
        System.out.println(code);
        int redundancy = code.getBlockLength() - code.getMessageLength();
        int maxDecoded = 0;
        int minFailed = redundancy;
        for (int t = 0; t < nRuns; ++t) {
            BlockCode testCode = code.clone();
            int errors;
            if (maxDecoded + 1 < minFailed) {
                errors = (maxDecoded + minFailed) / 2;
            }
            else {
                int delta = 1 + Random.nextInt(1 + (int)(16. * (1. - (double)t / nRuns)));
                errors = (t %2 == 0) ? maxDecoded + delta : minFailed - delta;
            }
            addErrors(testCode, errors);
            boolean decoded = false;
            if (testCode.decode()) {
                decoded = true;
                for (int i = 0; i < code.getBlockLength(); ++i) {
                    if (testCode.getAt(i) != code.getAt(i)) {
                        decoded = false;
                        break;
                    }
                }
            }
            System.out.printf("    errors: %d  %s%n", errors, decoded ? "OK" : "FAIL");
            if (decoded) {
                maxDecoded = Math.max(maxDecoded, errors);
            }
            else {
                minFailed = Math.min(minFailed, errors);
            }
        }
        System.out.printf("[min failed, max decoded]: [%d (%d%%), %d (%d%%)]%n",
                minFailed,
                (minFailed * 100 + redundancy / 2) / redundancy,
                maxDecoded,
                (maxDecoded * 100 + redundancy / 2) / redundancy);
    }
}
