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

public class GF64 {

    private static final long ROOT = 27l;
    private static final long MSBIT = 0x8000000000000000l;
    private static final long REVD = -2l;

    public static final long ZERO = 0;
    public static final long UNIT = 1;
    public static final long ALPHA = 2;

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

    public static long GFpow(long a, long b) {
        long bit = Long.highestOneBit(b);
        long res = 1;
        while (bit != 0) {
            res = GFmul(res, res);
            if ((b & bit) != 0) {
                res = GFmul(res, a);
            }
            bit = bit >>> 1;
        }
        return res;
    }

    public static long GFrev(long a) {
        if (a == 0) throw new IllegalArgumentException("division by zero");
        return GFpow(a, REVD);
    }

    public static long GFdiv(long a, long b) {
        return GFmul(a, GFrev(b));
    }

    public static long GFadd(long a, long b) {
        return a ^ b;
    }

    public static long GFsub(long a, long b) {
        return a ^ b;
    }
}
