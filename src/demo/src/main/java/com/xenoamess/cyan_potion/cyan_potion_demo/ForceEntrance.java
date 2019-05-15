package com.xenoamess.cyan_potion.cyan_potion_demo;

import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.x8l.X8lTree;

import java.io.IOException;
import java.util.Map;

/**
 * @author XenoAmess
 */
public class ForceEntrance {
    public static void main(String[] args) {
        System.out.println(com.xenoamess.x8l.Version.VERSION);
        Map<String, String> argsMap = GameManager.generateArgsMap(args);
        argsMap.put("SettingFilePath", "/settings/RpgModuleDemoSettings.x8l");
        GameManager gameManager = new GameManager(argsMap);
        try {
            gameManager.getDataCenter().setPatchSettingsTree(
                    X8lTree.loadFromString("<commonSettings runWithSteam=0>>"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        gameManager.startup();
    }
}
