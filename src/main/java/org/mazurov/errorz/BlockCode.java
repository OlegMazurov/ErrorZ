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

/**
 * Project ErrorZ
 *
 * https://github.com/OlegMazurov/ErrorZ
 *
 */

public abstract class BlockCode {

    protected int N, K;
    protected long[] X;
    protected int offset, step;

    public int getN() {
        return N;
    }

    public int getK() {
        return K;
    }

    public long[] getX() {
        return X;
    }

    /**
     * Maps logical index to physical
     * @param i logical index
     * @return physical index
     */
    protected int IDX(int i) {
        return offset + i * step;
    }

    /**
     * Generate exactly @E random errors in the code word
     * @param E number of errors
     * @return array of error indices
     */
    public int[] addErrors(int E) {
        int[] idx = new int[E];
        boolean[] marks = new boolean[X.length];
        for (int i = 0; i < E;) {
            int next = Random.nextInt(X.length);
            if (marks[next]) continue;
            marks[next] = true;
            X[next] = Random.nextLong();
            idx[i++] = next;
        }
        return idx;
    }

    /**
     * Test error recovery
     * @param nRuns    number of iterations
     * @param errors   number of errors
     */
    public int testErrors(int nRuns, int errors) {
        int rejected = 0;       // rejected decoding
        int failed = 0;         // erroneous decoding
        for (int t = 0; t < nRuns; ++t) {
            BlockCode testCode = this.clone();
            testCode.addErrors(errors);    // ignore error locations
            if (!testCode.fixErrors()) {
                rejected += 1;
            }
            else {
                long[] testX = testCode.getX();
                for (int i = 0; i < X.length; ++i) {
                    if (testX[i] != X[i]) {
                        failed += 1;
                        break;
                    }
                }
            }
        }
        int decoded = nRuns - rejected - failed;
        StringBuilder sb = new StringBuilder();
        sb.append(this);
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
    public void testErrors(int nRuns) {
        System.out.println(this);
        int maxDecoded = 0;
        int minFailed = Integer.MAX_VALUE;
        int hi = N - K;
        int lo = 0;
        for (int t = 0; t < nRuns; ++t) {
            BlockCode testCode = this.clone();
            int errors = (lo + hi)/2;
            testCode.addErrors(errors);
            boolean decoded = false;
            if (testCode.fixErrors()) {
                decoded = true;
                long[] testX = testCode.getX();
                for (int i = 0; i < X.length; ++i) {
                    if (testX[i] != X[i]) {
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


    public abstract BlockCode newInstance(int n, int k, long[] x, int offset, int step, boolean fix);
    public abstract BlockCode clone();
    public abstract void fixErasures(int[] idx);
    public abstract boolean fixErrors();
}
