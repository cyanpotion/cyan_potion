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

/**
 * SaveManager
 * A manager class for saving and loading, and other save file operations.
 *
 * @author XenoAmess
 * @version 0.159.0
 */
public class SaveManager {
    /**
     * Constant <code>DEFAULT_SAVE_FILE_PATH="System.getProperty(user.home) + /AppData/Roaming/cyan_potion_saves/"</code>
     */
    public static final String DEFAULT_SAVE_FILE_PATH = System.getProperty("user.home") + "/AppData/Roaming/cyan_potion_saves/";

    private String currentSaveFilePath = DEFAULT_SAVE_FILE_PATH;

    private final GameManager gameManager;

    /**
     * <p>Constructor for SaveManager.</p>
     *
     * @param gameManager a {@link com.xenoamess.cyan_potion.base.GameManager} object.
     */
    public SaveManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    /**
     * <p>Constructor for SaveManager.</p>
     *
     * @param gameManager         a {@link com.xenoamess.cyan_potion.base.GameManager} object.
     * @param currentSaveFilePath a {@link java.lang.String} object.
     */
    public SaveManager(GameManager gameManager, String currentSaveFilePath) {
        this(gameManager);
        this.currentSaveFilePath = currentSaveFilePath;
    }

    private SaveFileObject currentSaveFileObject;

    /**
     * get SaveFileObject
     *
     * @param index save file object index
     * @return a {@link com.xenoamess.cyan_potion.base.runtime.SaveFileObject} object.
     */
    public SaveFileObject getSaveFileObject(int index) {
        String saveFolderPath = currentSaveFilePath + this.getGameManager().getDataCenter().getGameSettings().getGameName() + "/" + index + "/";
        SaveFileObject saveFileObject = new SaveFileObject(this, saveFolderPath);
        return saveFileObject;
    }

    /**
     * pick save file object 0 at start.
     * some games only have 1 save slot,
     * and this mechanism can make their life easier.
     */
    public void init() {
        this.pickCurrentSaveFileObject(0);
    }

    /**
     * get a SaveFileObject using index,
     * then set it as default SaveFileObject
     * then return it.
     * <p>
     * this shall be invoked in a save GUI or something,
     * when you click a save slot,
     * then you invoke this,
     * then you call save to save.
     *
     * @param index save file object index
     * @return a {@link com.xenoamess.cyan_potion.base.runtime.SaveFileObject} object.
     */
    public SaveFileObject pickCurrentSaveFileObject(int index) {
        SaveFileObject result = getSaveFileObject(index);
        setCurrentSaveFileObject(result);
        return result;
    }

    /**
     * get current save file object
     *
     * @return a {@link com.xenoamess.cyan_potion.base.runtime.SaveFileObject} object.
     */
    public SaveFileObject getCurrentSaveFileObject() {
        return currentSaveFileObject;
    }

    /**
     * set current save file object.
     *
     * @param currentSaveFileObject a {@link com.xenoamess.cyan_potion.base.runtime.SaveFileObject} object.
     */
    protected void setCurrentSaveFileObject(SaveFileObject currentSaveFileObject) {
        this.currentSaveFileObject = currentSaveFileObject;
    }

    /**
     * <p>Getter for the field <code>gameManager</code>.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.GameManager} object.
     */
    public GameManager getGameManager() {
        return gameManager;
    }

    /**
     * <p>Getter for the field <code>currentSaveFilePath</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getCurrentSaveFilePath() {
        return currentSaveFilePath;
    }

    /**
     * <p>Setter for the field <code>currentSaveFilePath</code>.</p>
     *
     * @param currentSaveFilePath a {@link java.lang.String} object.
     */
    public void setCurrentSaveFilePath(String currentSaveFilePath) {
        this.currentSaveFilePath = currentSaveFilePath;
    }
}
