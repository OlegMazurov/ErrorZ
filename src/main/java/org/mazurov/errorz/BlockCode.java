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

/**
 * Project ErrorZ
 *
 * https://github.com/OlegMazurov/ErrorZ
 *
 */

public interface BlockCode {

    /**
     * getBlockLength
     * @return code block length, n
     */
    int getBlockLength();

    /**
     * getMessageLength
     * @return code message length, k
     */
    int getMessageLength();

    /**
     * Encode the code word
     */
    void encode();

    /**
     * Fix erasures at locations provided in {@code idx[]}
     * @param idx
     */
    void decode(int[] idx);

    /**
     * Fix errors
     * @return true if the code word has been successfully decoded
     */
    boolean decode();

    /**
     * Clone the current state of the code word
     * @return a full copy of the code word with no shared state with the original
     */
    BlockCode clone();

    /**
     * Get code word element at index @{code i};  0 <= @{code i} < getBlockLength()
     * @param i index
     * @return code word element at index @{code i}
     */
    long getAt(int i);

    /**
     * Set code word element at index @{code i} to {@code val}
     * @param i
     * @param val
     */
    void setAt(int i, long val);
}
