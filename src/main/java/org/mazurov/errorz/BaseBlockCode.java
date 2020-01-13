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

public abstract class BaseBlockCode implements BlockCode {

    protected int N, K;
    protected long[] X;
    protected int offset, step;

    protected BaseBlockCode() {}

    protected BaseBlockCode(int n, int k, long[] x, int offset, int step) {
        N = n;
        K = k;
        X = x;
        this.offset = offset;
        this.step = step;

        if (X == null) {
            if (offset != 0 || step != 1) {
                throw new IllegalArgumentException("Parameters not consistent: x == null && (offset != 0 || step != 1)");
            }

            // Create a new code word and initialize
            // the first K elements with random values
            X = new long[N];
            for (int i = 0; i < K; ++i) {
                X[i] = Random.nextLong();
            }
        }
    }

    @Override
    public int getBlockLength() {
        return N;
    }

    @Override
    public int getMessageLength() {
        return K;
    }

    @Override
    public long getAt(int i) {
        return X[IDX(i)];
    }

    @Override
    public void setAt(int i, long val) {
        X[IDX(i)] = val;
    }

    @Override
    public abstract BaseBlockCode clone();

    /**
     * Code locator access methods for synthetic codes
     * @param i code locator index
     * @return code locator at index @i
     */
    protected abstract long getLocator(int i);

    /**
     * Maps logical index to physical
     * @param i logical index
     * @return physical index
     */
    protected int IDX(int i) {
        return offset + i * step;
    }

    /**
     * Virtual constructor
     * @param n block length
     * @param k message length
     * @param x array containing the code word
     * @param offset offset of the first element in the array
     * @param step distance between code word elements in the array
     * @return a new instance of the same type as the original code word
     */
    public abstract BaseBlockCode newInstance(int n, int k, long[] x, int offset, int step);

    /**
     * Encode the code word by fixing erasures at K .. N-1
     */
    @Override
    public void encode() {
        int[] idx = new int[N - K];
        for (int i = 0; i < idx.length; ++i) {
            idx[i] = K + i;
        }
        decode(idx);
    }
}
