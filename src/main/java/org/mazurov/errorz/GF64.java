/*
 * Copyright 2017,2019 Oleg Mazurov
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

public class GF64 {

    private static final long ROOT = 27l;
    private static final long MSBIT = 0x8000000000000000l;

    public static final long ZERO = 0;
    public static final long UNIT = 1;
    public static final long ALPHA = 2;

    /**
     * Galois field multiplication
     * @param a field element
     * @param b field element
     * @return {@code a * b}
     */
    public static long GFmul(long a, long b) {
        long res = 0;
        while (b != 0) {
            if ((b & 1) == 1) {
                res ^= a;
            }
            b >>>= 1;
            if ((a & MSBIT) != 0l) {
                a = (a << 1) ^ ROOT;
            }
            else {
                a = (a << 1);
            }
        }
        return res;
    }

    /**
     * Galois field power function
     * @param a field element
     * @param exp exponent to which {@code a} is to be raised
     * @return {@code a ^ exp}
     */
    public static long GFpow(long a, long exp) {
        long bit = Long.highestOneBit(exp);
        long res = 1;
        while (bit != 0) {
            res = GFmul(res, res);
            if ((exp & bit) != 0) {
                res = GFmul(res, a);
            }
            bit = bit >>> 1;
        }
        return res;
    }

    /**
     * Galois field division
     * Using the extended Euclid algorithm simultaneously multiplying by {@code a}
     * @param a
     * @param b
     * @return {@code a / b}
     */
    public static long GFdiv(long a, long b) {
        if (b == 0) throw new IllegalArgumentException("division by zero");
        long m = MSBIT;
        long p = b;
        long vp = a;
        long q = p;
        long vq = vp;
        boolean done = false;
        while (!done) {
            done = (q & m) != 0;
            q <<= 1;
            if ((vq & MSBIT) != 0) vq = vq << 1 ^ ROOT;
            else vq <<= 1;
        }
        q ^= ROOT;

        while (p != 1) {
            for (;;) {
                if ((p & m) != 0) break;
                else if ((q & m) != 0) {
                    long t = p; p = q; q = t;
                    t = vp; vp = vq; vq = t;
                    break;
                }
                m >>>= 1;
            }
            long r = q;
            long vr = vq;
            while ((r & m) == 0) {
                r <<= 1;
                if ((vr & MSBIT) != 0) vr = vr << 1 ^ ROOT;
                else vr <<= 1;
            }
            p ^= r;
            vp ^= vr;
        }
        return vp;
    }

    /**
     * Galois field reciprocal
     * @param a field element
     * @return {@code 1 / a}
     */
    public static long GFrev(long a) {
        return GFdiv(UNIT, a);
    }

    /**
     * Galois field addition
     * @param a field element
     * @param b field element
     * @return {@code a + b}
     */
    public static long GFadd(long a, long b) {
        return a ^ b;
    }

    /**
     * Galois field subtraction
     * @param a field element
     * @param b field element
     * @return {@code a - b}
     */
    public static long GFsub(long a, long b) {
        return a ^ b;
    }
}
