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


import com.xenoamess.commons.as_final_field.AsFinalField;
import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.events.Event;
import com.xenoamess.cyan_potion.base.events.WindowResizeEvent;
import com.xenoamess.cyan_potion.base.io.input.key.Keymap;
import com.xenoamess.cyan_potion.base.io.input.keyboard.KeyboardEvent;
import org.lwjgl.glfw.GLFW;

import java.util.*;

import static com.xenoamess.commons.as_final_field.AsFinalFieldUtils.asFinalFieldSet;


/**
 * In normal cases the GameWindowComponentTree Shall be a List(Stack).
 * In other means there shall be one leafNode in the same time.
 * If there be more than one leafNodes,events would be processed by EVERY
 * leafNode,thus can cause a same Event be handled several times.
 * Please be careful about this situation when use.
 *
 * @author XenoAmess
 */
public class GameWindowComponentTree implements AutoCloseable {
    @AsFinalField
    private GameWindowComponentTreeNode root = null;
    private final Set<GameWindowComponentTreeNode> leafNodes = new HashSet<>();

    public Set<GameWindowComponentTreeNode> getLeafNodes() {
        return leafNodes;
    }

    public List<GameWindowComponentTreeNode> getAllNodes() {
        List<GameWindowComponentTreeNode> res = new ArrayList<>();
        this.getAllNodes(this.getRoot(), res);
        return res;
    }

    private void getAllNodes(GameWindowComponentTreeNode nowNode,
                             List<GameWindowComponentTreeNode> res) {
        for (GameWindowComponentTreeNode au : nowNode.getChildren()) {
            getAllNodes(au, res);
        }
        res.add(nowNode);
    }

    public void init(GameWindow gameWindow) {
        AbstractGameWindowComponent baseComponent =
                new AbstractGameWindowComponent(gameWindow) {

                    @Override
                    public void initProcessors() {
                        this.registerProcessor(KeyboardEvent.class.getCanonicalName(),
                                event -> {
                                    KeyboardEvent keyboardEvent = (KeyboardEvent) event;
                                    switch (keyboardEvent.getKeyTranslated(this.getGameWindow().getGameManager().getKeymap()).getKey()) {
                                        case Keymap.XENOAMESS_KEY_ENTER:
                                            if (keyboardEvent.getAction() == GLFW.GLFW_PRESS && keyboardEvent.checkMods(GLFW.GLFW_MOD_ALT)) {
                                                this.getGameWindow().changeFullScreen();
                                            }
                                            return null;
                                        default:
                                            return null;
                                    }
                                });
                        this.registerProcessor(WindowResizeEvent.class.getCanonicalName(),
                                event -> {
                                    WindowResizeEvent windowResizeEvent = (WindowResizeEvent) event;
                                    this.getGameWindow().setRealWindowWidth(windowResizeEvent.getWidth());
                                    this.getGameWindow().setRealWindowHeight(windowResizeEvent.getHeight());
                                    return null;
                                });
                    }

                    @Override
                    public void close() {
                        super.close();
                        this.getGameWindow().getGameManager().shutdown();
                    }

                    @Override
                    public void update() {
                        //no update here.
                    }

                    @Override
                    public void draw() {
                        //no draw here.
                    }
                };

        this.setRoot(new GameWindowComponentTreeNode(this, null, baseComponent));
        this.getLeafNodes().add(this.getRoot());
    }

    @Override
    public void close() {
        if (getRoot() != null) {
            getRoot().close();
        }

        getLeafNodes().clear();
    }

    public Set<Event> process(Event event) {
        Set<Event> res = new HashSet<>();
        this.getRoot().process(res, event);
        return res;
    }

    public void update() {
        getRoot().update();
    }

    public void draw() {
        getRoot().draw();
    }

    public GameWindowComponentTreeNode newNode(AbstractGameWindowComponent gameWindowComponent) {
        Iterator<GameWindowComponentTreeNode> it =
                this.getLeafNodes().iterator();
        assert (it.hasNext());
        return it.next().newNode(gameWindowComponent);
    }

    public GameWindowComponentTreeNode findNode(AbstractGameWindowComponent gameWindowComponent) {
        return this.getRoot().findNode(gameWindowComponent);
    }

    public GameWindowComponentTreeNode findNode(GameWindowComponentTreeNode gameWindowComponentTreeNode) {
        return this.getRoot().findNode(gameWindowComponentTreeNode);
    }

    public boolean deleteNode(AbstractGameWindowComponent gameWindowComponent) {
        return this.getRoot().deleteNode(gameWindowComponent);
    }

    public boolean deleteNode(GameWindowComponentTreeNode gameWindowComponentTreeNode) {
        return this.getRoot().deleteNode(gameWindowComponentTreeNode);
    }


    public GameWindowComponentTreeNode getRoot() {
        return root;
    }

    /**
     * set root of this tree.
     * notice that root is an {@link AsFinalField}.
     *
     * @param gameWindowComponentTreeNode root
     * @see AsFinalField
     */
    public void setRoot(GameWindowComponentTreeNode gameWindowComponentTreeNode) {
        asFinalFieldSet(this, "root", gameWindowComponentTreeNode);
    }
}
