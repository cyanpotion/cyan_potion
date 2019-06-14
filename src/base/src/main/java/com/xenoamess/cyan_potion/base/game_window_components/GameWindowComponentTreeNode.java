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

package com.xenoamess.cyan_potion.base.game_window_components;

import com.xenoamess.cyan_potion.base.events.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * <p>GameWindowComponentTreeNode class.</p>
 *
 * @author XenoAmess
 * @version 0.143.0
 */
public class GameWindowComponentTreeNode implements AutoCloseable {
    private final GameWindowComponentTree gameWindowComponentTree;

    private final GameWindowComponentTreeNode parent;
    private final int depth;
    private final AbstractGameWindowComponent gameWindowComponent;
    private final List<GameWindowComponentTreeNode> children =
            new ArrayList<>();

    /**
     * <p>Constructor for GameWindowComponentTreeNode.</p>
     *
     * @param gameWindowComponentTree a
     *                                <p>
     *                                <p>
     *
     *
     *
     *                             {@link com.xenoamess.cyan_potion.base.game_window_components.GameWindowComponentTree}
     *                                object.
     * @param parent                  a
     *                                <p>
     *                                <p>
     *                                <p>
     *                                <p>
     *
     *
     *
     *
     *
     *
     *
     *                         {@link com.xenoamess.cyan_potion.base.game_window_components.GameWindowComponentTreeNode}
     *                                object.
     * @param gameWindowComponent     a
     *                                <p>
     *                                <p>
     *                                <p>
     *                                <p>
     *
     *
     *
     *
     *
     *
     *
     *                         {@link com.xenoamess.cyan_potion.base.game_window_components.AbstractGameWindowComponent}
     *                                object.
     */
    protected GameWindowComponentTreeNode(GameWindowComponentTree gameWindowComponentTree,
                                          GameWindowComponentTreeNode parent,
                                          AbstractGameWindowComponent gameWindowComponent) {
        super();
        this.gameWindowComponentTree = gameWindowComponentTree;
        this.parent = parent;
        this.gameWindowComponent = gameWindowComponent;
        this.getGameWindowComponent().setGameWindowComponentTreeNode(this);
        if (this.getParent() != null) {
            this.getGameWindowComponentTree().leafNodesRemove(this.getParent());
            this.getGameWindowComponentTree().leafNodesAdd(this);

            this.getParent().childrenAdd(this);
            this.depth = this.getParent().getDepth() + 1;
        } else {
            this.depth = 0;
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        ArrayList<GameWindowComponentTreeNode> tmpSons =
                new ArrayList<>(childrenCopy());

        for (GameWindowComponentTreeNode au : tmpSons) {
            au.close();
        }

        this.getGameWindowComponent().close();

        getGameWindowComponentTree().leafNodesRemove(this);

        if (getParent() != null) {
            getParent().childrenRemove(this);
            if (getParent().childrenCopy().isEmpty()) {
                getGameWindowComponentTree().leafNodesAdd(getParent());
            }
        }
    }

    /**
     * <p>update.</p>
     */
    public void update() {
        ArrayList<GameWindowComponentTreeNode> tmpSons =
                new ArrayList<>(childrenCopy());
        tmpSons.parallelStream().forEach(GameWindowComponentTreeNode::update);
        this.getGameWindowComponent().update();
    }

    /**
     * <p>draw.</p>
     */
    public void draw() {
        this.getGameWindowComponent().draw();
        ArrayList<GameWindowComponentTreeNode> tmpSons =
                new ArrayList<>(childrenCopy());
        for (GameWindowComponentTreeNode au : tmpSons) {
            au.draw();
        }
    }

    /**
     * <p>process.</p>
     *
     * @param res   a {@link java.util.Set} object.
     * @param event event
     * @return a boolean.
     */
    public boolean process(final Set<Event> res, final Event event) {
        boolean[] flag0 = new boolean[1];
        flag0[0] = false;

        this.childrenCopy().parallelStream().forEach((GameWindowComponentTreeNode au) -> {
            if (au.process(res, event)) {
                flag0[0] = true;
            }
        });

        if (flag0[0]) {
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

    /**
     * <p>newNode.</p>
     *
     * @param gameWindowComponent a
     *                            <p>
     *                            <p>
     *
     *
     *
     *                         {@link com.xenoamess.cyan_potion.base.game_window_components.AbstractGameWindowComponent}
     *                            object.
     * @return return
     */
    public GameWindowComponentTreeNode newNode(AbstractGameWindowComponent gameWindowComponent) {
        return new GameWindowComponentTreeNode(this.getGameWindowComponentTree(), this, gameWindowComponent);
    }

    /**
     * <p>findNode.</p>
     *
     * @param gameWindowComponent a
     *                            <p>
     *                            <p>
     *
     *
     *
     *                         {@link com.xenoamess.cyan_potion.base.game_window_components.AbstractGameWindowComponent}
     *                            object.
     * @return return
     */
    public GameWindowComponentTreeNode findNode(AbstractGameWindowComponent gameWindowComponent) {
        if (this.getGameWindowComponent() == gameWindowComponent) {
            return this;
        }

        final GameWindowComponentTreeNode[] res = new GameWindowComponentTreeNode[1];

        this.childrenCopy().parallelStream().forEach((GameWindowComponentTreeNode gameWindowComponentTreeNode) -> {
            if (res[0] != null) {
                return;
            }
            GameWindowComponentTreeNode tmp = gameWindowComponentTreeNode.findNode(gameWindowComponent);
            if (tmp != null) {
                res[0] = tmp;
            }
        });
        return res[0];
    }

    /**
     * this function is used to detect if param gameWindowComponentTreeNode is in the child tree of this node.
     *
     * @param gameWindowComponentTreeNode a
     *                                    <p>
     *                                    <p>
     *                                    <p>
     *                                    <p>
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *                         {@link com.xenoamess.cyan_potion.base.game_window_components.GameWindowComponentTreeNode}
     *                                    object.
     * @return gameWindowComponentTreeNode if be there,
     */
    public boolean childrenTreeContains(GameWindowComponentTreeNode gameWindowComponentTreeNode) {
        if (this == gameWindowComponentTreeNode) {
            return true;
        }
        boolean[] flag0 = new boolean[1];
        flag0[0] = false;

        this.childrenCopy().parallelStream().forEach((GameWindowComponentTreeNode au) -> {
            if (flag0[0]) {
                return;
            }
            if (au.childrenTreeContains(gameWindowComponentTreeNode)) {
                flag0[0] = true;
            }
        });
        return flag0[0];
    }


    /**
     * <p>deleteNode.</p>
     *
     * @param gameWindowComponent a
     *                            <p>
     *                            <p>
     *
     *
     *
     *                         {@link com.xenoamess.cyan_potion.base.game_window_components.AbstractGameWindowComponent}
     *                            object.
     * @return a boolean.
     */
    public boolean deleteNode(AbstractGameWindowComponent gameWindowComponent) {
        GameWindowComponentTreeNode res = findNode(gameWindowComponent);
        if (res == null) {
            return false;
        } else {
            res.close();
            return true;
        }
    }

    /**
     * <p>deleteNode.</p>
     *
     * @param gameWindowComponentTreeNode a
     *                                    <p>
     *                                    <p>
     *                                    <p>
     *                                    <p>
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *                         {@link com.xenoamess.cyan_potion.base.game_window_components.GameWindowComponentTreeNode}
     *                                    object.
     * @return a boolean.
     */
    public boolean deleteNode(GameWindowComponentTreeNode gameWindowComponentTreeNode) {
        if (childrenTreeContains(gameWindowComponentTreeNode)) {
            gameWindowComponentTreeNode.close();
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GameWindowComponentTreeNode)) {
            return false;
        }
        GameWindowComponentTreeNode that = (GameWindowComponentTreeNode) o;
        return getDepth() == that.getDepth() &&
                Objects.equals(getGameWindowComponentTree(), that.getGameWindowComponentTree()) &&
                Objects.equals(getParent(), that.getParent()) &&
                Objects.equals(getGameWindowComponent(), that.getGameWindowComponent());
    }

    /**
     * <p>Getter for the field <code>gameWindowComponent</code>.</p>
     *
     * @return return
     */
    public AbstractGameWindowComponent getGameWindowComponent() {
        return this.gameWindowComponent;
    }

    /**
     * <p>childrenCopy.</p>
     *
     * @return return
     */
    public List<GameWindowComponentTreeNode> childrenCopy() {
        List<GameWindowComponentTreeNode> res;
        synchronized (this.children) {
            res = new ArrayList<>(this.children);
        }
        return res;
    }

    /**
     * <p>childrenAdd.</p>
     *
     * @param gameWindowComponentTreeNode a
     *                                    <p>
     *                                    <p>
     *                                    <p>
     *                                    <p>
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *                         {@link com.xenoamess.cyan_potion.base.game_window_components.GameWindowComponentTreeNode}
     *                                    object.
     */
    public void childrenAdd(GameWindowComponentTreeNode gameWindowComponentTreeNode) {
        synchronized (this.children) {
            this.children.add(gameWindowComponentTreeNode);
        }
    }

    /**
     * <p>childrenRemove.</p>
     *
     * @param gameWindowComponentTreeNode a
     *                                    <p>
     *                                    <p>
     *                                    <p>
     *                                    <p>
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *                         {@link com.xenoamess.cyan_potion.base.game_window_components.GameWindowComponentTreeNode}
     *                                    object.
     */
    public void childrenRemove(GameWindowComponentTreeNode gameWindowComponentTreeNode) {
        synchronized (this.children) {
            this.children.remove(gameWindowComponentTreeNode);
        }
    }

    /**
     * <p>Getter for the field <code>gameWindowComponentTree</code>.</p>
     *
     * @return return
     */
    public GameWindowComponentTree getGameWindowComponentTree() {
        return gameWindowComponentTree;
    }

    /**
     * <p>Getter for the field <code>parent</code>.</p>
     *
     * @return return
     */
    public GameWindowComponentTreeNode getParent() {
        return parent;
    }

    /**
     * <p>Getter for the field <code>depth</code>.</p>
     *
     * @return a int.
     */
    public int getDepth() {
        return depth;
    }
}

