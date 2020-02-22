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

package com.xenoamess.cyan_potion.cyan_potion_demo;

import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.memory.ResourceManager;
import com.xenoamess.x8l.X8lTree;
import com.xenoamess.x8l.dealers.JsonDealer;
import com.xenoamess.x8l.dealers.XmlDealer;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

/**
 * <p>ForceEntrance class.</p>
 *
 * @author XenoAmess
 * @version 0.161.1-SNAPSHOT
 */
public class ForceEntrance {
    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) {
        generateJsonAndXml();
        Map<String, String> argsMap = GameManager.generateArgsMap(args);
        switch (new Random().nextInt(3)) {
            case 0:
                argsMap.put("SettingFilePath", "resources/settings/RpgModuleDemoSettings.x8l");
                break;
            case 1:
                argsMap.put("SettingFilePath", "resources/settings/RpgModuleDemoSettings.json");
                break;
            case 2:
                argsMap.put("SettingFilePath", "resources/settings/RpgModuleDemoSettings.xml");
                break;
        }
        System.out.println(argsMap);
        GameManager gameManager = new GameManager(argsMap);
        gameManager.getDataCenter().setPatchSettingsTree(
                X8lTree.load("<debug>>"));
        gameManager.startup();
    }

    /**
     * <p>generateJsonAndXml.</p>
     */
    public static void generateJsonAndXml() {
        try {
            final String basePath = "resources/settings/RpgModuleDemoSettings";
            X8lTree x8lTree = null;
            x8lTree = X8lTree.load(ResourceManager.resolveFile(basePath + ".x8l"));
            x8lTree.trimForce();
            x8lTree.setLanguageDealer(JsonDealer.INSTANCE);
            X8lTree.save(ResourceManager.resolveFile(basePath + ".json"), x8lTree);
            x8lTree.setLanguageDealer(XmlDealer.INSTANCE);
            X8lTree.save(ResourceManager.resolveFile(basePath + ".xml"), x8lTree);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
