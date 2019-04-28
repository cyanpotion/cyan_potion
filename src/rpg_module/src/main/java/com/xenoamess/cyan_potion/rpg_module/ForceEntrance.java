package com.xenoamess.cyan_potion.rpg_module;

import com.xenoamess.cyan_potion.base.GameManager;

import java.util.Map;

/**
 * @author XenoAmess
 */
public class ForceEntrance {
    public static void main(String[] args) {
        Map<String, String> argsMap = GameManager.generateArgsMap(args);
        argsMap.put("SettingFilePath", "/settings/RpgModuleSettings.x8l");
        GameManager gameManager = new GameManager(argsMap);
        gameManager.startup();
    }
}
