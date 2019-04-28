package com.xenoamess.cyan_potion.rpg_module.jsons;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author XenoAmess
 */
public class MoveRouteJson implements Serializable {
    public boolean repeat;
    public boolean skippable;
    public boolean wait;
    public ArrayList<EventUnitProgramCodeLineJson> list;
}
