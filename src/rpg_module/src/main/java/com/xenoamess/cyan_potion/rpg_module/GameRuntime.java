/*
 * MIT License
 *
 * Copyright (c) 2019 XenoAmess
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
 * @author XenoAmess
 */
public class GameRuntime {
    private DataCenter dataCenter;

    private final BooleanArrayList runtimeSwitches = new BooleanArrayList();
    private final IntArrayList runtimeIntegerVariables = new IntArrayList();


    //TODO
    //maybe it would be better to write an Item class and an Actor class to
    // do this.
    //emmmmmmm

    public GameRuntime(DataCenter dataCenter) {
        this.setDataCenter(dataCenter);
    }


    public void saveGameRuntime() {
        //TODO
    }

    public void loadGameRuntime() {
        //TODO
    }

    public DataCenter getDataCenter() {
        return dataCenter;
    }

    public void setDataCenter(DataCenter dataCenter) {
        this.dataCenter = dataCenter;
    }

    public BooleanArrayList getRuntimeSwitches() {
        return runtimeSwitches;
    }

    public IntArrayList getRuntimeIntegerVariables() {
        return runtimeIntegerVariables;
    }
}
