package com.xenoamess.cyan_potion.rpg_module.gameMap;

import com.xenoamess.cyan_potion.base.render.Bindable;

import java.util.ArrayList;

/**
 * @author XenoAmess
 */
public class GameTile {
    //    public static final GameTile tileGrass = new GameTile(new Texture("grass.png"));
    //    public static final GameTile tileBlock = new GameTile(new Texture("checker.png")).setSolid();

    private boolean solid = false;

    private ArrayList<Bindable> bindables = new ArrayList<Bindable>();

    public void addBindable(Bindable bindable) {
        this.getBindables().add(bindable);
    }

    public GameTile() {

    }


    public GameTile(Bindable bindable) {
        this();
        this.addBindable(bindable);
    }

    public GameTile setSolid() {
        this.setSolid(true);
        return this;
    }

    public boolean isSolid() {
        return solid;
    }

    public void setSolid(boolean solid) {
        this.solid = solid;
    }

    public ArrayList<Bindable> getBindables() {
        return bindables;
    }

    public void setBindables(ArrayList<Bindable> bindables) {
        this.bindables = bindables;
    }
}
