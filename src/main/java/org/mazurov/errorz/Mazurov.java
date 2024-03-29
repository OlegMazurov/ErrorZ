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

import static org.mazurov.errorz.GF64.*;

/**
 * Project ErrorZ
 *
 * https://github.com/OlegMazurov/ErrorZ
 *
 */

public class Mazurov extends BaseBlockCode {

    // Maximum block length = 2^(64/D)
    private static final int MAXN = 256;

    // GF(2^D) - subfield of 2^64, 64 = D*k
    private static final int  D = 8;

    // BETAD = (2^64 - 1)/(2^D - 1) = 2^(64 - D) + 2^(64 - 2D) + ... + 2^D + 1
    private static final long BETAD;
    static {
        long r = 0;
        for (int d = 0; d < 64; d += D) {
            r += 1L << d;
        }
        BETAD = r;
    }

    // Locators
    private static final long[] Z;
    static {
        Z = new long[Math.min(MAXN, 1<<D)];
        long beta = GFpow(ALPHA, BETAD);
        Z[0] = ZERO;
        long z = UNIT;
        for (int i = 1; i < Z.length; ++i) {
            Z[i] = z;
            z = GFmul(z, beta);
        }
    }

    /**
     * Create an empty code word
     */
    public Mazurov() {}

    /**
     * Create a random code word of maximum length
     * @param k message length
     */
    public Mazurov(int k) {
        this(Z.length, k);
    }

    /**
     * Create a random code word of length ${code n}
     * @param n block length
     * @param k message length
     */
    public Mazurov(int n, int k) {
        this(n, k, null, 0, 1);
        encode();
    }

    /**
     * Create a sparse code word from an external array
     * @param n code length
     * @param k message length
     * @param x external array
     * @param offset first element
     * @param step next element
     */
    public Mazurov(int n, int k, long[] x, int offset, int step) {
        super(n, k, x, offset, step);
        if (n > Z.length) throw new IllegalArgumentException("Parameter n=" + n + "exceeds " + Z.length);
    }

    /**
     * Virtual constructor
     * @return a new instance of this class
     */
    @Override
    public BaseBlockCode newInstance(int n, int k, long[] x, int offset, int step) {
        return new Mazurov(n, k, x, offset, step);
    }

    @Override
    public BaseBlockCode clone() {
        return new Mazurov(N, K, X.clone(), offset, step);
    }

    @Override
    public String toString() {
        String str = "Mazurov-RS code";
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
        // Make a local copy
        long[] XX = new long[N];
        for (int i = 0; i < N; ++i) {
            XX[i] = X[IDX(i)];
        }
        // Erase indicated values
        for (int i : idx) {
            XX[i] = ZERO;
        }

        // Reconstruct erased values
        long[] E = new long[idx.length];
        for (int i=0; i<idx.length; ++i) {
            long d = UNIT;
            for (int j = 0; j < idx.length; ++j) {
                if (j == i) continue;
                d = GFmul(d, GFsub(Z[idx[i]], Z[idx[j]])); // d *= Z[idx[i]] - Z[idx[j]]
            }
            d = GFrev(d); // d = 1/d

            long e = ZERO;
            for (int k = 0; k < N; ++k) {
                if (XX[k] == ZERO) continue;
                long v = 1;
                for (int j = 0; j < idx.length; ++j) {
                    if (j == i) continue;
                    v = GFmul(v, GFsub(Z[k], Z[idx[j]])); // v *= Z[k] - Z[idx[j]]
                }
                e = GFadd(e, GFmul(XX[k], GFmul(v, d))); // e += X[IDX(k)] * v * d
            }
            E[i] = e;
        }

        // Restore erasures
        for (int i=0; i<idx.length; ++i) {
            X[IDX(idx[i])] = E[i];
        }
    }

    /**
     * Computes code word syndromes
     * @return array of N - K syndromes
     */
    private long[] getSyndromes(long[] XX) {
        long[] S = new long[N - K];
        for (int i = 0; i < N; ++i) {
            long v = XX[i];
            for (int j = 0; j < S.length; ++j) {
                S[j] = GFadd(S[j], v);
                v = GFmul(v, Z[i]);
            }
        }
        return S;
    }

    /**
     * Solves linear equations
     * @param A - syndrome matrix
     * @return solution
     */
    private static long[] doGauss(long[][] A) {
        int e;
        for (e = 0; e < A.length && e < A[e].length; ++e) {
            int i = e;
            for (int j = i; j < A.length && i < A[j].length; ++j) {
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

            for (int k = i + 1; k < A.length && i < A[k].length; ++k) {
                long v = A[k][i];
                int maxj = Math.min(A[i].length, A[k].length);
                for (int j = i; j < maxj; ++j) {
                    A[k][j] = GFadd(A[k][j], GFmul(A[i][j], v));
                }
            }
        }

        long[] res = new long[e+1];
        res[e] = UNIT;
        for (int i = e - 1; i >= 0; --i) {
            if (A[i].length <= e) return null;
            long v = ZERO;
            for (int j = e; j > i; --j) {
                v = GFadd(v, GFmul(A[i][j], res[j]));
            }
            res[i] = v;
        }
        return res;
    }

    /**
     * decode
     * @return true if successful
     */
    @Override
    public boolean decode() {
        // Make a local copy
        long[] XX = new long[N];
        for (int i = 0; i < N; ++i) {
            XX[i] = X[IDX(i)];
        }

        long[] S = getSyndromes(XX);

        // Construct the syndrome matrix
        int m = N - K;
        long[][] A = new long[(m - m/2)*D][];
        for (int i = 0; i < A.length; ++i) {
            int ii = i / D;
            int k = i % D;
            A[i] = new long[m - ii];
            for (int j = 0; j < A[i].length; ++j) {
                A[i][j] = k == 0 ? S[ii+j] : GFpow(A[i-1][j], 1<<D);
            }
        }

        long[] P = doGauss(A);
        if (P == null) return false;

        // Find roots and decode errors in one scan
        long[] P0 = new long[P.length - 1];
        int[] idx = new int[m];
        long[] val = new long[m];
        int ii = 0;
        for (int i = 0; i < N; ++i) {
            long z = Z[i];
            long r = P[P.length - 1]; // UNIT
            for (int j = P0.length - 1; j >= 0; --j) {
                P0[j] = r;
                r = GFadd(GFmul(r, z), P[j]);
            }
            if (r != ZERO) continue;

            long p = ZERO;
            long q = ZERO;
            for (int j = P0.length - 1; j >= 0; --j ) {
                p = GFadd(p, GFmul(P0[j], S[j]));
                q = GFadd(GFmul(q, z), P0[j]);
            }
            if (q == ZERO) {
                // Repeated root
                return false;
            }
            idx[ii] = IDX(i);
            val[ii] = GFadd(XX[i], GFdiv(p, q));
            ii += 1;
        }
        if (ii != P.length - 1) {
            // Too few roots
            return false;
        }

        for (int i = 0; i < ii; ++i) {
            X[idx[i]] = val[i];
        }

        return true;
    }

}
