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

public class BlockCode2D extends BlockCode {

    private final BlockCode baseCode;
    int NR, NC, KR, KC;
    private BlockCode[] rows;
    private BlockCode[] cols;

    /**
     * Constructs a 2-dimensional code word
     * @param n1 length of a row (number of columns)
     * @param k1 number of data symbols in rows
     * @param n2 length of a column (number of rows)
     * @param k2 number of data symbols in columns
     * @param base - base block code
     */
    public BlockCode2D(int n1, int k1, int n2, int k2, BlockCode base) {
        baseCode = base;
        N = n1 * n2;
        K = k1 * k2;
        X = new long[N];

        // Initialize the entire array even if we recompute
        // redundant symbols in the next two steps
        for (int i = 0; i < X.length; ++i) {
            X[i] = Random.nextLong();
        }

        // Define each row as a base code word.
        // Last NC - KC rows will be recomputed below.
        NR = n1;
        KR = k1;
        rows = new BlockCode[n2];
        for (int r = 0; r < rows.length; ++r) {
            rows[r] = baseCode.newInstance(NR, KR, X, r * NR, 1, true);
        }

        // Define each column as a base code word.
        NC = n2;
        KC = k2;
        cols = new BlockCode[n1];
        for (int c = 0; c < cols.length; ++c) {
            cols[c] = baseCode.newInstance(NC, KC, X, c, NR, true);
        }
    }

    /**
     * Clone constructor
     * @param code - 2D block code
     */
    public BlockCode2D(BlockCode2D code) {
        X = code.X.clone();
        N = code.N;
        K = code.K;
        offset = code.offset;
        step = code.step;
        baseCode = code.baseCode;
        NR = code.NR;
        KR = code.KR;
        NC = code.NC;
        KC = code.KC;
        rows = code.rows.clone();
        for (int r = 0; r < rows.length; ++r) {
            rows[r] = baseCode.newInstance(NR, KR, X, r * NR, 1, false);
        }
        cols = code.cols.clone();
        for (int c = 0; c < cols.length; ++c) {
            cols[c] = baseCode.newInstance(NC, KC, X, c, NR, false);
        }
    }

    @Override
    public BlockCode newInstance(int n, int k, long[] x, int offset, int step, boolean fix) {
        throw new RuntimeException("Unimplemented");
    }

    @Override
    public BlockCode clone() {
        return new BlockCode2D(this);
    }

    @Override
    public String toString() {
        return "2-dimensional " + baseCode + " (n,k)=(" + N + "," + K + ")=(" + NR + "," + KR + ")*(" + NC + "," + KC + ")";
    }

    @Override
    public void fixErasures(int[] idx) {
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
    public boolean fixErrors() {
        boolean[] fixedR = new boolean[rows.length];
        boolean[] fixedC = new boolean[cols.length];
        for(;;) {
            int nrows = 0;
            for (int r=0; r<rows.length; ++r) {
                if (fixedR[r]) continue;
                if (rows[r].fixErrors()) {
                    fixedR[r] = true;
                    nrows += 1;
                }
            }
            int cnt = count(fixedR);
            if (cnt == rows.length) return true;

            int ncols = 0;
            for (int c = 0; c < cols.length; ++c) {
                if (fixedC[c]) continue;
                if (cols[c].fixErrors()) {
                    fixedC[c] = true;
                    ncols += 1;
                }
            }
            cnt = count(fixedC);

            if (cnt == cols.length) return true;

            if (nrows == 0 && ncols == 0) break;
        }
        return false;
    }
}
