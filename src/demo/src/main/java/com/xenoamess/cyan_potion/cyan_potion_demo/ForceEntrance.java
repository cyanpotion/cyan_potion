package com.xenoamess.cyan_potion.cyan_potion_demo;

import com.xenoamess.cyan_potion.base.GameManager;

import java.util.Map;

/**
 * @author XenoAmess
 */
public class ForceEntrance {
    public static void main(String[] args) {
        Map<String, String> argsMap = GameManager.generateArgsMap(args);
        argsMap.put("SettingFilePath", "/settings/RpgModuleDemoSettings.x8l");
        GameManager gameManager = new GameManager(argsMap);
        gameManager.startup();
    }
}
