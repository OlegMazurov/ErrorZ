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

import static org.mazurov.errorz.GF64.*;

/**
 * Project ErrorZ
 *
 * https://github.com/OlegMazurov/ErrorZ
 *
 */

public class Lagrange implements BlockCode {

    private BaseBlockCode delegate; // Delegate block code
    private long[] coeff;   // Conversion coefficients

    private Lagrange() {}

    public Lagrange(int n, int k, BaseBlockCode base) {
        this(n, k, null, 0, 1, base);
        encode();
    }

    public Lagrange(int n, int k, long[] x, int offset, int step, BaseBlockCode base) {
        coeff = new long[n];
        for (int i = 0; i < coeff.length; ++i) {
            long val = UNIT;
            for (int j = 0; j < coeff.length; ++j) {
                if (j == i) continue;
                val = GFdiv(val, base.getLocator(i) ^ base.getLocator(j));
            }
            coeff[i] = val;
        }
        delegate = base.newInstance(n, k, x, offset, step);
    }

    @Override
    public BlockCode clone() {
        Lagrange clone = new Lagrange();
        clone.coeff = coeff;    // read only, no need to compute again
        clone.delegate = delegate.clone();
        return clone;
    }

    @Override
    public String toString() {
        return "Lagrange-RS code [" + delegate + ']';
    }

    @Override
    public int getBlockLength() {
        return delegate.getBlockLength();
    }

    @Override
    public int getMessageLength() {
        return delegate.getMessageLength();
    }

    @Override
    public long getAt(int i) {
        return delegate.getAt(i);
    }

    @Override
    public void setAt(int i, long val) {
        delegate.setAt(i, val);
    }

    /**
     * Encode using Lagrangian interpolation
     */
    @Override
    public void encode() {
        int N = delegate.getBlockLength();
        int K = delegate.getMessageLength();
        for (int i = K; i < N; ++i) {
            long X = ZERO;
            long Zi = delegate.getLocator(i);
            for (int j = 0; j < K; ++j) {
                long val = delegate.getAt(j);
                long Zj = delegate.getLocator(j);
                for (int k = 0; k < K; ++k) {
                    if (k == j) continue;
                    long Zk = delegate.getLocator(k);
                    val = GFdiv(GFmul(val, GFsub(Zi, Zk)), GFsub(Zj, Zk));
                }
                X ^= val;
            }
            delegate.setAt(i, X);
        }
    }

    @Override
    public void decode(int[] idx) {
        long[] X = new long[delegate.getBlockLength()];
        for (int i = 0; i < X.length; ++i) {
            X[i] = GFmul(delegate.getAt(i), coeff[i]);
        }
        BaseBlockCode tmp = delegate.newInstance(delegate.getBlockLength(), delegate.getMessageLength(), X, 0, 1);
        tmp.decode(idx);
        for (int i = 0; i < delegate.getBlockLength(); ++i) {
            delegate.setAt(i, GFdiv(tmp.getAt(i), coeff[i]));
        }
    }

    @Override
    public boolean decode() {
        long[] X = new long[delegate.getBlockLength()];
        for (int i = 0; i < X.length; ++i) {
            X[i] = GFmul(delegate.getAt(i), coeff[i]);
        }
        BaseBlockCode tmp = delegate.newInstance(delegate.getBlockLength(), delegate.getMessageLength(), X, 0, 1);
        if (!tmp.decode()) return false;
        for (int i = 0; i < delegate.getBlockLength(); ++i) {
            delegate.setAt(i, GFdiv(tmp.getAt(i), coeff[i]));
        }
        return true;
    }
}
