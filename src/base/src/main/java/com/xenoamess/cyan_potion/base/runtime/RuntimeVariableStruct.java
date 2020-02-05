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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.xenoamess.cyan_potion.base.DataCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

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
 * <p>
 * RuntimeVariableStruct must HAVE a empty constructor.
 *
 * @author XenoAmess
 * @version 0.148.8
 */
public abstract class RuntimeVariableStruct implements Serializable {
    @JsonIgnore
    private static transient final Logger LOGGER =
            LoggerFactory.getLogger(RuntimeVariableStruct.class);

    /**
     * <p>Constructor for RuntimeVariableStruct.</p>
     */
    public RuntimeVariableStruct() {
        //do nothing
    }

    /**
     * register this to a runtimeManager
     * shortcut of RuntimeManager.registerRuntimeVariableStruct(RuntimeVariableStruct)
     *
     * @param runtimeManager the runtimeManager to register to
     * @see RuntimeManager#registerRuntimeVariableStruct(RuntimeVariableStruct)
     */
    public void registerTo(RuntimeManager runtimeManager) {
        runtimeManager.registerRuntimeVariableStruct(this);
    }

    /**
     * loadFrom means copy all things from a object of same class to this.
     * assert (runtimeVariableStruct != null);
     * assert (runtimeVariableStruct.getClass().equals(this.getClass()));
     *
     * @param object the object you wanna copy from
     */
    public abstract void loadFrom(Object object);

    /**
     * get object from string and fill this.
     *
     * @param string json string
     */
    public void loadFrom(String string) {
        this.loadFrom(RuntimeVariableStruct.loadFromString(string, this.getClass()));
    }

    /**
     * get an RuntimeVariableStruct object from a string.
     *
     * @param string      json string
     * @param classObject class of the RuntimeVariableStruct
     * @param <T>         class of the RuntimeVariableStruct
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
     * @param <T>                   class of the RuntimeVariableStruct
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
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.saveToString();
    }
}
