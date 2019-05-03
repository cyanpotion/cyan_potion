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

package com.xenoamess.cyan_potion.rpg_module.gameMap;

import com.xenoamess.cyan_potion.base.render.Bindable;

import java.util.ArrayList;

/**
 * @author XenoAmess
 */
public class GameTile {
    //    public static final GameTile tileGrass = new GameTile(new Texture
    //    ("grass.png"));
    //    public static final GameTile tileBlock = new GameTile(new Texture
    //    ("checker.png")).setSolid();

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
