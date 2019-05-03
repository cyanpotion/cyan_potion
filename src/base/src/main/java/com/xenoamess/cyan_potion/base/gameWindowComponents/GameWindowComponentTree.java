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

//import com.xenoamess.gearbar.GameEngineObject;

import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.events.Event;
import com.xenoamess.cyan_potion.base.events.KeyEvent;
import com.xenoamess.cyan_potion.base.io.input.key.Keymap;
import org.lwjgl.glfw.GLFW;

import java.util.*;


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
    private final GameWindowComponentTreeNode root;
    private final Set<GameWindowComponentTreeNode> leafNodes;

    public Set<GameWindowComponentTreeNode> getLeafNodes() {
        return leafNodes;
    }

    public List<GameWindowComponentTreeNode> getAllNodes() {
        List<GameWindowComponentTreeNode> res =
                new ArrayList<GameWindowComponentTreeNode>();
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

    public GameWindowComponentTree(GameWindow gameWindow) {
        super();
        AbstractGameWindowComponent baseComponent =
                new AbstractGameWindowComponent(gameWindow) {

                    @Override
                    public void initProcessors() {
                        this.registerProcessor(KeyEvent.class.getCanonicalName(),
                                event -> {
                                    KeyEvent keyEvent = (KeyEvent) event;
                                    switch (keyEvent.getKeyTranslated().getKey()) {
                                        case Keymap.XENOAMESS_KEY_ENTER:
                                            if (keyEvent.getAction() == GLFW.GLFW_PRESS && keyEvent.checkMods(GLFW.GLFW_MOD_ALT)) {
                                                this.getGameWindow().changeFullScreen();
                                            }
                                            return null;
                                        default:
                                            return null;
                                    }
                                });
                    }

                    @Override
                    public void close() {
                        super.close();
                        this.getGameWindow().getGameManager().shutdown();
                    }

                    @Override
                    public void update() {

                    }

                    @Override
                    public void draw() {

                    }
                };

        root = new GameWindowComponentTreeNode(this, null, baseComponent);
        leafNodes = new HashSet<>();
        getLeafNodes().add(getRoot());
    }

    @Override
    public void close() {
        if (getRoot() != null) {
            getRoot().close();
        }

        getLeafNodes().clear();
//        super.close();
    }


    public Set<Event> process(Event event) {
        Set<Event> res = new HashSet<Event>();
        process(this.getRoot(), res, event);
        return res;
//        Map<GameWindowComponentTreeNode, Set> eventMap = new
//        HashMap<GameWindowComponentTreeNode, Set>();
//        for (GameWindowComponentTreeNode au : this.getLeafNodes()) {
//            Set eventSet = new HashSet<Event>();
//            eventSet.add(event);
//            eventMap.put(au, eventSet);
//        }
//
//        while (!eventMap.isEmpty()) {
//            Map<GameWindowComponentTreeNode, Set> newEventMap = new
//            HashMap<GameWindowComponentTreeNode, Set>();
//            for (Map.Entry<GameWindowComponentTreeNode, Set> entry :
//            eventMap.entrySet()) {
//                if (entry.getValue().size() >= 2) {
//                    entry.getValue().remove(event);
//                }
//
//                for (Object object : entry.getValue()) {
//                    if (object == null) continue;
//                    Event originalEvent = (Event) object;
//                    Event resEvent = entry.getKey().gameWindowComponent
//                    .process(originalEvent);
//                    GameWindowComponentTreeNode node = entry.getKey();
//                    if (resEvent != null) {
//                        if (node.father == null) {
//                            res.add(resEvent);
//                        } else {
//                            Set<Event> eventSet = newEventMap.get(node
//                            .father);
//                            if (eventSet == null) {
//                                eventSet = new HashSet<Event>();
//                                newEventMap.put(node.father, eventSet);
//                            }
//                            eventSet.add(resEvent);
//                        }
//                    }
//                }
//            }
//            eventMap.clear();
//            eventMap = newEventMap;
//        }
//        return res;
    }

    private boolean process(GameWindowComponentTreeNode nowNode,
                            Set<Event> res, Event event) {
        boolean flag0 = false;
        for (GameWindowComponentTreeNode au : nowNode.getChildren()) {
            if (process(au, res, event)) {
                flag0 = true;
            }
        }

        if (flag0) {
            return true;
        } else {
            Event resEvent = nowNode.getGameWindowComponent().process(event);
            if (resEvent != event) {
                if (resEvent != null) {
                    res.add(resEvent);
                }
                return true;
            }
        }
        return false;
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
        if (it.hasNext()) {
            return it.next().newNode(gameWindowComponent);
        } else {
            throw new Error("GameWindowComponentTree do not have any " +
                    "leafNode!!! What did you do???");
        }
    }

    public GameWindowComponentTreeNode findNode(AbstractGameWindowComponent gameWindowComponent) {
        return this.getRoot().findNode(gameWindowComponent);
    }

    public GameWindowComponentTreeNode findNode(GameWindowComponentTreeNode gameWindowComponentTreeNode) {
        return findNode(gameWindowComponentTreeNode);
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
}
