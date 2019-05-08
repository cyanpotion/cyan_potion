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

package com.xenoamess.cyan_potion.base.gameWindowComponents;

import com.xenoamess.cyan_potion.base.events.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author XenoAmess
 */
public class GameWindowComponentTreeNode implements AutoCloseable {
    private final GameWindowComponentTree gameWindowComponentTree;

    private final GameWindowComponentTreeNode parent;
    private final int depth;
    private final AbstractGameWindowComponent gameWindowComponent;
    private final List<GameWindowComponentTreeNode> children =
            new ArrayList<>();

    protected GameWindowComponentTreeNode(GameWindowComponentTree gameWindowComponentTree,
                                          GameWindowComponentTreeNode parent,
                                          AbstractGameWindowComponent gameWindowComponent) {
        super();
        this.gameWindowComponentTree = gameWindowComponentTree;
        this.parent = parent;
        this.gameWindowComponent = gameWindowComponent;
        this.getGameWindowComponent().setGameWindowComponentTreeNode(this);
        if (this.getParent() != null) {
            if (this.getGameWindowComponentTree().getLeafNodes().contains(this.getParent())) {
                this.getGameWindowComponentTree().getLeafNodes().remove(this.getParent());
            }
            this.getGameWindowComponentTree().getLeafNodes().add(this);

            this.getParent().getChildren().add(this);
            this.depth = this.getParent().getDepth() + 1;
        } else {
            this.depth = 0;
        }

    }

    @Override
    public void close() {
        ArrayList<GameWindowComponentTreeNode> tmpSons =
                new ArrayList<>(getChildren());

        for (GameWindowComponentTreeNode au : tmpSons) {
            au.close();
        }

        this.getGameWindowComponent().close();


        if (getGameWindowComponentTree().getLeafNodes().contains(this)) {
            getGameWindowComponentTree().getLeafNodes().remove(this);
        }

        if (getParent() != null) {
            getParent().getChildren().remove(this);
            if (getParent().getChildren().isEmpty()) {
                getGameWindowComponentTree().getLeafNodes().add(getParent());
            }
        }
        //        super.close();
    }


    public void update() {
        ArrayList<GameWindowComponentTreeNode> tmpSons =
                new ArrayList<>(getChildren());
        for (GameWindowComponentTreeNode au : tmpSons) {
            au.update();
        }
        this.getGameWindowComponent().update();
    }

    public void draw() {
        this.getGameWindowComponent().draw();
        ArrayList<GameWindowComponentTreeNode> tmpSons =
                new ArrayList<>(getChildren());
        for (GameWindowComponentTreeNode au : tmpSons) {
            au.draw();
        }
    }

    public boolean process(final Set<Event> res, final Event event) {
        boolean flag0 = false;
        for (GameWindowComponentTreeNode au : this.getChildren()) {
            if (au.process(res, event)) {
                flag0 = true;
            }
        }

        if (flag0) {
            return true;
        } else {
            Event resEvent = this.getGameWindowComponent().process(event);
            if (resEvent != event) {
                if (resEvent != null) {
                    res.add(resEvent);
                }
                return true;
            }
        }
        return false;
    }

    public GameWindowComponentTreeNode newNode(AbstractGameWindowComponent gameWindowComponent) {
        return new GameWindowComponentTreeNode(this.getGameWindowComponentTree(), this, gameWindowComponent);
    }

    public GameWindowComponentTreeNode findNode(AbstractGameWindowComponent gameWindowComponent) {
        GameWindowComponentTreeNode res = null;
        if (this.getGameWindowComponent() == gameWindowComponent) {
            return this;
        }
        for (GameWindowComponentTreeNode au : this.getChildren()) {
            res = au.findNode(gameWindowComponent);
            if (res != null) {
                return res;
            }
        }
        return res;
    }

    public GameWindowComponentTreeNode findNode(GameWindowComponentTreeNode gameWindowComponentTreeNode) {
        GameWindowComponentTreeNode res = null;
        if (this == gameWindowComponentTreeNode) {
            return this;
        }
        for (GameWindowComponentTreeNode au : this.getChildren()) {
            res = au.findNode(gameWindowComponentTreeNode);
            if (res != null) {
                return res;
            }
        }
        return res;
    }


    public boolean deleteNode(AbstractGameWindowComponent gameWindowComponent) {
        GameWindowComponentTreeNode res = findNode(gameWindowComponent);
        if (res == null) {
            return false;
        } else {
            res.close();
            return true;
        }
    }

    public boolean deleteNode(GameWindowComponentTreeNode gameWindowComponentTreeNode) {
        GameWindowComponentTreeNode res = findNode(gameWindowComponentTreeNode);
        if (res == null) {
            return false;
        } else {
            res.close();
            return true;
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        return super.equals(object);
    }

    public AbstractGameWindowComponent getGameWindowComponent() {
        return this.gameWindowComponent;
    }

    public List<GameWindowComponentTreeNode> getChildren() {
        return this.children;
    }

    public GameWindowComponentTree getGameWindowComponentTree() {
        return gameWindowComponentTree;
    }

    public GameWindowComponentTreeNode getParent() {
        return parent;
    }

    public int getDepth() {
        return depth;
    }
}

