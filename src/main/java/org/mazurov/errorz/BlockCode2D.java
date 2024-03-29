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

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * Project ErrorZ
 *
 * https://github.com/OlegMazurov/ErrorZ
 *
 */

public class BlockCode2D implements BlockCode {

    private final BaseBlockCode baseCode;
    private final int NR;
    private final int NC;
    private final int KR;
    private final int KC;
    private final long[] X;
    private final BaseBlockCode[] rows;
    private final BaseBlockCode[] cols;

    /**
     * Constructs a 2-dimensional code word
     * @param n1 length of a row (number of columns)
     * @param k1 number of data symbols in rows
     * @param n2 length of a column (number of rows)
     * @param k2 number of data symbols in columns
     * @param base - base block code
     */
    public BlockCode2D(int n1, int k1, int n2, int k2, BaseBlockCode base) {
        baseCode = base;

        // Initialize the entire array even if we recompute
        // redundant symbols in the next two steps
        X = new long[n1 * n2];
        for (int i = 0; i < X.length; ++i) {
            X[i] = Random.nextLong();
        }

        // Define each row as a base code word.
        // Last NC - KC rows will be recomputed below.
        NR = n1;
        KR = k1;
        rows = new BaseBlockCode[n2];
        for (int r = 0; r < rows.length; ++r) {
            rows[r] = baseCode.newInstance(NR, KR, X, r * NR, 1);
        }

        // Define each column as a base code word.
        NC = n2;
        KC = k2;
        cols = new BaseBlockCode[n1];
        for (int c = 0; c < cols.length; ++c) {
            cols[c] = baseCode.newInstance(NC, KC, X, c, NR);
        }

        encode();
    }

    /**
     * Clone constructor
     * @param code - 2D block code
     */
    public BlockCode2D(BlockCode2D code) {
        X = code.X.clone();
        baseCode = code.baseCode;
        NR = code.NR;
        KR = code.KR;
        NC = code.NC;
        KC = code.KC;
        rows = code.rows.clone();
        for (int r = 0; r < rows.length; ++r) {
            rows[r] = baseCode.newInstance(NR, KR, X, r * NR, 1);
        }
        cols = code.cols.clone();
        for (int c = 0; c < cols.length; ++c) {
            cols[c] = baseCode.newInstance(NC, KC, X, c, NR);
        }
    }

    @Override
    public BlockCode clone() {
        return new BlockCode2D(this);
    }

    @Override
    public String toString() {
        return "2-dimensional [" + baseCode + "] (n,k)=(" + getBlockLength() + "," + getMessageLength() + ")=(" + NR + "," + KR + ")*(" + NC + "," + KC + ")";
    }

    @Override
    public int getBlockLength() {
        return NR * NC;
    }

    @Override
    public int getMessageLength() {
        return KR * KC;
    }

    @Override
    public long getAt(int i) {
        return X[i];
    }

    @Override
    public void setAt(int i, long val) {
        X[i] = val;
    }

    @Override
    public void encode() {
        Arrays.stream(rows).parallel().forEach(BaseBlockCode::encode);
        Arrays.stream(cols).parallel().forEach(BaseBlockCode::encode);
    }

    @Override
    public void decode(int[] idx) {
        throw new RuntimeException("Unimplemented");
    }

    private static int count(boolean[] bb) {
        int cnt = 0;
        for (boolean b : bb) cnt += b ? 1 : 0;
        return cnt;
    }

    /**
     * Iteratively fix errors working in both dimensions
     * @return {@code true} if fully decoded, {@code false} otherwise
     */
    @Override
    public boolean decode() {
        boolean[] fixedR = new boolean[rows.length];
        boolean[] fixedC = new boolean[cols.length];
        for (;;) {
            AtomicInteger progress = new AtomicInteger(0);
            IntStream.range(0, rows.length).parallel()
                    .filter(r -> !fixedR[r])
                    .forEach(r -> progress.getAndAdd((fixedR[r] = rows[r].decode()) ? 1 : 0));
            int cnt = count(fixedR);
            if (cnt == rows.length) return true;

            IntStream.range(0, cols.length).parallel()
                    .filter(c -> !fixedC[c])
                    .forEach(c -> progress.getAndAdd((fixedC[c] = cols[c].decode()) ? 1 : 0));
            cnt = count(fixedC);
            if (cnt == cols.length) return true;

            if (progress.get() == 0) break;
        }
        return false;
    }
}
