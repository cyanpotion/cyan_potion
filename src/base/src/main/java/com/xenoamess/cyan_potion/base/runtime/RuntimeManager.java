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

import com.xenoamess.cyan_potion.base.GameManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class RuntimeManager {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(RuntimeManager.class);

    private final GameManager gameManager;

    private final List<RuntimeVariableStruct> runtimeVariableStructList = new ArrayList<>();

    /**
     * <p>Constructor for DataCenter.</p>
     *
     * @param gameManager gameManager
     */
    public RuntimeManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    /**
     * register a RuntimeVariableStruct to RuntimeManager
     * notice that we will not change the RuntimeVariableStruct instances in runtimeVariableStructList.
     * so the common use is first you build a RuntimeVariableStruct / RuntimeVariableStructs at somewhere,
     * then you register it / them into RuntimeManager here.
     * then you invoke save / load,
     * and the data in objects in runtimeVariableStructList changes(but the objects will not add / remove)
     * that is the basic logic of this this functions & this class.
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
     * @param index
     */
    public void load(int index) {
        List<RuntimeVariableStruct> runtimeVariableStructList = gameManager.getSaveManager().pickCurrentSaveFileObject(index).load();
        for (int i = 0; i < runtimeVariableStructList.size(); i++) {
            this.runtimeVariableStructList.get(i).fill(runtimeVariableStructList.get(i));
        }
    }

    /**
     * load from the current SaveFileObject.
     */
    public void load() {
        List<RuntimeVariableStruct> runtimeVariableStructList = gameManager.getSaveManager().getCurrentSaveFileObject().load();
        for (int i = 0; i < runtimeVariableStructList.size(); i++) {
            this.runtimeVariableStructList.get(i).fill(runtimeVariableStructList.get(i));
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
     * @param index
     */
    public void save(int index) {
        gameManager.getSaveManager().pickCurrentSaveFileObject(index).save(this.runtimeVariableStructList);
    }

    /**
     * save into the current SaveFileObject.
     */
    public void save() {
        gameManager.getSaveManager().getCurrentSaveFileObject().save(this.runtimeVariableStructList);
    }

}