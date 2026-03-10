/**
 * Copyright (C) 2020 XenoAmess
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.xenoamess.cyan_potion.civilization;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.xenoamess.cyan_potion.base.GameManager;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * XenoAmess's Civilization module entry point.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
public class CivilizationGame {

    /**
     * Main entry point for the civilization game.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Map<String, String> argsMap = GameManager.generateArgsMap(args);
        argsMap.put("SettingFilePath", "settings/CivilizationSettings.x8l");
        GameManager gameManager = new GameManager(argsMap);
        gameManager.startup();
    }
}
