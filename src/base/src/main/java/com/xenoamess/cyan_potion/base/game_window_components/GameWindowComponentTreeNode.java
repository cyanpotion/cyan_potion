/*
 * MIT License
 *
 * Copyright (c) 2020 XenoAmess
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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>GameWindowComponentTreeNode class.</p>
 *
 * @author XenoAmess
 * @version 0.162.3
 */
@EqualsAndHashCode
@ToString
public class GameWindowComponentTreeNode implements Closeable {
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Getter
    private final GameWindowComponentTree gameWindowComponentTree;

    @Getter
    private final GameWindowComponentTreeNode parent;

    @Getter
    private final AbstractGameWindowComponent gameWindowComponent;

    @Getter
    private final int depth;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Getter
    private final List<GameWindowComponentTreeNode> children =
            new ArrayList<>();

    private final AtomicBoolean alive = new AtomicBoolean(true);

    /**
     * <p>Constructor for GameWindowComponentTreeNode.</p>
     *
     * @param gameWindowComponentTree gameWindowComponentTree
     * @param parent                  parent
     * @param gameWindowComponent     gameWindowComponent
     */
    protected GameWindowComponentTreeNode(GameWindowComponentTree gameWindowComponentTree,
                                          GameWindowComponentTreeNode parent,
                                          AbstractGameWindowComponent gameWindowComponent) {
        super();
        this.gameWindowComponentTree = gameWindowComponentTree;
        this.gameWindowComponent = gameWindowComponent;
        this.parent = parent;
        this.getGameWindowComponent().setGameWindowComponentTreeNode(this);
        if (parent != null) {
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
        if (!this.isAlive()) {
            return;
        }
        this.setAlive(false);

        ArrayList<GameWindowComponentTreeNode> tmpSons =
                new ArrayList<>(childrenCopy());

        for (GameWindowComponentTreeNode au : tmpSons) {
            au.close();
        }

        AbstractGameWindowComponent gameWindowComponentLocal = this.getGameWindowComponent();
        if (gameWindowComponentLocal != null) {
            gameWindowComponentLocal.close();
        }

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
        AtomicBoolean flag0 = new AtomicBoolean(false);

        this.childrenCopy().parallelStream().forEach((GameWindowComponentTreeNode au) -> {
            if (au.process(res, event)) {
                flag0.set(true);
            }
        });

        if (flag0.get()) {
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
     * @param gameWindowComponent gameWindowComponent
     * @return return
     */
    @SuppressWarnings("UnusedReturnValue")
    public GameWindowComponentTreeNode newNode(AbstractGameWindowComponent gameWindowComponent) {
        return new GameWindowComponentTreeNode(this.getGameWindowComponentTree(), this, gameWindowComponent);
    }

    /**
     * <p>findNode.</p>
     *
     * @param gameWindowComponent gameWindowComponent
     * @return return
     */
    public GameWindowComponentTreeNode findNode(AbstractGameWindowComponent gameWindowComponent) {
        if (this.getGameWindowComponent() == gameWindowComponent) {
            return this;
        }

        final GameWindowComponentTreeNode[] res = new GameWindowComponentTreeNode[1];
        final AtomicBoolean find = new AtomicBoolean(false);

        this.childrenCopy().parallelStream().forEach((GameWindowComponentTreeNode gameWindowComponentTreeNode) -> {
            if (find.get()) {
                return;
            }
            GameWindowComponentTreeNode tmp = gameWindowComponentTreeNode.findNode(gameWindowComponent);
            if (tmp != null) {
                if (!find.get()) {
                    res[0] = tmp;
                    find.set(true);
                }
                return;
            }
        });
        return res[0];
    }

    /**
     * this function is used to detect if param gameWindowComponentTreeNode is in the child tree of this node.
     *
     * @param gameWindowComponentTreeNode gameWindowComponentTreeNode
     * @return gameWindowComponentTreeNode if be there,
     */
    public boolean childrenTreeContains(GameWindowComponentTreeNode gameWindowComponentTreeNode) {
        if (this == gameWindowComponentTreeNode) {
            return true;
        }
        AtomicBoolean flag0 = new AtomicBoolean(false);

        this.childrenCopy().parallelStream().forEach((GameWindowComponentTreeNode au) -> {
            if (flag0.get()) {
                return;
            }
            if (au.childrenTreeContains(gameWindowComponentTreeNode)) {
                flag0.set(true);
            }
        });
        return flag0.get();
    }


    /**
     * <p>deleteNode.</p>
     *
     * @param gameWindowComponent gameWindowComponent
     * @return if the delete succeed.
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
     * @param gameWindowComponentTreeNode gameWindowComponentTreeNode
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
     * @param gameWindowComponentTreeNode gameWindowComponentTreeNode
     */
    public void childrenAdd(GameWindowComponentTreeNode gameWindowComponentTreeNode) {
        synchronized (this.children) {
            this.children.add(gameWindowComponentTreeNode);
        }
    }

    /**
     * <p>childrenRemove.</p>
     *
     * @param gameWindowComponentTreeNode gameWindowComponentTreeNode
     */
    public void childrenRemove(GameWindowComponentTreeNode gameWindowComponentTreeNode) {
        synchronized (this.children) {
            this.children.remove(gameWindowComponentTreeNode);
        }
    }

    /**
     * <p>Getter for the field <code>alive</code>.</p>
     *
     * @return a boolean.
     */
    public boolean isAlive() {
        return alive.get();
    }

    /**
     * <p>Setter for the field <code>alive</code>.</p>
     *
     * @param alive a boolean.
     */
    public void setAlive(boolean alive) {
        this.alive.set(alive);
    }
}

