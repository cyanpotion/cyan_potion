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
import com.xenoamess.cyan_potion.base.DataCenter;
import com.xenoamess.cyan_potion.base.memory.ResourceManager;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * SaveFileObject
 * SaveFileObject means a save file.
 * It is a save file from user sight,
 * but actually is a set of FileObjects in a folder.
 * we use several files here for backup,
 * so if user get save file broken we can get a easy repair plan for them.
 * basically path is the path of the folder, and it is with the last / .
 * path+status is a file that contains some infos of this save file.
 * other files with number name, they are json file containing data.
 *
 * @author XenoAmess
 * @version 0.158.1
 * @see SaveFileObjectStatus
 * @see SaveFileContent
 */
public class SaveFileObject {
    @JsonIgnore
    private static transient final Logger LOGGER =
            LoggerFactory.getLogger(SaveFileObject.class);

    /**
     * Constant <code>saveFileObjectNum=10</code>
     */
    public static final int SAVE_FILE_OBJECT_NUM = 10;

    private final SaveManager saveManager;
    private final String path;
    private SaveFileObjectStatus saveFileObjectStatus;

    /**
     * <p>initStatusFile.</p>
     */
    protected synchronized void initStatusFile() {
        FileObject fileObject = ResourceManager.resolveFile(path + "status");
        try {
            if (fileObject.exists()) {
                return;
            }
        } catch (FileSystemException e) {
            LOGGER.error("cannot create file : {}", fileObject, e);
        }

        try {
            fileObject.createFile();
            try (OutputStream outputStream = fileObject.getContent().getOutputStream()) {
                SaveFileObjectStatus newCreatedSaveFileObjectStatus = new SaveFileObjectStatus();
                newCreatedSaveFileObjectStatus.setNowIndex(-1);
                newCreatedSaveFileObjectStatus.setVersion(saveManager.getGameManager().getDataCenter().getGameSettings().getGameVersion());
                long time = System.currentTimeMillis();
                newCreatedSaveFileObjectStatus.setLastSaveTime(time);
                newCreatedSaveFileObjectStatus.setLastLoadTime(time);
                DataCenter.getObjectMapper().writeValue(outputStream, newCreatedSaveFileObjectStatus);
            }
        } catch (IOException e) {
            LOGGER.error("cannot create file : {}", fileObject, e);
        }
    }

    /**
     * <p>updateStatusFile.</p>
     */
    protected synchronized void updateStatusFile() {
        FileObject fileObject = ResourceManager.resolveFile(path + "status");
        try (OutputStream outputStream = fileObject.getContent().getOutputStream()) {
            DataCenter.getObjectMapper().writeValue(outputStream, this.getSaveFileObjectStatus());
        } catch (IOException e) {
            LOGGER.error("cannot save status file : {}", fileObject, e);
        }
    }

    /**
     * build a SaveFileObject
     * if path + "status" does not exist, then create it as
     * {version:dataCenter.gameVersion,lastSaveTime:now,lastLoadTime:now,nowIndex:-1}
     * then load from path + "status".
     * then set path + "status"'s lastLoadTime be now
     * (this step will not thange the loaded saveFileObjectStatus in memory, just change the file on disk.)
     *
     * @param saveManager saveManager
     * @param path        path
     */
    public SaveFileObject(SaveManager saveManager, String path) {
        this.saveManager = saveManager;
        this.path = path;
        FileObject fileObject = ResourceManager.resolveFile(path + "status");
        this.initStatusFile();

        try (InputStream inputStream = fileObject.getContent().getInputStream()) {
            SaveFileObjectStatus loadedSaveFileObjectStatus = DataCenter.getObjectMapper().readValue(inputStream, SaveFileObjectStatus.class);
            this.setSaveFileObjectStatus(loadedSaveFileObjectStatus);
        } catch (IOException e) {
            LOGGER.error("cannot create file : {}", fileObject, e);
        }
        try (OutputStream outputStream = fileObject.getContent().getOutputStream()) {
            SaveFileObjectStatus beSavedSaveFileObjectStatus = new SaveFileObjectStatus(this.getSaveFileObjectStatus());
            beSavedSaveFileObjectStatus.setLastLoadTime(System.currentTimeMillis());
            DataCenter.getObjectMapper().writeValue(outputStream, beSavedSaveFileObjectStatus);
        } catch (IOException e) {
            LOGGER.error("cannot create file : {}", fileObject, e);
        }
    }

    /**
     * load from current saveFileObjectStatus.getNowIndex()
     * notice that this function will not change saveFileObjectStatus.getNowIndex() after load.
     * please make sure the file [path + saveFileObjectStatus.getNowIndex()] really exist and really have saved something.
     * Otherwise bomb.
     *
     * @return loaded RuntimeVariableStructList
     */
    public synchronized List<RuntimeVariableStruct> load() {
        if (!this.exist()) {
            LOGGER.error("cannot load SaveFileContent : index==-1 means savefile is empty : {}", getPath());
            return new ArrayList<>();
        }
        FileObject fileObject = ResourceManager.resolveFile(getPath() + getSaveFileObjectStatus().getNowIndex());
        SaveFileContent res = null;
        try (InputStream inputStream = fileObject.getContent().getInputStream()) {
            res = DataCenter.getObjectMapper().readValue(inputStream, SaveFileContent.class);
        } catch (IOException e) {
            LOGGER.error("cannot load SaveFileContent from : {}", fileObject, e);
        }
        updateStatusFile();
        assert res != null;
        return res.getRuntimeVariableStructList();
    }


    /**
     * load to current saveFileObjectStatus.getNowIndex()+1
     * notice that we will first +1 aveFileObjectStatus.getNowIndex(), then save.
     * so we can leave some backups of save file.
     *
     * @param runtimeVariableStructList list of runtimeVariableStruct to save.
     */
    public synchronized void save(List<RuntimeVariableStruct> runtimeVariableStructList) {
        pickNextSaveFileObject();
        long time = System.currentTimeMillis();
        this.getSaveFileObjectStatus().setLastSaveTime(time);
        this.getSaveFileObjectStatus().setLastLoadTime(time);
        this.getSaveFileObjectStatus().setVersion(this.getSaveManager().getGameManager().getDataCenter().getGameSettings().getGameVersion());

        FileObject fileObject = ResourceManager.resolveFile(getPath() + getSaveFileObjectStatus().getNowIndex());
        SaveFileContent saveFileContent = new SaveFileContent();
        saveFileContent.getRuntimeVariableStructList().addAll(runtimeVariableStructList);
        try (OutputStream outputStream = fileObject.getContent().getOutputStream()) {
            DataCenter.getObjectMapper().writeValue(outputStream, saveFileContent);
        } catch (IOException e) {
            LOGGER.error("cannot save SaveFileContent to : {}", fileObject, e);
        }
        updateStatusFile();
    }

    /**
     * this.getNowIndex() += 1
     */
    protected synchronized void pickNextSaveFileObject() {
        int nextIndex = this.getNowIndex() + 1;
        if (nextIndex >= SAVE_FILE_OBJECT_NUM) {
            nextIndex %= SAVE_FILE_OBJECT_NUM;
        }
        setNowIndex(nextIndex);
    }

    /**
     * set now used fileObject index
     *
     * @return now index
     */
    protected int getNowIndex() {
        return this.getSaveFileObjectStatus().getNowIndex();
    }

    /**
     * set now used fileObject index
     *
     * @return now index
     */
    public boolean exist() {
        return this.getSaveFileObjectStatus().getNowIndex() != -1;
    }


    /**
     * get now used fileObject index
     *
     * @param index a int.
     */
    protected void setNowIndex(int index) {
        this.getSaveFileObjectStatus().setNowIndex(index);
    }

    /**
     * <p>Getter for the field <code>saveFileObjectStatus</code>.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.runtime.SaveFileObjectStatus} object.
     */
    protected SaveFileObjectStatus getSaveFileObjectStatus() {
        return saveFileObjectStatus;
    }

    /**
     * <p>Setter for the field <code>saveFileObjectStatus</code>.</p>
     *
     * @param saveFileObjectStatus a {@link com.xenoamess.cyan_potion.base.runtime.SaveFileObjectStatus} object.
     */
    protected void setSaveFileObjectStatus(SaveFileObjectStatus saveFileObjectStatus) {
        this.saveFileObjectStatus = saveFileObjectStatus;
    }

    /**
     * <p>Getter for the field <code>path</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    protected String getPath() {
        return path;
    }

    /**
     * <p>Getter for the field <code>saveManager</code>.</p>
     *
     * @return a {@link com.xenoamess.cyan_potion.base.runtime.SaveManager} object.
     */
    protected SaveManager getSaveManager() {
        return saveManager;
    }
}
