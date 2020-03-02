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
import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.SubManager;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * RuntimeManager
 * A manager class for holding runtimeVariableStructList of RuntimeVariableStructs,
 * and load/save them
 * (invoking functions in SaveManager).
 *
 * @author XenoAmess
 * @version 0.161.4
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public class RuntimeManager extends SubManager {
    @JsonIgnore
    private static final transient Logger LOGGER =
            LoggerFactory.getLogger(RuntimeManager.class);

    private final List<RuntimeVariableStruct> runtimeVariableStructList = new ArrayList<>();

    /**
     * <p>Constructor for DataCenter.</p>
     *
     * @param gameManager gameManager
     */
    public RuntimeManager(GameManager gameManager) {
        super(gameManager);
    }

    @Override
    public void init() {
        //do nothing
    }

    @Override
    public void update() {
        //do nothing
    }

    @Override
    public void close() {
        //do nothing
    }

    /**
     * register a RuntimeVariableStruct to RuntimeManager
     * notice that we will not change the RuntimeVariableStruct instances in runtimeVariableStructList.
     * so the common use is first you build a RuntimeVariableStruct / RuntimeVariableStructs at somewhere,
     * then you register it / them into RuntimeManager here.
     * then you invoke save / load,
     * and the data in objects in runtimeVariableStructList changes(but the objects will not add / remove)
     * that is the basic logic of this this functions (and this class).
     *
     * @param runtimeVariableStruct runtimeVariableStruct
     */
    public void registerRuntimeVariableStruct(RuntimeVariableStruct runtimeVariableStruct) {
        runtimeVariableStructList.add(runtimeVariableStruct);
    }

    /**
     * Load index'th save file.
     * Notice that you can always add new RuntimeVariableStruct to your game, as well as you make sure every of them are registered.
     * But:
     * 1. you can't change the order they add.
     * 2. you can't delete some of the RuntimeVariableStruct in runtimeVariableStructList
     * 3. all RuntimeVariableStructs MUST be added to runtimeVariableStructList before you load a save file.
     *
     * @param index a int.
     */
    public synchronized void load(int index) {
        List<RuntimeVariableStruct> loadedRuntimeVariableStructList = this.getGameManager().getSaveManager().pickCurrentSaveFileObject(index).load();
        for (int i = 0; i < loadedRuntimeVariableStructList.size(); i++) {
            this.runtimeVariableStructList.get(i).loadFrom(loadedRuntimeVariableStructList.get(i));
        }
    }

    /**
     * load from the current SaveFileObject.
     */
    public synchronized void load() {
        List<RuntimeVariableStruct> loadedRuntimeVariableStructList = this.getGameManager().getSaveManager().getCurrentSaveFileObject().load();
        for (int i = 0; i < loadedRuntimeVariableStructList.size(); i++) {
            this.runtimeVariableStructList.get(i).loadFrom(loadedRuntimeVariableStructList.get(i));
        }
    }


    /**
     * Save into index'th save file.
     * Notice that you can always add new RuntimeVariableStruct to your game, as well as you make sure every of them are registered.
     * But:
     * 1. you can't change the order they add.
     * 2. you can't delete some of the RuntimeVariableStruct in runtimeVariableStructList
     * 3. all RuntimeVariableStructs MUST be added to runtimeVariableStructList before you load a save file.
     *
     * @param index a int.
     */
    public synchronized void save(int index) {
        this.getGameManager().getSaveManager().pickCurrentSaveFileObject(index).save(this.runtimeVariableStructList);
    }

    /**
     * save into the current SaveFileObject.
     */
    public synchronized void save() {
        this.getGameManager().getSaveManager().getCurrentSaveFileObject().save(this.runtimeVariableStructList);
    }

}
