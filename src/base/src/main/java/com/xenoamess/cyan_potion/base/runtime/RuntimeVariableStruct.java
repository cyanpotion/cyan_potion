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

    /**
     * fill means copy all things from a object of same class to this.
     * assert (runtimeVariableStruct != null);
     * assert (runtimeVariableStruct.getClass().equals(this.getClass()));
     *
     * @param object the object you wanna copy from
     */
    public abstract void fill(Object object);

    /**
     * get object from string and fill this.
     *
     * @param string
     */
    public void fill(String string) {
        this.fill(loadFromString(string));
    }

    /**
     * save this object to a string
     *
     * @return
     */
    public String saveToString() {
        String res = "";
        try {
            res = DataCenter.getObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            LOGGER.error("cannot saveToString : an object of class {}", this.getClass(), e);
        }
        return res;
    }

    /**
     * get an object from a string.
     * this function is designed not to be a static function
     * because static function cannot inherit.
     *
     * @param string string
     * @return
     */
    public Object loadFromString(String string) {
        Object object = null;
        try {
            object = DataCenter.getObjectMapper().readValue(string, this.getClass());
        } catch (JsonProcessingException e) {
            LOGGER.error("cannot loadFromString : string {}", string, e);
        }
        return object;
    }

    @Override
    public String toString() {
        return this.saveToString();
    }
}
