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

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Project ErrorZ
 *
 * https://github.com/OlegMazurov/ErrorZ
 *
 */

public class Random {

    private static long seed = Long.getLong("random.seed", 1L);
    private static SecureRandom rnd;
    static {
        try {
            rnd = SecureRandom.getInstance("SHA1PRNG");
            rnd.setSeed(seed);
        }
        catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static long nextLong() {
        return rnd.nextLong();
    }

    public static int nextInt(int bound) {
        return rnd.nextInt(bound);
    }
}
