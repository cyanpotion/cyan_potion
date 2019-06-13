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

package com.xenoamess.cyan_potion.base.plugins;

import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.exceptions.URITypeNotDefinedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.function.Function;

/**
 * @author XenoAmess
 */
public class CodePluginManager {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(CodePluginManager.class);

    private EnumMap<CodePluginPosition, ArrayList<Function<GameManager, Void>>> codePluginPositionFunctionHashMap =
            new EnumMap<>(CodePluginPosition.class);

    public CodePluginManager() {
        for (CodePluginPosition codePluginPosition : CodePluginPosition.values()) {
            this.codePluginPositionFunctionHashMap.put(codePluginPosition, new ArrayList<>(4));
        }
    }


    public Function<GameManager, Void> getCodePluginFunctionFromString(String codePluginString) {
        Function<GameManager, Void> res = null;
        String[] codePluginStrings = codePluginString.split(":");

        switch (codePluginStrings[0]) {
            case "SimpleFunctionObject":
                try {
                    Field field = Class.forName(codePluginStrings[1]).getDeclaredField(codePluginStrings[2]);
                    field.setAccessible(true);
                    if (field.getType().equals(Function.class)) {
                        res = (Function<GameManager, Void>) field.get(null);
                    }
                } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
                    LOGGER.error("getCodePluginFunctionFromString(String codePluginString) fails:{}", codePluginString, e);
                }
                break;
            default:
        }
        if (res == null) {
            throw new URITypeNotDefinedException(codePluginString);
        }
        return res;
    }

    public void putCodePlugin(CodePluginPosition codePluginPosition, String codePluginString) {
        putCodePlugin(codePluginPosition, getCodePluginFunctionFromString(codePluginString));
    }

    public void putCodePlugin(CodePluginPosition codePluginPosition, Function<GameManager, Void> codePluginFunction) {
        codePluginPositionFunctionHashMap.get(codePluginPosition).add(codePluginFunction);
    }

    public ArrayList<Function<GameManager, Void>> getCodePluginFunctions(CodePluginPosition codePluginPosition) {
        return codePluginPositionFunctionHashMap.get(codePluginPosition);
    }

    public void apply(GameManager gameManager, CodePluginPosition codePluginPosition) {
        for (Function<GameManager, Void> function : this.getCodePluginFunctions(codePluginPosition)) {
            function.apply(gameManager);
        }
    }

}
