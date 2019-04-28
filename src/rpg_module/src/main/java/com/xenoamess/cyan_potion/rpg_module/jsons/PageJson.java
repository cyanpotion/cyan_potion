package com.xenoamess.cyan_potion.rpg_module.jsons;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author XenoAmess
 */
public class PageJson implements Serializable {
    public ConditionsJson conditions;
    public boolean directionFix;
    public ImageJson image;
    public ArrayList<EventUnitProgramCodeLineJson> list;
    public int moveFrequency;
    public MoveRouteJson moveRoute;
    public int moveSpeed;
    public int moveType;
    public int priorityType;
    public boolean stepAnime;
    public boolean through;
    public int trigger;
    public boolean walkAnime;
}
