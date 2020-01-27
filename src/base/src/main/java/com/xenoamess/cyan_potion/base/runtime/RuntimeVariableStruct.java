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

/**
 * RuntimeVariableStruct
 * RuntimeVariableStruct is some struct that stores game datas.
 * RuntimeVariableStruct is used for storing runtime,
 * like variables, or characters, or equipments, or generated map,
 * or something else.
 * The common use case is you create some RuntimeVariableStruct in somewhere,
 * make it final(or as-final), then register it into RuntimeManager.
 * and then change value in it while the game play.
 * and call functions in RuntimeManager to save/load.
 * <p>
 * usually I suggest one game own one RuntimeVariableStruct at first,
 * because migrate for several RuntimeVariableStruct is really really hard and boring.
 * and several RuntimeVariableStructs are acceptable,
 * if you wanna add a whole new module like dlc or something you can add it.
 *
 * @author XenoAmess
 * @version 0.148.8
 */
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
     * @param string json string
     */
    public void fill(String string) {
        this.fill(loadFromString(string));
    }

    /**
     * get an RuntimeVariableStruct object from a string.
     *
     * @param string      json string
     * @param classObject class of the RuntimeVariableStruct
     * @return a {@link java.lang.Object} object.
     */
    public static <T extends RuntimeVariableStruct> T loadFromString(String string, Class<T> classObject) {
        T res = null;
        try {
            res = DataCenter.getObjectMapper().readValue(string, classObject);
        } catch (JsonProcessingException e) {
            LOGGER.error("cannot loadFromString : string {}", string, e);
        }
        return res;
    }

    /**
     * save a RuntimeVariableStruct to a String
     *
     * @param runtimeVariableStruct the struct you wanna save.
     * @return json string
     */
    public static <T extends RuntimeVariableStruct> String saveToString(T runtimeVariableStruct) {
        String res = "";
        if (runtimeVariableStruct == null) {
            return res;
        }
        try {
            res = DataCenter.getObjectMapper().writeValueAsString(runtimeVariableStruct);
        } catch (JsonProcessingException e) {
            LOGGER.error("cannot saveToString : an object of class {}", runtimeVariableStruct.getClass(), e);
        }
        return res;
    }

    /**
     * save this object to a string
     * just for a shortcut
     *
     * @return json string
     * @see RuntimeVariableStruct#saveToString()
     */
    public String saveToString() {
        return RuntimeVariableStruct.saveToString(this);
    }

    /**
     * get an object from a string.
     * this function is designed not to be a static function
     * because static function cannot inherit.
     * you can use new XXX().loadFromString(string) to make it generate a XXX class.
     * just for a shortcut.
     *
     * @param string json string
     * @return the loaded RuntimeVariableStruct
     * @see RuntimeVariableStruct#loadFromString(String, Class)
     */
    public RuntimeVariableStruct loadFromString(String string) {
        return RuntimeVariableStruct.loadFromString(string, this.getClass());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.saveToString();
    }
}
