package com.xenoamess.cyan_potion.rpg_module;

import com.xenoamess.cyan_potion.base.DataCenter;

import java.util.ArrayList;

/**
 * @author XenoAmess
 */
public class GameRuntime {
    public DataCenter dataCenter;

    public ArrayList<Boolean> runtimeSwitches;
    public ArrayList<Integer> runtimeIntegerVariables;


    //TODO
    //maybe it would be better to write an Item class and an Actor class to do this.
    //emmmmmmm

    public GameRuntime(DataCenter dataCenter) {
        this.dataCenter = dataCenter;
    }


    public void saveGameRuntime() {
        //TODO
    }

    public void loadGameRuntime() {
        //TODO
    }
}
