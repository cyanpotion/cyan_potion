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

public class SaveManager {
    private GameManager gameManager;

    public SaveManager(GameManager gameManager) {
        this.setGameManager(gameManager);
    }

    private static final int saveFileMaxNum = 1000;

    private SaveFileObject currentSaveFileObject;

    public SaveFileObject getSaveFileObject(int index) {
        String saveFolderPath = System.getProperty("user.home") + "/cyan_potion/" + this.getGameManager().getDataCenter().getGameName() + "/" + index + "/";
        SaveFileObject saveFileObject = new SaveFileObject(this, saveFolderPath);
        return saveFileObject;
    }

    public void init() {
        this.pickCurrentSaveFileObject(0);
    }

    public SaveFileObject pickCurrentSaveFileObject(int index) {
        SaveFileObject result = getSaveFileObject(index);
        setCurrentSaveFileObject(result);
        return result;
    }

    public SaveFileObject getCurrentSaveFileObject() {
        return currentSaveFileObject;
    }

    public void setCurrentSaveFileObject(SaveFileObject currentSaveFileObject) {
        this.currentSaveFileObject = currentSaveFileObject;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }
}
