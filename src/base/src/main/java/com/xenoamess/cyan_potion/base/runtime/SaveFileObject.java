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

public class SaveFileObject {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(SaveFileObject.class);

    public static final int saveBackupNum = 10;

    private final SaveManager saveManager;
    private final String path;
    private SaveFileObjectStatus saveFileObjectStatus;

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
        try {
            fileObject.createFile();
        } catch (FileSystemException e) {
            LOGGER.error("cannot create file : {}", fileObject, e);
        }
        try {
            if (!fileObject.exists()) {
                try (OutputStream outputStream = fileObject.getContent().getOutputStream()) {
                    SaveFileObjectStatus saveFileObjectStatus = new SaveFileObjectStatus();
                    saveFileObjectStatus.setNowIndex(-1);
                    saveFileObjectStatus.setVersion(saveManager.getGameManager().getDataCenter().getGameVersion());
                    long time = System.currentTimeMillis();
                    saveFileObjectStatus.setLastSaveTime(time);
                    saveFileObjectStatus.setLastLoadTime(time);
                    DataCenter.getObjectMapper().writeValue(outputStream, saveFileObjectStatus);
                }
            }
        } catch (IOException e) {
            LOGGER.error("cannot create file : {}", fileObject, e);
        }
        try (InputStream inputStream = fileObject.getContent().getInputStream()) {
            SaveFileObjectStatus saveFileObjectStatus = DataCenter.getObjectMapper().readValue(inputStream, SaveFileObjectStatus.class);
            this.setSaveFileObjectStatus(saveFileObjectStatus);
        } catch (IOException e) {
            LOGGER.error("cannot create file : {}", fileObject, e);
        }
        try (OutputStream outputStream = fileObject.getContent().getOutputStream()) {
            SaveFileObjectStatus saveFileObjectStatus = new SaveFileObjectStatus(this.getSaveFileObjectStatus());
            saveFileObjectStatus.setLastLoadTime(System.currentTimeMillis());
            DataCenter.getObjectMapper().writeValue(outputStream, saveFileObjectStatus);
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
    public List<RuntimeVariableStruct> load() {
        if (this.getSaveFileObjectStatus().getNowIndex() == -1) {
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
        assert res != null;
        return res.getRuntimeVariableStructList();
    }


    /**
     * load to current saveFileObjectStatus.getNowIndex()+1
     * notice that we will first +1 aveFileObjectStatus.getNowIndex(), then save.
     * so we hold backups of savefile.
     */
    public void save(List<RuntimeVariableStruct> runtimeVariableStructList) {
        pickNextSaveBackup();
        long time = System.currentTimeMillis();
        this.getSaveFileObjectStatus().setLastSaveTime(time);
        this.getSaveFileObjectStatus().setLastLoadTime(time);
        this.getSaveFileObjectStatus().setVersion(this.getSaveManager().getGameManager().getDataCenter().getGameVersion());

        FileObject fileObject = ResourceManager.resolveFile(getPath() + getSaveFileObjectStatus().getNowIndex());
        SaveFileContent saveFileContent = new SaveFileContent();
        saveFileContent.getRuntimeVariableStructList().addAll(runtimeVariableStructList);
        try (OutputStream outputStream = fileObject.getContent().getOutputStream()) {
            DataCenter.getObjectMapper().writeValue(outputStream, saveFileContent);
        } catch (IOException e) {
            LOGGER.error("cannot save SaveFileContent to : {}", fileObject, e);
        }
    }

    /**
     * this.getSaveFileObjectStatus().getNowIndex() += 1
     */
    protected void pickNextSaveBackup() {
        int nextIndex = this.getSaveFileObjectStatus().getNowIndex() + 1;
        if (nextIndex >= saveBackupNum) {
            nextIndex %= saveBackupNum;
        }
        setSaveBackupIndex(nextIndex);
    }

    protected void setSaveBackupIndex(int index) {
        this.getSaveFileObjectStatus().setNowIndex(index);
    }

    protected SaveFileObjectStatus getSaveFileObjectStatus() {
        return saveFileObjectStatus;
    }

    protected void setSaveFileObjectStatus(SaveFileObjectStatus saveFileObjectStatus) {
        this.saveFileObjectStatus = saveFileObjectStatus;
    }

    protected String getPath() {
        return path;
    }

    protected SaveManager getSaveManager() {
        return saveManager;
    }
}
