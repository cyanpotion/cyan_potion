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

package com.xenoamess.cyan_potion.base;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xenoamess.commons.as_final_field.AsFinalField;
import com.xenoamess.cyan_potion.base.setting_file.GameSettings;
import com.xenoamess.multi_language.MultiLanguageStructure;
import com.xenoamess.x8l.X8lTree;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>DataCenter class.</p>
 *
 * @author XenoAmess
 * @version 0.162.2-SNAPSHOT
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public class DataCenter extends SubManager {

    /**
     * Constant <code>MAIN_THREAD_NAME="main"</code>
     */
    @SuppressWarnings("unused")
    public static final String MAIN_THREAD_NAME = "main";
    /**
     * Constant <code>MAIN_THREAD_ID=1</code>
     */
    public static final long MAIN_THREAD_ID = 1;

    /**
     * <p>ifMainThread.</p>
     *
     * @return a boolean.
     */
    public static boolean ifMainThread() {
        return Thread.currentThread().getId() == MAIN_THREAD_ID;
    }

    /**
     * Constant <code>ALLOW_RUN_WITHOUT_STEAM=true</code>
     */
    public static final boolean ALLOW_RUN_WITHOUT_STEAM = true;

    /**
     * Constant <code>DEFAULT_CONSOLE_PORT=13888</code>
     */
    public static final int DEFAULT_CONSOLE_PORT = 13888;

    /**
     * Constant <code>SCALE=2</code>
     */
    public static final int SCALE = 2;

    //---final ends---

    /**
     * the port used to receive console commands.
     */
    @Getter
    private int consolePort = DEFAULT_CONSOLE_PORT;

    /**
     * If true, then will use JXInput
     * (using DirectX directly, but can only run in windows.)
     * to deal with controller.
     * <p>
     * If false, then will use Jamepad
     * (using SDL, can run on multi-platforms.)
     * to deal with controller.
     * <p>
     * At default it is set to false, meaing we just use Jamepad.
     */
    @Getter
    @Setter
    private boolean usingJXInput = false;

    @Getter
    @Setter
    private X8lTree patchSettingsTree;

    @Getter
    @Setter
    private MultiLanguageStructure textStructure;

    @Getter
    @Setter
    @AsFinalField
    private GameSettings gameSettings;

    /**
     * <p>Constructor for DataCenter.</p>
     *
     * @param gameManager gameManager
     */
    public DataCenter(GameManager gameManager) {
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

    @Getter
    @AsFinalField
    private static ObjectMapper objectMapper;

    /**
     * <p>Getter for the field <code>objectMapper</code>.</p>
     *
     * @return return
     */
    public static ObjectMapper getObjectMapper() {
        /*
         * lazy init.
         * we don't synchronize here,
         * because it will not cause any big trouble,
         * even if we have multiple objectMappers running at the same time.
         */
        if (objectMapper == null) {
            ObjectMapper localObjectMapper = new ObjectMapper();
            localObjectMapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
            objectMapper = localObjectMapper;
        }
        return objectMapper;
    }
}
