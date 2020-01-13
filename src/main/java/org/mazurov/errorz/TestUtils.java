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
        int decoded = nRuns - rejected - failed;
        StringBuilder sb = new StringBuilder();
        sb.append(code);
        sb.append(", redundancy: ").append(code.getBlockLength() - code.getMessageLength());
        sb.append(", errors: ").append(errors);
        sb.append(", runs: ").append(nRuns);
        sb.append(", decoded: " + decoded);
        sb.append(", rejected: ").append(rejected);
        sb.append(", failed: ").append(failed);
        System.out.println(sb);
        return decoded;
    }

    /**
     * Iteratively finds what number of errors has recovery probability .5
     * @param nRuns maximum number of iterations
     */
    public static void testErrors(BlockCode code, int nRuns) {
        System.out.println(code);
        int maxDecoded = 0;
        int minFailed = Integer.MAX_VALUE;
        int hi = code.getBlockLength() - code.getMessageLength();
        int lo = 0;
        for (int t = 0; t < nRuns; ++t) {
            BlockCode testCode = code.clone();
            int errors = (lo + hi)/2;
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
            System.out.println("    errors: " + errors + (decoded ? "  OK" : "  FAIL"));
            if (decoded) {
                maxDecoded = Math.max(maxDecoded, errors);
                lo = errors + 1;
                if (lo > hi) hi = lo;
            }
            else {
                minFailed = Math.min(minFailed, errors);
                hi = errors - 1;
                if (lo > hi) lo = hi;
            }
        }
        System.out.println("    [min failed, max decoded]: [" + minFailed +", " + maxDecoded + "]");
    }
}
