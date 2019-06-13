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

package com.xenoamess.cyan_potion.base.gameWindowComponents.ControllableGameWindowComponents;

import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.events.Event;
import com.xenoamess.cyan_potion.base.io.ClipboardUtil;
import com.xenoamess.cyan_potion.base.io.input.keyboard.CharEvent;
import com.xenoamess.cyan_potion.base.io.input.keyboard.KeyboardEvent;
import com.xenoamess.cyan_potion.base.visual.Font;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBTruetype.stbtt_GetPackedQuad;


/**
 * @author XenoAmess
 */
public class InputBox extends AbstractControllableGameWindowComponent {
    private long slashTime = 1000L;
    private String contentString = "";
    private int nowInsertPos = 0;
    private int nowSelectStartPos = -1;
    private int nowSelectEndPos = -1;

    private float charHeight =
            20.0f / this.getGameWindow().getLogicWindowHeight() * this.getGameWindow().getRealWindowHeight();

    private Vector4f textColor = new Vector4f(1, 1, 1, 1);
    private Vector4f textSelectColor = new Vector4f(0.3f, 0.5f, 0.5f, 1);
    private Vector4f insertColor = new Vector4f(1f, 1f, 1f, 1);


    public InputBox(GameWindow gameWindow) {
        super(gameWindow);
    }

    @Override
    public void initProcessors() {
        super.initProcessors();

        this.registerOnMouseButtonLeftDownCallback(
                (Event event) -> {
                    if (Thread.currentThread().getId() != 1) {
                        InputBox.this.getGameWindow().getGameManager().delayMainThreadEventProcess(InputBox.this.onMouseButtonLeftDownEventProcessor, event);
                    }
                    int clickIndex =
                            InputBox.this.drawTextGivenHeightLeftTopAndGetIndex(InputBox.this.getGameWindow().getMousePosX(),
                                    InputBox.this.getGameWindow().getMousePosY(), false, null);
                    setNowSelectStartPos(clickIndex);
                    setNowSelectEndPos(clickIndex);
                    setNowInsertPos(clickIndex);
                    InputBox.this.slashStartTime = System.currentTimeMillis();
                    return null;
                }
        );

        this.registerOnMouseLeaveAreaCallback(
                (Event event) -> InputBox.this.onMouseButtonLeftUp(null)
        );

        this.registerOnMouseButtonLeftUpCallback(
                (Event event) -> {
                    if (Thread.currentThread().getId() != 1) {
                        InputBox.this.getGameWindow().getGameManager().delayMainThreadEventProcess(InputBox.this.onMouseButtonLeftUpEventProcessor, event);
                    }
                    if (getNowSelectStartPos() < 0) {
                        return null;
                    }
                    int releaseIndex =
                            this.drawTextGivenHeightLeftTopAndGetIndex(this.getGameWindow().getMousePosX(),
                                    this.getGameWindow().getMousePosY(), false, null);
                    setNowSelectEndPos(releaseIndex);
                    setNowInsertPos(getNowSelectEndPos());
                    if (getNowSelectStartPos() > getNowSelectEndPos()) {
                        int tmpInt = getNowSelectStartPos();
                        setNowSelectStartPos(getNowSelectEndPos());
                        setNowSelectEndPos(tmpInt);
                    }
                    if (getNowSelectStartPos() < 0) {
                        setNowSelectStartPos(0);
                    }
                    if (getNowSelectStartPos() == getNowSelectEndPos()) {
                        setNowSelectStartPos(-1);
                        setNowSelectEndPos(-1);
                    }
                    return null;
                }
        );

        this.registerOnMouseButtonLeftPressingCallback(
                (Event event) -> {
                    if (Thread.currentThread().getId() != 1) {
                        InputBox.this.getGameWindow().getGameManager().delayMainThreadEventProcess(InputBox.this.onMouseButtonLeftPressingEventProcessor, event);
                    }
                    if (getNowSelectStartPos() == -1) {
                        return null;
                    }
                    int clickIndex =
                            this.drawTextGivenHeightLeftTopAndGetIndex(this.getGameWindow().getMousePosX(),
                                    this.getGameWindow().getMousePosY(), false, null);
                    setNowSelectEndPos(clickIndex);
                    return null;
                }
        );

        this.registerProcessor(KeyboardEvent.class.getCanonicalName(), event -> {
            if (Thread.currentThread().getId() != 1) {
                InputBox.this.getGameWindow().getGameManager().delayMainThreadEventProcess(InputBox.this.getProcessor(KeyboardEvent.class.getCanonicalName()), event);
            }
            synchronized (InputBox.this) {
                if (!this.isInFocusNow()) {
                    return event;
                }
                KeyboardEvent keyboardEvent = (KeyboardEvent) event;
                this.slashStartTime = System.currentTimeMillis();

                if (keyboardEvent.getAction() == GLFW_PRESS || keyboardEvent.getAction() == GLFW_REPEAT) {
                    Vector2f insertPos;
                    switch (keyboardEvent.getKeyRaw().getKey()) {
                        case GLFW_KEY_ENTER:
                            this.insertString("\n");
                            return null;
                        case GLFW_KEY_TAB:
                            this.insertString("  ");
                            return null;
                        case GLFW_KEY_ESCAPE:
                            this.loseFocus();
                            return null;
                        case GLFW_KEY_DELETE:
                            if (getNowSelectStartPos() != -1) {
                                insertStringToBetweenNowSelectStartPosAndNowSelectEndPos("");
                            } else {
                                setContentString(getContentString().substring(0,
                                        getNowInsertPos()) + (getNowInsertPos() < getContentString().length() ?
                                        getContentString().substring(getNowInsertPos() + 1)
                                        : ""));
                                this.limitNowInsertPos();
                            }
                            return null;
                        case GLFW_KEY_BACKSPACE:
                            if (getNowSelectStartPos() != -1) {
                                insertStringToBetweenNowSelectStartPosAndNowSelectEndPos("");
                            } else {
                                setContentString((getNowInsertPos() > 1 ?
                                        getContentString().substring(0,
                                                getNowInsertPos() - 1) : "") + (getNowInsertPos() <= getContentString().length() ? getContentString().substring(getNowInsertPos()) : ""));
                                setNowInsertPos(getNowInsertPos() - 1);
                                this.limitNowInsertPos();
                            }
                            return null;
                        case GLFW_KEY_LEFT:
                            setNowInsertPos(getNowInsertPos() - 1);
                            this.limitNowInsertPos();
                            return null;
                        case GLFW_KEY_RIGHT:
                            setNowInsertPos(getNowInsertPos() + 1);
                            this.limitNowInsertPos();
                            return null;
                        case GLFW_KEY_UP:
                            insertPos = new Vector2f();
                            drawTextGivenHeightLeftTopAndGetIndex(this.getGameWindow().getMousePosX(),
                                    this.getGameWindow().getMousePosY(), false, insertPos);
                            this.setNowInsertPos(drawTextGivenHeightLeftTopAndGetIndex(insertPos.x,
                                    insertPos.y - this.getCharHeight(), false, insertPos));
                            return null;
                        case GLFW_KEY_DOWN:
                            insertPos = new Vector2f();
                            drawTextGivenHeightLeftTopAndGetIndex(this.getGameWindow().getMousePosX(),
                                    this.getGameWindow().getMousePosY(), false, insertPos);
                            this.setNowInsertPos(drawTextGivenHeightLeftTopAndGetIndex(insertPos.x,
                                    insertPos.y + this.getCharHeight(), false, insertPos));
                            return null;
                        case GLFW_KEY_A:
                            if (keyboardEvent.getAction() == GLFW_PRESS && keyboardEvent.getMods() == GLFW_MOD_CONTROL) {
                                setNowSelectStartPos(0);
                                setNowSelectEndPos(this.getContentString().length());
                                setNowInsertPos(0);
                            }
                            return null;
                        case GLFW_KEY_C:
                            if (keyboardEvent.getAction() == GLFW_PRESS && keyboardEvent.getMods() == GLFW_MOD_CONTROL) {
                                if (getNowSelectStartPos() != -1) {
                                    if (getNowSelectEndPos() < getNowSelectStartPos()) {
                                        int tmpInt = getNowSelectStartPos();
                                        setNowSelectStartPos(getNowSelectEndPos());
                                        setNowSelectEndPos(tmpInt);
                                    }
                                    ClipboardUtil.setText(this.getContentString().substring(this.getNowSelectStartPos(),
                                            this.getNowSelectEndPos()));
                                    setNowSelectStartPos(-1);
                                    setNowSelectEndPos(-1);
                                } else {
                                    ClipboardUtil.setText("");
                                }
                            }
                            return null;
                        case GLFW_KEY_V:
                            if (keyboardEvent.getAction() == GLFW_PRESS && keyboardEvent.getMods() == GLFW_MOD_CONTROL) {
                                if (getNowSelectStartPos() != -1) {
                                    insertStringToBetweenNowSelectStartPosAndNowSelectEndPos(ClipboardUtil.getText());
                                } else {
                                    insertStringToInsertPos(ClipboardUtil.getText());
                                    this.limitNowInsertPos();
                                }
                            }
                            return null;
                        case GLFW_KEY_X:
                            if (keyboardEvent.getAction() == GLFW_PRESS && keyboardEvent.getMods() == GLFW_MOD_CONTROL) {
                                if (getNowSelectStartPos() != -1) {
                                    if (getNowSelectEndPos() < getNowSelectStartPos()) {
                                        int tmpInt = getNowSelectStartPos();
                                        setNowSelectStartPos(getNowSelectEndPos());
                                        setNowSelectEndPos(tmpInt);
                                    }
                                    ClipboardUtil.setText(this.getContentString().substring(this.getNowSelectStartPos(),
                                            this.getNowSelectEndPos()));
                                    insertStringToBetweenNowSelectStartPosAndNowSelectEndPos("");
                                    setNowSelectStartPos(-1);
                                    setNowSelectEndPos(-1);
                                } else {
                                    ClipboardUtil.setText("");
                                }
                            }
                            return null;
                        default:
                            return null;
                    }
                }
                return keyboardEvent;
            }
        });

        this.registerProcessor(CharEvent.class.getCanonicalName(), event -> {
            if (Thread.currentThread().getId() != 1) {
                InputBox.this.getGameWindow().getGameManager().delayMainThreadEventProcess(InputBox.this.getProcessor(CharEvent.class.getCanonicalName()), event);
            }
            synchronized (InputBox.this) {
                if (!this.isInFocusNow()) {
                    return event;
                }
                CharEvent charEvent = (CharEvent) event;
                this.insertString("" + (char) charEvent.getCodepoint());
                return null;
            }
        });
    }

    private void limitNowInsertPos() {
        if (getNowInsertPos() < 0) {
            setNowInsertPos(0);
        }
        if (getNowInsertPos() > getContentString().length()) {
            setNowInsertPos(getContentString().length());
        }
    }

    public void insertString(String insertString) {
        if (getNowSelectStartPos() == -1) {
            this.insertStringToInsertPos(insertString);
        } else {
            this.insertStringToBetweenNowSelectStartPosAndNowSelectEndPos(insertString);
        }
    }

    private void insertStringToInsertPos(String insertString) {
        limitNowInsertPos();
        setContentString(getContentString().substring(0, getNowInsertPos()) + insertString + getContentString().substring(getNowInsertPos()));
        setNowInsertPos(getNowInsertPos() + insertString.length());
        limitNowInsertPos();
    }

    private void insertStringToBetweenNowSelectStartPosAndNowSelectEndPos(String insertString) {
        if (insertString == null) {
            insertString = "";
        }
        if (getNowSelectEndPos() < getNowSelectStartPos()) {
            int tmpInt = getNowSelectStartPos();
            setNowSelectStartPos(getNowSelectEndPos());
            setNowSelectEndPos(tmpInt);
        }
        setContentString(getContentString().substring(0,
                getNowSelectStartPos()) + insertString + getContentString().substring(getNowSelectEndPos()
        ));
        setNowInsertPos(getNowSelectStartPos() + insertString.length());
        limitNowInsertPos();
        setNowSelectStartPos(-1);
        setNowSelectEndPos(-1);
    }


    @Override
    public void ifVisibleThenDraw() {
        this.drawTextGivenHeightLeftTopAndGetIndex(this.getGameWindow().getMousePosX(),
                this.getGameWindow().getMousePosY(), true, null);
    }

    private long slashStartTime = 0;

    public int drawTextGivenHeightLeftTopAndGetIndex(float distPosX,
                                                     float distPosY,
                                                     boolean ifDraw,
                                                     Vector2f insertPos) {
        float realLeftTopPosX =
                this.getLeftTopPosX() / this.getGameWindow().getLogicWindowWidth() * this.getGameWindow().getRealWindowWidth();
        float realLeftTopPosY =
                this.getLeftTopPosY() / this.getGameWindow().getLogicWindowHeight() * this.getGameWindow().getRealWindowHeight();
        float realWidth =
                this.getWidth() / this.getGameWindow().getLogicWindowWidth() * this.getGameWindow().getRealWindowWidth();
        float realHeight =
                this.getHeight() / this.getGameWindow().getLogicWindowHeight() * this.getGameWindow().getRealWindowHeight();
        float realCharHeight =
                this.getCharHeight() / this.getGameWindow().getLogicWindowHeight() * this.getGameWindow().getRealWindowHeight();


        float insPosX0 = 0;
        float insPosY0 = 0;
        float insPosX1 = 0;
        float insPosY1 = 0;

        Font font = Font.getCurrentFont();
        String[] strings = this.getContentString().split("\n", -1);

        float minDist = Float.MAX_VALUE;

        int index = 0;
        int resIndex = -1;
        int resLineIndex = -1;

        for (int j = 0; j < strings.length; j++) {
            String line = strings[j];

            float x1 = realLeftTopPosX;
            float y1 = realLeftTopPosY + realCharHeight * j;
            float height = realCharHeight;
            float characterSpace = 0;
            String text = line;

            font.bind();
            float scaleY = font.getScale(height);
            float scaleX = scaleY;


            float x = x1;
            float y = y1;
            font.getXb().put(0, x);
            font.getYb().put(0, y);

            font.getCharData().position(0);
            glEnable(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, font.getFontTexture());

            if (this.getTextColor() != null) {
                glColor4f(this.getTextColor().x, this.getTextColor().y,
                        this.getTextColor().z, this.getTextColor().w);
            }

            glBegin(GL_QUADS);
            float lastXReal = x;
            float lastYReal = y;
            float lastXShould = x;
            float lastYShould = y;
            for (int i = 0; i < text.length(); i++) {
                stbtt_GetPackedQuad(font.getCharData(), Font.BITMAP_W,
                        Font.BITMAP_H, text.charAt(i), font.getXb(),
                        font.getYb(), font.getQ(), false);
                float charWidthShould = font.getQ().x1() - font.getQ().x0();
                float charHeightShould = font.getQ().y1() - font.getQ().y0();
                float spaceLeftToCharShould = font.getQ().x0() - lastXShould;
                float spaceUpToCharShould = font.getQ().y0() - lastYShould;
                float nowX0 = lastXReal + spaceLeftToCharShould * scaleX;
                float nowY0 = y + spaceUpToCharShould * scaleY;

                if ((index >= getNowSelectStartPos() && index < getNowSelectEndPos()) || (index < getNowSelectStartPos() && index >= getNowSelectEndPos())) {
                    glColor4f(this.getTextSelectColor().x,
                            this.getTextSelectColor().y,
                            this.getTextSelectColor().z,
                            this.getTextSelectColor().w);
                } else {
                    glColor4f(this.getTextColor().x, this.getTextColor().y,
                            this.getTextColor().z, this.getTextColor().w);
                }
                if (ifDraw) {
                    Font.drawBoxTC(
                            nowX0, nowY0 + height * 0.8f,
                            nowX0 + charWidthShould * scaleX,
                            nowY0 + charHeightShould * scaleY + height * 0.8f,
                            font.getQ().s0(), font.getQ().t0(),
                            font.getQ().s1(), font.getQ().t1()
                    );
                }

                float newX0 = nowX0;
                float newX1 = nowX0 + charWidthShould * scaleX;
                float newY0 = nowY0 + height * 0.8f;
                float newY1 = nowY0 + charHeightShould * scaleY + height * 0.8f;

                if (distPosX >= newX0 && distPosX <= newX1 && distPosY >= newY0 && distPosY <= newY1) {
                    resIndex = index;
                }
                if (distPosY >= newY0 && distPosY <= newY1) {
                    resLineIndex = j;
                }
                if (newY1 >= distPosY && resLineIndex == -1) {
                    resLineIndex = j;
                }

                if (j == strings.length - 1 && resLineIndex == -1) {
                    resLineIndex = j;
                }

                if (distPosX >= newX0 && distPosX <= newX1 && resLineIndex == j) {
                    resIndex = index;
                }

                if (index == this.getNowInsertPos()) {
                    insPosX0 = lastXReal;
                    if (i != 0 && text.charAt(i - 1) == ' ') {
                        insPosX0 = nowX0;
                    }

                    insPosY0 = realLeftTopPosY + realCharHeight * j;
                    insPosX1 = insPosX0 + 5;
                    insPosY1 = insPosY0 + realCharHeight;
                    if (insertPos != null) {
                        insertPos.set(lastXReal,
                                realLeftTopPosY + realCharHeight * j + realCharHeight / 2);
                    }
                }
                if (i == line.length() - 1) {
                    newY0 = nowY0 + height * 0.8f;
                    newY1 = nowY0 + charHeightShould * scaleY + height * 0.8f;
                    if (distPosY >= newY0 && distPosY <= newY1) {
                        resLineIndex = j;
                    }
                    if (newY1 >= distPosY && resLineIndex == -1) {
                        resLineIndex = j;
                    }

                    if (index + 1 == this.getNowInsertPos()) {
                        insPosX0 = nowX0 + charWidthShould * scaleX;
                        insPosY0 = realLeftTopPosY + realCharHeight * j;
                        insPosX1 = insPosX0 + 5;
                        insPosY1 = insPosY0 + realCharHeight;
                        if (insertPos != null) {
                            insertPos.set(nowX0 + charWidthShould * scaleX,
                                    realLeftTopPosY + realCharHeight * j + realCharHeight / 2);
                        }
                    }
                }

                lastXReal = nowX0 + charWidthShould * scaleX;
                lastYReal = y;
                lastXShould = font.getQ().x1();
                lastYShould = y;
                index++;


                if (resIndex == -1 && resLineIndex == j && i == line.length() - 1) {
                    int index2 = index - line.length();
                    lastXReal = x;

                    for (int i2 = 0; i2 < line.length(); i2++) {

                        stbtt_GetPackedQuad(font.getCharData(), Font.BITMAP_W
                                , Font.BITMAP_H, text.charAt(i2),
                                font.getXb(), font.getYb(), font.getQ(), false);
                        charWidthShould = font.getQ().x1() - font.getQ().x0();
                        charHeightShould = font.getQ().y1() - font.getQ().y0();
                        spaceLeftToCharShould = font.getQ().x0() - lastXShould;
                        spaceUpToCharShould = font.getQ().y0() - lastYShould;
                        nowX0 = lastXReal + spaceLeftToCharShould * scaleX;
                        nowY0 = y + spaceUpToCharShould * scaleY;

                        if ((index >= getNowSelectStartPos() && index < getNowSelectEndPos()) || (index < getNowSelectStartPos() && index >= getNowSelectEndPos())) {
                            glColor4f(this.getTextSelectColor().x,
                                    this.getTextSelectColor().y,
                                    this.getTextSelectColor().z,
                                    this.getTextSelectColor().w);
                        } else {
                            glColor4f(this.getTextColor().x,
                                    this.getTextColor().y,
                                    this.getTextColor().z,
                                    this.getTextColor().w);
                        }
                        float nowDist = Math.abs(distPosX - nowX0);
                        if (nowDist < minDist) {
                            minDist = nowDist;
                            resIndex = index2;
                        }
                        if (i2 == line.length() - 1) {
                            nowDist =
                                    Math.abs(distPosX - (nowX0 + charWidthShould * scaleX));
                            if (nowDist < minDist) {
                                minDist = nowDist;
                                resIndex = index2 + 1;
                            }
                        }
                        index2++;
                    }
                }
            }

            if (line.isEmpty()) {
                float nowX0 = x;
                float nowY0 = y;

                float newX0 = nowX0;
                float newX1 = nowX0 + realCharHeight * scaleX;
                float newY0 = nowY0 + height * 0.8f;
                float newY1 = nowY0 + realCharHeight * scaleY + height * 0.8f;

                if (distPosY >= newY0 && distPosY <= newY1) {
                    resLineIndex = j;
                }
                if (newY1 >= distPosY && resLineIndex == -1) {
                    resLineIndex = j;
                }
                if (j == strings.length - 1 && resLineIndex == -1) {
                    resLineIndex = j;
                }

                if (resLineIndex == j && distPosX >= nowX0) {
                    resIndex = index;
                }

                if (index == this.getNowInsertPos()) {
                    insPosX0 = lastXReal;
                    insPosY0 = realLeftTopPosY + realCharHeight * j;
                    insPosX1 = lastXReal + 5;
                    insPosY1 =
                            realLeftTopPosY + realCharHeight * j + realCharHeight;
                    if (insertPos != null) {
                        insertPos.set(lastXReal,
                                realLeftTopPosY + realCharHeight * j + realCharHeight / 2);
                    }
                }
            }

            if (ifDraw && (((System.currentTimeMillis() - this.slashStartTime) / this.getSlashTime()) % 2 == 0)) {
                glColor4f(getInsertColor().x(), getInsertColor().y(),
                        getInsertColor().z(), getInsertColor().w());
                stbtt_GetPackedQuad(font.getCharData(), Font.BITMAP_W,
                        Font.BITMAP_H, '|', font.getXb(), font.getYb(),
                        font.getQ(), false);
                Font.drawBoxTC(
                        insPosX0, insPosY0 - (insPosY1 - insPosY0) / 4,
                        insPosX1 - (insPosX1 - insPosX0) / 3 * 2,
                        insPosY1 + (insPosY1 - insPosY0) / 4,
                        font.getQ().s0(), font.getQ().t0(),
                        font.getQ().s1(), font.getQ().t1()
                );
            }

            glEnd();
            index++;
        }

        if (resIndex == -1) {
            return 0;
        } else {
            return resIndex;
        }

    }

    public long getSlashTime() {
        return slashTime;
    }

    public void setSlashTime(long slashTime) {
        this.slashTime = slashTime;
    }

    public String getContentString() {
        return contentString;
    }

    public void setContentString(String contentString) {
        this.contentString = contentString;
    }

    public int getNowInsertPos() {
        return nowInsertPos;
    }

    public void setNowInsertPos(int nowInsertPos) {
        this.nowInsertPos = nowInsertPos;
    }

    public int getNowSelectStartPos() {
        return nowSelectStartPos;
    }

    public void setNowSelectStartPos(int nowSelectStartPos) {
        this.nowSelectStartPos = nowSelectStartPos;
    }

    public int getNowSelectEndPos() {
        return nowSelectEndPos;
    }

    public void setNowSelectEndPos(int nowSelectEndPos) {
        this.nowSelectEndPos = nowSelectEndPos;
    }

    public float getCharHeight() {
        return charHeight;
    }

    public void setCharHeight(float charHeight) {
        this.charHeight = charHeight;
    }

    public Vector4f getTextColor() {
        return textColor;
    }

    public void setTextColor(Vector4f textColor) {
        this.textColor = textColor;
    }

    public Vector4f getTextSelectColor() {
        return textSelectColor;
    }

    public void setTextSelectColor(Vector4f textSelectColor) {
        this.textSelectColor = textSelectColor;
    }

    public Vector4f getInsertColor() {
        return insertColor;
    }

    public void setInsertColor(Vector4f insertColor) {
        this.insertColor = insertColor;
    }

}

