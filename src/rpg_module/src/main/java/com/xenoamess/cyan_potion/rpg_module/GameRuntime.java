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

package com.xenoamess.cyan_potion.rpg_module;

import com.xenoamess.commons.primitive.collections.lists.array_lists.BooleanArrayList;
import com.xenoamess.commons.primitive.collections.lists.array_lists.IntArrayList;
import com.xenoamess.cyan_potion.base.DataCenter;

/**
 * <p>GameRuntime class.</p>
 *
 * @author XenoAmess
 * @version 0.143.0
 */
public class GameRuntime {
    private DataCenter dataCenter;

    private final BooleanArrayList runtimeSwitches = new BooleanArrayList();
    private final IntArrayList runtimeIntegerVariables = new IntArrayList();


    //TODO maybe it would be better to write an Item class and an Actor class to do this. emmmmmmm

    /**
     * <p>Constructor for GameRuntime.</p>
     *
     * @param dataCenter dataCenter
     */
    public GameRuntime(DataCenter dataCenter) {
        this.setDataCenter(dataCenter);
    }


    /**
     * <p>saveGameRuntime.</p>
     */
    public void saveGameRuntime() {
        //TODO
    }

    /**
     * <p>loadGameRuntime.</p>
     */
    public void loadGameRuntime() {
        //TODO
    }

    /**
     * <p>Getter for the field <code>dataCenter</code>.</p>
     *
     * @return return
     */
    public DataCenter getDataCenter() {
        return dataCenter;
    }

    /**
     * <p>Setter for the field <code>dataCenter</code>.</p>
     *
     * @param dataCenter dataCenter
     */
    public void setDataCenter(DataCenter dataCenter) {
        this.dataCenter = dataCenter;
    }

    /**
     * <p>Getter for the field <code>runtimeSwitches</code>.</p>
     *
     * @return return
     */
    public BooleanArrayList getRuntimeSwitches() {
        return runtimeSwitches;
    }

    /**
     * <p>Getter for the field <code>runtimeIntegerVariables</code>.</p>
     *
     * @return return
     */
    public IntArrayList getRuntimeIntegerVariables() {
        return runtimeIntegerVariables;
    }
}
