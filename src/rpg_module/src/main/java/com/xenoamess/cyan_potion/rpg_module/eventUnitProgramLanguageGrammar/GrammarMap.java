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

package com.xenoamess.cyan_potion.rpg_module.eventUnitProgramLanguageGrammar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * @author XenoAmess
 */
public class GrammarMap {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrammarMap.class);

    private GrammarMap() {
    }

    private static final Map<Integer, String> MethodNumToMethodNameMap =
            new HashMap<>();
    private static final Map<String, Integer> MethodNameToMethodNumMap =
            new HashMap<>();


    public static String getMethodName(int methodNum) {
        checkInit();
        return MethodNumToMethodNameMap.get(methodNum);
    }

    public static int getMethodNum(String methodName) {
        checkInit();
        return MethodNameToMethodNumMap.get(methodName);
    }

    private static void checkInit() {
        if (MethodNumToMethodNameMap == null || MethodNameToMethodNumMap == null) {
            int size = Grammar.class.getDeclaredFields().length;
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
                        GrammarMap.MethodNumToMethodNameMap.put(methodNum,
                                methodName);
                        GrammarMap.MethodNameToMethodNumMap.put(methodName,
                                methodNum);
                    }
                }
            }
        }
    }
}
