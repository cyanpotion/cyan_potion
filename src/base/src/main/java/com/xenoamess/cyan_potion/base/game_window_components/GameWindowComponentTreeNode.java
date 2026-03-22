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
import com.xenoamess.cyan_potion.base.game_window_components.zsupport.CoordinateSystemMode;
import com.xenoamess.cyan_potion.base.game_window_components.zsupport.ZIndexSorter;
import com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components.AbstractControllableGameWindowComponent;
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
     * Cache for sorted children list (by Z coordinate).
     * <p>
     * This is lazily computed when getSortedChildren() is called and sortDirty is true.
     * For LEGACY_MODE, this is not used.
     * </p>
     */
    private List<GameWindowComponentTreeNode> sortedChildren = null;

    /**
     * Flag indicating whether the sorted children cache needs to be recomputed.
     * <p>
     * Set to true when a child's Z coordinate changes or when children are added/removed.
     * </p>
     */
    private boolean sortDirty = true;

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
     * <p>
     * When in Z_AXIS_MODE, children are drawn in Z coordinate order (low to high).
     * When in LEGACY_MODE, children are drawn in tree traversal order.
     * </p>
     */
    public void draw() {
        this.getGameWindowComponent().draw();
        
        // Choose children list based on coordinate system mode
        List<GameWindowComponentTreeNode> childrenToDraw;
        if (this.getGameWindowComponent().getEffectiveCoordinateSystemMode() 
                == CoordinateSystemMode.Z_AXIS_MODE) {
            // Z_AXIS_MODE: draw in Z order (low to high, so high Z appears on top)
            childrenToDraw = this.getSortedChildren();
        } else {
            // LEGACY_MODE: draw in original order
            childrenToDraw = this.childrenCopy();
        }
        
        ArrayList<GameWindowComponentTreeNode> tmpSons = new ArrayList<>(childrenToDraw);
        for (GameWindowComponentTreeNode au : tmpSons) {
            au.draw();
        }
    }

    /**
     * <p>process.</p>
     * <p>
     * Processes events for this component and its children.
     * When in Z_AXIS_MODE, children are processed in reverse Z order (highest Z first),
     * so that components in front receive events before components behind them.
     * When in LEGACY_MODE, the original processing order is preserved.
     * </p>
     *
     * @param res   a {@link java.util.Set} object.
     * @param event event
     * @return a boolean.
     */
    public boolean process(final Set<Event> res, final Event event) {
        AtomicBoolean flag0 = new AtomicBoolean(false);

        // Choose children list based on coordinate system mode
        List<GameWindowComponentTreeNode> childrenToProcess;
        if (this.getGameWindowComponent().getEffectiveCoordinateSystemMode() 
                == CoordinateSystemMode.Z_AXIS_MODE) {
            // Z_AXIS_MODE: Process in reverse Z order (high to low)
            // This ensures components with higher Z (in front) get events first
            childrenToProcess = ZIndexSorter.sortByZDescending(this.childrenCopy());
        } else {
            // LEGACY_MODE: Use original order
            childrenToProcess = this.childrenCopy();
        }

        // Sequential processing for Z-axis mode (order matters)
        // Parallel processing for legacy mode (order doesn't matter)
        if (this.getGameWindowComponent().getEffectiveCoordinateSystemMode() 
                == CoordinateSystemMode.Z_AXIS_MODE) {
            // Sequential: Process highest Z first
            for (GameWindowComponentTreeNode au : childrenToProcess) {
                if (au.process(res, event)) {
                    flag0.set(true);
                    break; // Event consumed by higher Z component
                }
            }
        } else {
            // Parallel: Original behavior
            childrenToProcess.parallelStream().forEach((GameWindowComponentTreeNode au) -> {
                if (au.process(res, event)) {
                    flag0.set(true);
                }
            });
        }

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
        this.markSortDirty();
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
        this.markSortDirty();
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

    public void setVisible(boolean visible) {
        if (this.getGameWindowComponent() instanceof AbstractControllableGameWindowComponent) {
            if (((AbstractControllableGameWindowComponent) this.getGameWindowComponent()).isVisible() != visible) {
                ((AbstractControllableGameWindowComponent) this.getGameWindowComponent()).setVisible(visible);
            }
        }
        for (GameWindowComponentTreeNode gameWindowComponentTreeNode : this.children) {
            if (gameWindowComponentTreeNode != null) {
                gameWindowComponentTreeNode.setVisible(visible);
            }
        }
    }

    /**
     * Get the sorted list of children by Z coordinate.
     * <p>
     * If the sortDirty flag is true, recomputes the sorted order using ZIndexSorter.
     * Otherwise, returns the cached sorted list.
     * </p>
     * <p>
     * Components are sorted by Z coordinate in ascending order (low to high).
     * Components with the same Z coordinate maintain their original order (stable sort).
     * </p>
     *
     * @return a list of children sorted by Z coordinate
     * @see ZIndexSorter
     */
    public List<GameWindowComponentTreeNode> getSortedChildren() {
        if (sortDirty || sortedChildren == null) {
            synchronized (this.children) {
                sortedChildren = ZIndexSorter.sortByZ(this.children);
                sortDirty = false;
            }
        }
        return sortedChildren;
    }

    /**
     * Mark the sorted children cache as dirty.
     * <p>
     * This should be called when:
     * <ul>
     *   <li>A child's Z coordinate changes</li>
     *   <li>A child is added or removed</li>
     * </ul>
     * </p>
     * <p>
     * The next call to getSortedChildren() will recompute the sorted order.
     * </p>
     */
    public void markSortDirty() {
        this.sortDirty = true;
    }

}

