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

package com.xenoamess.cyan_potion.base.gameWindowComponents.ControlableGameWindowComponents;

import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.events.CharEvent;
import com.xenoamess.cyan_potion.base.events.Event;
import com.xenoamess.cyan_potion.base.events.KeyEvent;
import com.xenoamess.cyan_potion.base.io.ClipboardUtil;
import com.xenoamess.cyan_potion.base.visual.Font;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBTruetype.stbtt_GetPackedQuad;


/**
 * @author XenoAmess
 */
public class InputBox extends AbstractControlableGameWindowComponent {
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

//    public Texture backgroundTexture;
//            this.this.getGameWindow().gameManager.resourceManager
//            .fetchResourceWithShortenURI(Texture.class,
//            "/www/img/pictures/saveSlot.png:picture");

    {
        this.registerOnMouseButtonLeftDownCallback(
                (Event event) -> {
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
                (Event event) -> {
                    return InputBox.this.onMouseButtonLeftUp(null);
                }
        );

        this.registerOnMouseButtonLeftUpCallback(
                (Event event) -> {
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
    }

    public InputBox(GameWindow gameWindow) {
        super(gameWindow);
//        this.backgroundTexture = backgroundTexture;
    }

    @Override
    public void initProcessors() {
        super.initProcessors();
        this.registerProcessor(KeyEvent.class.getCanonicalName(), event -> {
            if (!this.isInFocusNow()) {
                return event;
            }
            KeyEvent keyEvent = (KeyEvent) event;
            this.slashStartTime = System.currentTimeMillis();

            if (keyEvent.getAction() == GLFW_PRESS || keyEvent.getAction() == GLFW_REPEAT) {
                Vector2f insertPos;
                switch (keyEvent.getKeyRaw().getKey()) {
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
                                    getContentString().substring(getNowInsertPos() + 1, getContentString().length())
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
                                            getNowInsertPos() - 1) : "") + (getNowInsertPos() <= getContentString().length() ? getContentString().substring(getNowInsertPos(), getContentString().length()) : ""));
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
                        if (keyEvent.getAction() == GLFW_PRESS && keyEvent.getMods() == GLFW_MOD_CONTROL) {
                            setNowSelectStartPos(0);
                            setNowSelectEndPos(this.getContentString().length());
                            setNowInsertPos(0);
                        }
                        return null;
                    case GLFW_KEY_C:
                        if (keyEvent.getAction() == GLFW_PRESS && keyEvent.getMods() == GLFW_MOD_CONTROL) {
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
                        if (keyEvent.getAction() == GLFW_PRESS && keyEvent.getMods() == GLFW_MOD_CONTROL) {
                            if (getNowSelectStartPos() != -1) {
                                insertStringToBetweenNowSelectStartPosAndNowSelectEndPos(ClipboardUtil.getText());
                            } else {
                                insertStringToInsertPos(ClipboardUtil.getText());
                                this.limitNowInsertPos();
                            }
                        }
                        return null;
                    case GLFW_KEY_X:
                        if (keyEvent.getAction() == GLFW_PRESS && keyEvent.getMods() == GLFW_MOD_CONTROL) {
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
            return keyEvent;
        });

        this.registerProcessor(CharEvent.class.getCanonicalName(), event -> {
            if (!this.isInFocusNow()) {
                return event;
            }
            CharEvent charEvent = (CharEvent) event;
            this.insertString("" + (char) charEvent.getCodepoint());
            return null;
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
        setContentString(getContentString().substring(0, getNowInsertPos()) + insertString + getContentString().substring(getNowInsertPos(), getContentString().length()));
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
                getNowSelectStartPos()) + insertString + getContentString().substring(getNowSelectEndPos(),
                getContentString().length()));
        setNowInsertPos(getNowSelectStartPos() + insertString.length());
        limitNowInsertPos();
        setNowSelectStartPos(-1);
        setNowSelectEndPos(-1);
    }


    @Override
    public void ifVisibleThenDraw() {
//        this.getGameWindow().drawBindableRelativeLeftTop(this
//        .backgroundTexture, this.leftTopPosX, this.leftTopPosY, this.width,
//        this.height);
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
//        int minDistIndex = -1;

        int index = 0;
        int resIndex = -1;
        int resLineIndex = -1;
//        int lineIndex = 0;

        for (int j = 0; j < strings.length; j++) {
            String line = strings[j];
//            LOGGER.debug(line);

            float x1 = realLeftTopPosX;
            float y1 = realLeftTopPosY + realCharHeight * j;
            float height = realCharHeight;
            float characterSpace = 0;
            String text = line;

//        LOGGER.debug("!!! x:" + x + " y:" + y);
            font.bind();
            float scaley = font.getScale(height);
            float scalex = scaley;

//        drawText(x1, y1, scalex, scaley, characterSpace, color, text);

            float x = x1;
            float y = y1;
            //        LOGGER.debug("!!! x:" + x + " y:" + y);
            font.getXb().put(0, x);
            font.getYb().put(0, y);

//        chardata.position(font * MAX_NUM);
            font.getChardata().position(0);
            glEnable(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, font.getFontTexture());

            if (this.getTextColor() != null) {
                glColor4f(this.getTextColor().x, this.getTextColor().y,
                        this.getTextColor().z, this.getTextColor().w);
            }

            glBegin(GL_QUADS);
            float lastxReal = x;
            float lastyReal = y;
            float lastxShould = x;
            float lastyShould = y;
            for (int i = 0; i < text.length(); i++) {
                stbtt_GetPackedQuad(font.getChardata(), Font.BITMAP_W,
                        Font.BITMAP_H, text.charAt(i), font.getXb(),
                        font.getYb(), font.getQ(), false);
//            LOGGER.debug("x0:" + q.x0() + " x1:" + q.x1() + " y0:" +
//            q.y0() + " y1:" + q.y1());
                float charWidthShould = font.getQ().x1() - font.getQ().x0();
                float charHeightShould = font.getQ().y1() - font.getQ().y0();
                float spaceLeftToCharShould = font.getQ().x0() - lastxShould;
                float spaceUpToCharShould = font.getQ().y0() - lastyShould;
                float nowx0 = lastxReal + spaceLeftToCharShould * scalex;
                float nowy0 = y + spaceUpToCharShould * scaley;

//            LOGGER.debug(charWidthShould + " " + charHeightShould + "
//            " + spaceLeftToCharShould + " " + spaceUpToCharShould + " " +
//            nowx0 + " " + nowy0);
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
                            nowx0, nowy0 + height * 0.8f,
                            nowx0 + charWidthShould * scalex,
                            nowy0 + charHeightShould * scaley + height * 0.8f,
                            font.getQ().s0(), font.getQ().t0(),
                            font.getQ().s1(), font.getQ().t1()
                    );
                }
//                float nowx;
//                float nowy;
//                float nowDist;

//                nowx = nowx0;
//                nowy = (nowy0 + height * 0.8f + nowy0 + charHeightShould *
//                scaley + height * 0.8f) / 2;

                float newx0 = nowx0;
                float newx1 = nowx0 + charWidthShould * scalex;
                float newy0 = nowy0 + height * 0.8f;
                float newy1 = nowy0 + charHeightShould * scaley + height * 0.8f;

                if (distPosX >= newx0 && distPosX <= newx1 && distPosY >= newy0 && distPosY <= newy1) {
                    resIndex = index;
                }
                if (distPosY >= newy0 && distPosY <= newy1) {
                    resLineIndex = j;
                }
                if (newy1 >= distPosY && resLineIndex == -1) {
                    resLineIndex = j;
                }

                if (j == strings.length - 1 && resLineIndex == -1) {
                    resLineIndex = j;
                }

                if (distPosX >= newx0 && distPosX <= newx1 && resLineIndex == j) {
                    resIndex = index;
                }

//                nowDist = (distPosX - nowx) * (distPosX - nowx) + (distPosY
//                - nowy) * (distPosY - nowy) * 1e3f;
//                if (minDist > nowDist) {
//                    minDist = nowDist;
//                    minIndex = index;
//                }

                if (index == this.getNowInsertPos()) {
                    insPosX0 = lastxReal;
                    if (i != 0 && text.charAt(i - 1) == ' ') {
                        insPosX0 = nowx0;
                    }

                    insPosY0 = realLeftTopPosY + realCharHeight * j;
                    insPosX1 = insPosX0 + 5;
                    insPosY1 = insPosY0 + realCharHeight;
                    if (insertPos != null) {
                        insertPos.set(lastxReal,
                                realLeftTopPosY + realCharHeight * j + realCharHeight / 2);
                    }
                }
                if (i == line.length() - 1) {
//                    nowx = nowx0 + charWidthShould * scalex;
//                    nowy = (nowy0 + height * 0.8f + nowy0 +
//                    charHeightShould * scaley + height * 0.8f) / 2;
//                    nowDist = (distPosX - nowx) * (distPosX - nowx) +
//                    (distPosY - nowy) * (distPosY - nowy) * 1e3f;
//                    if (minDist > nowDist) {
//                        minDist = nowDist;
//                        minIndex = index + 1;
//                    }
                    newy0 = nowy0 + height * 0.8f;
                    newy1 = nowy0 + charHeightShould * scaley + height * 0.8f;
                    if (distPosY >= newy0 && distPosY <= newy1) {
                        resLineIndex = j;
                    }
                    if (newy1 >= distPosY && resLineIndex == -1) {
                        resLineIndex = j;
                    }

                    if (index + 1 == this.getNowInsertPos()) {
                        insPosX0 = nowx0 + charWidthShould * scalex;
                        insPosY0 = realLeftTopPosY + realCharHeight * j;
                        insPosX1 = insPosX0 + 5;
                        insPosY1 = insPosY0 + realCharHeight;
                        if (insertPos != null) {
                            insertPos.set(nowx0 + charWidthShould * scalex,
                                    realLeftTopPosY + realCharHeight * j + realCharHeight / 2);
                        }
                    }
                }

                lastxReal = nowx0 + charWidthShould * scalex;
                lastyReal = y;
                lastxShould = font.getQ().x1();
                lastyShould = y;
                index++;


                if (resIndex == -1 && resLineIndex == j && i == line.length() - 1) {
                    int index2 = index - line.length();
                    lastxReal = x;

                    for (int i2 = 0; i2 < line.length(); i2++) {

                        stbtt_GetPackedQuad(font.getChardata(), Font.BITMAP_W
                                , Font.BITMAP_H, text.charAt(i2),
                                font.getXb(), font.getYb(), font.getQ(), false);
//            LOGGER.debug("x0:" + q.x0() + " x1:" + q.x1() + " y0:" +
//            q.y0() + " y1:" + q.y1());
                        charWidthShould = font.getQ().x1() - font.getQ().x0();
                        charHeightShould = font.getQ().y1() - font.getQ().y0();
                        spaceLeftToCharShould = font.getQ().x0() - lastxShould;
                        spaceUpToCharShould = font.getQ().y0() - lastyShould;
                        nowx0 = lastxReal + spaceLeftToCharShould * scalex;
                        nowy0 = y + spaceUpToCharShould * scaley;

//            LOGGER.debug(charWidthShould + " " + charHeightShould + "
//            " + spaceLeftToCharShould + " " + spaceUpToCharShould + " " +
//            nowx0 + " " + nowy0);

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
                        float nowDist = Math.abs(distPosX - nowx0);
                        if (nowDist < minDist) {
                            minDist = nowDist;
                            resIndex = index2;
                        }
                        if (i2 == line.length() - 1) {
                            nowDist =
                                    Math.abs(distPosX - (nowx0 + charWidthShould * scalex));
                            if (nowDist < minDist) {
                                minDist = nowDist;
                                resIndex = index2 + 1;
                            }
                        }
                        index2++;
                    }
                }
            }
//            LOGGER.debug(line.length());
            if (line.isEmpty()) {
                float nowx0 = x;
                float nowy0 = y;

                float newx0 = nowx0;
                float newx1 = nowx0 + realCharHeight * scalex;
                float newy0 = nowy0 + height * 0.8f;
                float newy1 = nowy0 + realCharHeight * scaley + height * 0.8f;

                if (distPosY >= newy0 && distPosY <= newy1) {
                    resLineIndex = j;
                }
                if (newy1 >= distPosY && resLineIndex == -1) {
                    resLineIndex = j;
                }
                if (j == strings.length - 1 && resLineIndex == -1) {
                    resLineIndex = j;
                }

                if (resLineIndex == j && distPosX >= nowx0) {
                    resIndex = index;
                }

                if (index == this.getNowInsertPos()) {
                    insPosX0 = lastxReal;
                    insPosY0 = realLeftTopPosY + realCharHeight * j;
                    insPosX1 = lastxReal + 5;
                    insPosY1 =
                            realLeftTopPosY + realCharHeight * j + realCharHeight;
                    if (insertPos != null) {
                        insertPos.set(lastxReal,
                                realLeftTopPosY + realCharHeight * j + realCharHeight / 2);
                    }
                }
            }

            if (ifDraw) {
                if (((System.currentTimeMillis() - this.slashStartTime) / this.getSlashTime()) % 2 == 0) {
                    glColor4f(getInsertColor().x(), getInsertColor().y(),
                            getInsertColor().z(), getInsertColor().w());
                    stbtt_GetPackedQuad(font.getChardata(), Font.BITMAP_W,
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
            }

            glEnd();
            index++;
        }

        if (resIndex == -1) {
            return 0;
        } else {
            return resIndex;
        }
//        return resIndex;
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

//    @Override
//    public void update() {
//        super.update();
//        if(){
//
//        }
//    }
}


//    public Vector2f getNextPos(char nextChar, float lastPosX, float
//    lastPosY) {
//
//    }

