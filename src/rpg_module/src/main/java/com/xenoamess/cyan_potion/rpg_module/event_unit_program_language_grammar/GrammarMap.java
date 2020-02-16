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

package com.xenoamess.cyan_potion.rpg_module.event_unit_program_language_grammar;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>GrammarMap class.</p>
 *
 * @author XenoAmess
 * @version 0.159.0
 */
public class GrammarMap {
    @JsonIgnore
    private static transient final Logger LOGGER = LoggerFactory.getLogger(GrammarMap.class);

    private GrammarMap() {
    }

    private static final Map<Integer, String> METHOD_NUM_TO_METHOD_NAME_MAP =
            new ConcurrentHashMap<>();
    private static final Map<String, Integer> METHOD_NAME_TO_METHOD_NUM_MAP =
            new ConcurrentHashMap<>();


    /**
     * <p>getMethodName.</p>
     *
     * @param methodNum a int.
     * @return return
     */
    public static String getMethodName(int methodNum) {
        checkInit();
        return METHOD_NUM_TO_METHOD_NAME_MAP.get(methodNum);
    }

    /**
     * <p>getMethodNum.</p>
     *
     * @param methodName methodName
     * @return return
     */
    public static Integer getMethodNum(String methodName) {
        checkInit();
        return METHOD_NAME_TO_METHOD_NUM_MAP.get(methodName);
    }

    private static void checkInit() {
        if (METHOD_NUM_TO_METHOD_NAME_MAP.isEmpty() || METHOD_NAME_TO_METHOD_NUM_MAP.isEmpty()) {
            for (Field field : Grammar.class.getDeclaredFields()) {
                if (field.getType().equals(int.class) && field.getName().startsWith("G_")) {
                    String methodName = field.getName().substring(2);
                    int methodNum = -1;
                    field.setAccessible(true);
                    try {
                        methodNum = (int) field.get(null);
                    } catch (IllegalAccessException e) {
                        LOGGER.warn("IllegalAccessException in GrammarMap.checkInit", e);
                    }

                    if (methodNum != -1) {
                        GrammarMap.METHOD_NUM_TO_METHOD_NAME_MAP.put(methodNum,
                                methodName);
                        GrammarMap.METHOD_NAME_TO_METHOD_NUM_MAP.put(methodName,
                                methodNum);
                    }
                }
            }
        }
    }
}
