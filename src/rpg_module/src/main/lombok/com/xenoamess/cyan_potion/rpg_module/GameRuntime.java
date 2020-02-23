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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xenoamess.commons.primitive.collections.lists.array_lists.BooleanArrayList;
import com.xenoamess.commons.primitive.collections.lists.array_lists.IntArrayList;
import com.xenoamess.cyan_potion.base.DataCenter;
import com.xenoamess.cyan_potion.base.runtime.RuntimeVariableStruct;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>GameRuntime class.</p>
 *
 * @author XenoAmess
 * @version 0.161.1
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public class GameRuntime extends RuntimeVariableStruct {

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Getter
    @Setter
    @JsonIgnore
    private transient DataCenter dataCenter;

    @Getter
    private final BooleanArrayList runtimeSwitches = new BooleanArrayList();

    @Getter
    private final IntArrayList runtimeIntegerVariables = new IntArrayList();

    /**
     * <p>Constructor for GameRuntime.</p>
     */
    public GameRuntime() {
        super();
    }

    /**
     * <p>Constructor for GameRuntime.</p>
     *
     * @param dataCenter dataCenter
     */
    public GameRuntime(DataCenter dataCenter) {
        this();
        this.setDataCenter(dataCenter);
        this.registerTo(dataCenter.getGameManager().getRuntimeManager());
    }


    /**
     * why don't you call RuntimeManager.save() directly?
     *
     * @deprecated
     */
    @Deprecated
    public void saveGameRuntime() {
        this.getDataCenter().getGameManager().getRuntimeManager().save();
    }

    /**
     * why don't you call RuntimeManager.load() directly?
     *
     * @deprecated
     */
    @Deprecated
    public void loadGameRuntime() {
        this.getDataCenter().getGameManager().getRuntimeManager().load();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFrom(Object object) {
        GameRuntime gameRuntime = (GameRuntime) object;
        this.getRuntimeIntegerVariables().clear();
        this.getRuntimeIntegerVariables().addAll(gameRuntime.getRuntimeIntegerVariables());
        this.getRuntimeSwitches().clear();
        this.getRuntimeSwitches().addAll(gameRuntime.getRuntimeSwitches());
    }
}
