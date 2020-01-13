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

import java.util.ArrayList;

import static org.mazurov.errorz.GF64.*;

/**
 * Project ErrorZ
 *
 * https://github.com/OlegMazurov/ErrorZ
 *
 */

public class Vandermonde extends BaseBlockCode {

    // This limit is set artificially low to allow for a reasonably sized
    // pre-computed set of locators
    private static final int MAXN = 256;

    // Locators
    private static final long[] Z;
    static {
        Z = new long[MAXN];
        Z[0] = ZERO;
        long z = UNIT;
        for (int i = 1; i < Z.length; ++i) {
            Z[i] = z;
            z = GFmul(z, ALPHA);
        }
    }

    /**
     * Create an empty code word
     */
    public Vandermonde() {}

    public Vandermonde(int k) {
        this(Z.length, k);
    }

    public Vandermonde(int n, int k) {
        this(n, k, null, 0, 1);
        encode();
    }

    /**
     * Create a sparse code word from an external array
     * @param n code length
     * @param k
     * @param x external array
     * @param offset first element
     * @param step next element
     */
    public Vandermonde(int n, int k, long[] x, int offset, int step) {
        super(n, k, x, offset, step);
        if (n > Z.length) throw new IllegalArgumentException("Parameter n=" + n + "exceeds " + Z.length);
    }

    /**
     * Virtual constructor
     * @return a new instance of this class
     */
    @Override
    public BaseBlockCode newInstance(int n, int k, long[] x, int offset, int step) {
        return new Vandermonde(n, k, x, offset, step);
    }

    @Override
    public BaseBlockCode clone() {
        return new Vandermonde(N, K, X.clone(), offset, step);
    }

    @Override
    public String toString() {
        String str = "Vandermonde-RS code";
        if (N > 0) {
            str += " (n,k)=(" + N + "," + K + ")";
        }
        return str;
    }

    public long getLocator(int i) {
        return Z[i];
    }

    /**
     * Fixes erasures
     * @param idx - array of erased indices
     */
    @Override
    public void decode(int[] idx) {
        // Zero the indicated values (erasures)
        for (int i=0; i<idx.length; ++i) {
            X[IDX(idx[i])] = ZERO;
        }

        // Reconstruct erased values
        long[] E = new long[idx.length];
        for (int i=0; i<idx.length; ++i) {
            long d = UNIT;
            for (int j = 0; j < idx.length; ++j) {
                if (j == i) continue;
                d = GFmul(d, GFsub(Z[idx[i]], Z[idx[j]]));
            }
            d = GFrev(d);

            long e = ZERO;
            for (int k = 0; k < N; ++k) {
                if (X[IDX(k)] == ZERO) continue;
                long v = 1;
                for (int j = 0; j < idx.length; ++j) {
                    if (j == i) continue;
                    v = GFmul(v, GFsub(Z[k], Z[idx[j]]));
                }
                e = GFadd(e, GFmul(X[IDX(k)], GFmul(v, d)));
            }
            E[i] = e;
        }

        // Restore erasures
        for (int i=0; i<idx.length; ++i) {
            X[IDX(idx[i])] = E[i];
        }
    }

    /**
     * getSyndromes
     * @return array of N - K syndromes
     */
    private long[] getSyndromes() {
        long[] S = new long[N - K];
        for (int i=0; i<N; ++i) {
            long v = X[IDX(i)];
            for (int j=0; j<S.length; ++j) {
                S[j] = GFadd(S[j], v);
                v = GFmul(v, Z[i]);
            }
        }
        return S;
    }

    /**
     * doGauss - solve linear equations
     * @param A - syndrome matrix
     * @return solution
     */
    private static long[] doGauss(long[][] A) {
        int e;
        for (e=0; e<A.length && e < A[e].length; ++e) {
            int i = e;
            for (int j=i; j < A.length && i < A[j].length; ++j) {
                if (A[j][i] != ZERO) {
                    if (j != i) {
                        long[] ta = A[j];
                        A[j] = A[i];
                        A[i] = ta;
                    }
                    break;
                }
            }
            if (A[i][i] == ZERO) {
                break;
            }
            long d = GFrev(A[i][i]);

            for (int j=i; j<A[i].length; ++j) {
                A[i][j] = GFmul(A[i][j], d);
            }

            for (int k=i+1; k<A.length && i<A[k].length; ++k) {
                long v = A[k][i];
                int maxj = Math.min(A[i].length, A[k].length);
                for (int j=i; j<maxj; ++j) {
                    A[k][j] = GFadd(A[k][j], GFmul(A[i][j], v));
                }
            }
        }

        long[] res = new long[e+1];
        res[e] = UNIT;
        for (int i=e-1; i>=0; --i) {
            if (A[i].length <= e) return null;
            long v = ZERO;
            for (int j = e; j>i; --j) {
                v = GFadd(v, GFmul(A[i][j], res[j]));
            }
            res[i] = v;
        }
        return res;
    }

    /**
     * findRoots - find all distinct roots of a polynomial
     * @param P - polynomial
     * @return array of found distinct roots
     */
    private static int[] findRoots(long[] P) {
        ArrayList<Integer> idx = new ArrayList<>();
        for (int i = 0; i < Z.length; ++i) {
            long v = 0;
            for (int j = P.length-1; j >= 0; --j) {
                v = GFadd(GFmul(v, Z[i]), P[j]);
            }
            if (v == 0) {
                idx.add(i);
            }
        }
        int[] res = new int[idx.size()];
        for (int i=0; i<res.length; ++i) {
            res[i] = idx.get(i);
        }
        return res;
    }

    @Override
    public boolean decode() {
        long[] S = getSyndromes();

        // Construct the syndrome matrix
        int m = N - K;
        long[][] A = new long[m - m/2][];
        for (int i = 0; i < A.length; ++i) {
            A[i] = new long[m-i];
            for (int j = 0; j < A[i].length; ++j) {
                A[i][j] = S[i+j];
            }
        }

        long[] P = doGauss(A);
        if (P == null) return false;

        int[] idx = findRoots(P);
        if (idx.length != P.length - 1) return false;
        decode(idx);

        return true;
    }
}
