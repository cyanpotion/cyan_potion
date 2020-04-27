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

package com.xenoamess.cyan_potion.base.game_window_components.controllable_game_window_components;

import com.xenoamess.commonx.java.lang.IllegalArgumentExceptionUtilsx;
import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.io.ClipboardUtil;
import com.xenoamess.cyan_potion.base.io.input.keyboard.KeyboardEvent;
import com.xenoamess.cyan_potion.base.io.input.keyboard.TextEvent;
import com.xenoamess.cyan_potion.base.io.input.mouse.MouseButtonEvent;
import com.xenoamess.cyan_potion.base.visual.Font;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.joml.Vector4fc;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.xenoamess.cyan_potion.base.visual.Font.EACH_CHAR_NUM;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBTruetype.stbtt_GetPackedQuad;


/**
 * <p>InputBox class.</p>
 *
 * @author XenoAmess
 * @version 0.162.2-SNAPSHOT
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public class InputBox extends AbstractControllableGameWindowComponent {

    /**
     * Constant <code>NEXT_LINE_STRING="\n"</code>
     */
    public static final String NEXT_LINE_STRING = "\n";

    /**
     * the time between every single slash shines.
     */
    @Getter
    @Setter
    private long cursorShineTime = 1000L;

    @Getter
    @Setter
    private String contentString = "";

    @Getter
    @Setter
    private int nowInsertPos = 0;

    @Getter
    @Setter
    private int nowSelectStartPos = -1;

    @Getter
    @Setter
    private int nowSelectEndPos = -1;

    @Getter
    @Setter
    private float charHeight =
            20.0f / this.getGameWindow().getLogicWindowHeight() * this.getGameWindow().getRealWindowHeight();

    private final Vector4f textColor = new Vector4f(1, 1, 1, 1);

    private final Vector4f textSelectColor = new Vector4f(0.3f, 0.5f, 0.5f, 1);

    private final Vector4f cursorColor = new Vector4f(1f, 1f, 1f, 1);

    /**
     * if allowMultiLine is true then this InputBox allows multiline.
     * otherwise does not allow multiLine(all contents must be in one line. all /n will be deleted).
     */
    private final AtomicBoolean allowMultiLine = new AtomicBoolean(true);

    @Getter
    @Setter
    private int contentStringLengthLimit = -1;

    /**
     * if useAllowCharSet is true then this InputBox use {@link #allowCharSet},
     * and only chars in {@link #allowCharSet} are allowed in this InputBox.
     */
    private final AtomicBoolean useAllowCharSet = new AtomicBoolean(true);

    /**
     * @see #useAllowCharSet
     */
    @Getter
    private final Set<Character> allowCharSet = new HashSet<>();

    /**
     * if useAllowCharSet is true then this InputBox use {@link #disallowCharSet},
     * and only chars not in {@link #disallowCharSet} are allowed in this InputBox.
     */
    private final AtomicBoolean useDisallowCharSet = new AtomicBoolean(true);

    /**
     * @see #useDisallowCharSet
     */
    @Getter
    private final Set<Character> disallowCharSet = new HashSet<>();

    /**
     * <p>Constructor for InputBox.</p>
     *
     * @param gameWindow gameWindow
     */
    public InputBox(GameWindow gameWindow) {
        super(gameWindow);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initProcessors() {
        super.initProcessors();
        this.registerOnMouseButtonLeftDownCallback(new MainThreadEventProcessor<MouseButtonEvent>(
                this,
                (MouseButtonEvent event) -> {
                    int clickIndex =
                            InputBox.this.drawTextGivenHeightLeftTopAndGetIndex(event.getMousePosX(),
                                    event.getMousePosY(), false, null);
                    setNowSelectStartPos(clickIndex);
                    setNowSelectEndPos(clickIndex);
                    setNowInsertPos(clickIndex);
                    InputBox.this.slashStartTime = System.currentTimeMillis();
                    return null;
                })
        );

//        this.registerOnMouseLeaveAreaCallback(
//                (MouseButtonEvent event) -> InputBox.this.onMouseButtonLeftUp(MouseButtonEvent.generateEmptyMouseButtonEvent(this.getGameWindow()))
//        );

        this.registerOnMouseButtonLeftUpCallback(
                new MainThreadEventProcessor<MouseButtonEvent>(
                        this,
                        (MouseButtonEvent event) -> {
                            if (getNowSelectStartPos() < 0) {
                                return null;
                            }
                            IllegalArgumentExceptionUtilsx.isAnyNullInParamsThenThrowIllegalArgumentException(event);
                            int releaseIndex =
                                    this.drawTextGivenHeightLeftTopAndGetIndex(event.getMousePosX(),
                                            event.getMousePosY(), false, null);
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
                )
        );

        this.registerOnMouseButtonLeftPressingCallback(
                new MainThreadEventProcessor<MouseButtonEvent>(
                        this,
                        (MouseButtonEvent event) -> {
                            if (getNowSelectStartPos() == -1) {
                                return null;
                            }
                            int clickIndex =
                                    this.drawTextGivenHeightLeftTopAndGetIndex(event.getMousePosX(),
                                            event.getMousePosY(), false, null);
                            setNowSelectEndPos(clickIndex);
                            return null;
                        }
                )
        );

        this.registerProcessor(KeyboardEvent.class,
                new MainThreadEventProcessor<KeyboardEvent>(
                        this,
                        (KeyboardEvent keyboardEvent) -> {
                            synchronized (InputBox.this) {
                                if (!this.isInFocusNow()) {
                                    return keyboardEvent;
                                }
                                this.slashStartTime = System.currentTimeMillis();

                                if (keyboardEvent.getAction() == GLFW_PRESS || keyboardEvent.getAction() == GLFW_REPEAT) {
                                    Vector2f insertPos;
                                    switch (keyboardEvent.getKeyRaw().getKey()) {
                                        case GLFW_KEY_ENTER:
                                            if (this.isAllowMultiLine()) {
                                                this.insertString(NEXT_LINE_STRING);
                                            }
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
                        }
                )
        );

        this.registerProcessor(TextEvent.class,
                new MainThreadEventProcessor<TextEvent>(
                        this,
                        (TextEvent textEvent) -> {
                            synchronized (InputBox.this) {
                                if (!this.isInFocusNow()) {
                                    return textEvent;
                                }
                                this.insertString(textEvent.getContentString());
                                return null;
                            }
                        }
                )
        );
    }

    private void limitNowInsertPos() {
        if (getNowInsertPos() < 0) {
            setNowInsertPos(0);
        }
        if (getNowInsertPos() > getContentString().length()) {
            setNowInsertPos(getContentString().length());
        }
    }

    /**
     * <p>insertString.</p>
     *
     * @param insertString insertString
     */
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


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean ifVisibleThenDraw() {
        this.drawTextGivenHeightLeftTopAndGetIndex(Float.NaN,
                Float.NaN, true, null);
        return true;
    }

    private long slashStartTime = 0;

    /**
     * <p>drawTextGivenHeightLeftTopAndGetIndex.</p>
     *
     * @param distPosX  a float.
     * @param distPosY  a float.
     * @param ifDraw    a boolean.
     * @param insertPos insertPos
     * @return a int.
     */
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
        String[] strings = this.getContentString().split(NEXT_LINE_STRING, -1);

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

            glEnable(GL_TEXTURE_2D);

            if (this.getTextColor() != null) {
                glColor4f(
                        this.getTextColor().x(),
                        this.getTextColor().y(),
                        this.getTextColor().z(),
                        this.getTextColor().w()
                );
            }
            glBegin(GL_QUADS);
            float lastXReal = x;
            float lastYReal = y;
            float lastXShould = x;
            float lastYShould = y;
            for (int i = 0; i < text.length(); i++) {
                if (text.charAt(i) < 32) {
                    continue;
                }
                glEnd();
                glBindTexture(GL_TEXTURE_2D,
                        font.getFontTextures().getPrimitive(text.charAt(i) / EACH_CHAR_NUM));
                glBegin(GL_QUADS);
                stbtt_GetPackedQuad(font.getCharDatas().get(text.charAt(i) / EACH_CHAR_NUM), Font.BITMAP_W,
                        Font.BITMAP_H, text.charAt(i) % EACH_CHAR_NUM, font.getXb(),
                        font.getYb(), font.getQ(), false);
                float charWidthShould = font.getQ().x1() - font.getQ().x0();
                float charHeightShould = font.getQ().y1() - font.getQ().y0();
                float spaceLeftToCharShould = font.getQ().x0() - lastXShould;
                float spaceUpToCharShould = font.getQ().y0() - lastYShould;
                float nowX0 = lastXReal + spaceLeftToCharShould * scaleX;
                float nowY0 = y + spaceUpToCharShould * scaleY;

                if ((index >= getNowSelectStartPos() && index < getNowSelectEndPos()) || (index < getNowSelectStartPos() && index >= getNowSelectEndPos())) {
                    glColor4f(this.getTextSelectColor().x(),
                            this.getTextSelectColor().y(),
                            this.getTextSelectColor().z(),
                            this.getTextSelectColor().w());
                } else {
                    glColor4f(this.getTextColor().x(), this.getTextColor().y(),
                            this.getTextColor().z(), this.getTextColor().w());
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
                        if (text.charAt(i2) < 32) {
                            continue;
                        }
                        glEnd();
                        glBindTexture(GL_TEXTURE_2D,
                                font.getFontTextures().getPrimitive(text.charAt(i2) / EACH_CHAR_NUM));
                        glBegin(GL_QUADS);
                        stbtt_GetPackedQuad(font.getCharDatas().get(text.charAt(i2) / EACH_CHAR_NUM), Font.BITMAP_W
                                , Font.BITMAP_H, text.charAt(i2) % EACH_CHAR_NUM,
                                font.getXb(), font.getYb(), font.getQ(), false);
                        charWidthShould = font.getQ().x1() - font.getQ().x0();
                        charHeightShould = font.getQ().y1() - font.getQ().y0();
                        spaceLeftToCharShould = font.getQ().x0() - lastXShould;
                        spaceUpToCharShould = font.getQ().y0() - lastYShould;
                        nowX0 = lastXReal + spaceLeftToCharShould * scaleX;
                        nowY0 = y + spaceUpToCharShould * scaleY;

                        if ((index >= getNowSelectStartPos() && index < getNowSelectEndPos()) || (index < getNowSelectStartPos() && index >= getNowSelectEndPos())) {
                            glColor4f(this.getTextSelectColor().x(),
                                    this.getTextSelectColor().y(),
                                    this.getTextSelectColor().z(),
                                    this.getTextSelectColor().w());
                        } else {
                            glColor4f(this.getTextColor().x(),
                                    this.getTextColor().y(),
                                    this.getTextColor().z(),
                                    this.getTextColor().w());
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

            if (ifDraw && (((System.currentTimeMillis() - this.slashStartTime) / this.getCursorShineTime()) % 2 == 0)) {
                glColor4f(getCursorColor().x(), getCursorColor().y(),
                        getCursorColor().z(), getCursorColor().w());
                glEnd();
                glBindTexture(GL_TEXTURE_2D,
                        font.getFontTextures().getPrimitive('|' / EACH_CHAR_NUM));
                glBegin(GL_QUADS);
                stbtt_GetPackedQuad(font.getCharDatas().get('|' / EACH_CHAR_NUM), Font.BITMAP_W,
                        Font.BITMAP_H, '|' % EACH_CHAR_NUM, font.getXb(), font.getYb(),
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

    /**
     * delete next line in this.contentString.
     */
    public void deleteNextLine() {
        this.contentString = StringUtils.join(
                this.contentString.split(
                        NEXT_LINE_STRING
                )
        );
    }

    /**
     * <p>Setter for the field <code>contentString</code>.</p>
     *
     * @param contentString contentString
     */
    public void setContentString(String contentString) {
        this.contentString = contentString;
        if (this.isAllowMultiLine()) {
            this.deleteNextLine();
        }
        this.applyContentStringLengthLimit();
        if (this.isUseAllowCharSet()) {
            this.applyAllowCharSet();
        }
        if (this.isUseDisallowCharSet()) {
            this.applyDisallowCharSet();
        }
    }

    /**
     * apply disallowCharSet on {@link #contentString}
     * notice that usually you shall check {@link #useDisallowCharSet} before you invoke this.
     *
     * @see #disallowCharSet
     */
    private void applyDisallowCharSet() {
        if (!this.getDisallowCharSet().isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (char chr : this.getContentString().toCharArray()) {
                if (!this.getDisallowCharSet().contains(chr)) {
                    stringBuilder.append(chr);
                }
            }
            this.contentString = stringBuilder.toString();
        }
    }

    /**
     * apply allowCharSet on {@link #contentString}
     * notice that usually you shall check {@link #useAllowCharSet} before you invoke this.
     *
     * @see #allowCharSet
     */
    private void applyAllowCharSet() {
        if (!this.getAllowCharSet().isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (char chr : this.getContentString().toCharArray()) {
                if (this.getAllowCharSet().contains(chr)) {
                    stringBuilder.append(chr);
                }
            }
            this.contentString = stringBuilder.toString();
        }
    }

    /**
     * cut {@link #contentString} 's longer than {@link #contentStringLengthLimit}'s part.
     *
     * @see #contentStringLengthLimit
     */
    public void applyContentStringLengthLimit() {
        if (this.getContentStringLengthLimit() != -1 && this.getContentString().length() > this.getContentStringLengthLimit()) {
            this.contentString = this.getContentString().substring(0, this.getContentStringLengthLimit());
        }
    }

    /**
     * <p>Getter for the field <code>textColor</code>.</p>
     *
     * @return return
     */
    public Vector4fc getTextColor() {
        return new Vector4f(textColor);
    }

    /**
     * <p>Setter for the field <code>textColor</code>.</p>
     *
     * @param textColor textColor
     */
    public void setTextColor(Vector4fc textColor) {
        this.textColor.set(textColor);
    }

    /**
     * <p>Setter for the field <code>textColor</code>.</p>
     *
     * @param x textColor.x
     * @param y textColor.x
     * @param z textColor.x
     * @param w textColor.x
     */
    public void setTextColor(float x, float y, float z, float w) {
        this.textColor.set(x, y, z, w);
    }

    /**
     * <p>Getter for the field <code>textSelectColor</code>.</p>
     *
     * @return return
     */
    public Vector4fc getTextSelectColor() {
        return new Vector4f(textSelectColor);
    }

    /**
     * <p>Setter for the field <code>textSelectColor</code>.</p>
     *
     * @param textSelectColor textSelectColor
     */
    public void setTextSelectColor(Vector4fc textSelectColor) {
        this.textSelectColor.set(textSelectColor);
    }

    /**
     * <p>Setter for the field <code>textSelectColor</code>.</p>
     *
     * @param x textSelectColor.x
     * @param y textSelectColor.y
     * @param z textSelectColor.z
     * @param w textSelectColor.w
     */
    public void setTextSelectColor(float x, float y, float z, float w) {
        this.textSelectColor.set(x, y, z, w);
    }

    /**
     * <p>Getter for the field <code>insertColor</code>.</p>
     *
     * @return return
     */
    public Vector4fc getCursorColor() {
        return new Vector4f(cursorColor);
    }

    /**
     * <p>Setter for the field <code>insertColor</code>.</p>
     *
     * @param cursorColor insertColor
     */
    public void setCursorColor(Vector4fc cursorColor) {
        this.cursorColor.set(cursorColor);
    }

    /**
     * <p>Setter for the field <code>cursorColor</code>.</p>
     *
     * @param x cursorColor.x
     * @param y cursorColor.y
     * @param z cursorColor.z
     * @param w cursorColor.w
     */
    public void setCursorColor(float x, float y, float z, float w) {
        this.cursorColor.set(x, y, z, w);
    }

    /**
     * <p>isAllowMultiLine.</p>
     *
     * @return a boolean.
     */
    public boolean isAllowMultiLine() {
        return allowMultiLine.get();
    }

    /**
     * <p>Setter for the field <code>allowMultiLine</code>.</p>
     *
     * @param allowMultiLine a boolean.
     */
    public void setAllowMultiLine(boolean allowMultiLine) {
        this.allowMultiLine.set(allowMultiLine);
    }

    /**
     * <p>isUseAllowCharSet.</p>
     *
     * @return a boolean.
     */
    public boolean isUseAllowCharSet() {
        return this.useAllowCharSet.get();
    }

    /**
     * <p>Setter for the field <code>useAllowCharSet</code>.</p>
     *
     * @param useAllowCharSet a boolean.
     */
    public void setUseAllowCharSet(boolean useAllowCharSet) {
        this.useAllowCharSet.set(useAllowCharSet);
    }

    /**
     * <p>isUseDisallowCharSet.</p>
     *
     * @return a boolean.
     */
    public boolean isUseDisallowCharSet() {
        return this.useDisallowCharSet.get();
    }

    /**
     * <p>Setter for the field <code>useDisallowCharSet</code>.</p>
     *
     * @param useDisallowCharSet a boolean.
     */
    public void setUseDisallowCharSet(boolean useDisallowCharSet) {
        this.useDisallowCharSet.set(useDisallowCharSet);
    }
}

