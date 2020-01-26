/*
 * MIT License
 *
 * Copyright (c) 2020 XenoAmess
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.xenoamess.cyan_potion.base.runtime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.xenoamess.cyan_potion.base.DataCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RuntimeVariableStruct {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(RuntimeManager.class);

    RuntimeVariableStruct() {

    }

    public void fill(RuntimeVariableStruct runtimeVariableStruct) {
        assert (runtimeVariableStruct != null);
        assert (runtimeVariableStruct.getClass().equals(this.getClass()));
        this.loadFromString(runtimeVariableStruct.saveToString());
    }

    public String saveToString() {
        String res = "";
        try {
            res = DataCenter.getObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            LOGGER.error("cannot toString", e);
        }
        return res;
    }

    public abstract void loadFromString(String string);

    @Override
    public String toString() {
        return this.saveToString();
    }
}
