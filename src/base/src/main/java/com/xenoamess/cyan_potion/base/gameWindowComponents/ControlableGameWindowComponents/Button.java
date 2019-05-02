package com.xenoamess.cyan_potion.base.gameWindowComponents.Co

import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.render.Texture;
import com.xenoamess.cyan_potion.base.visual.Font;
import org.joml.Vector4f;

ntrolableGameWindowComponents;

/**
 * @author XenoAmess
 */
public class Button extends AbstractControlableGameWindowComponent {
    private Texture buttonTexture;
    private String buttonText;

    public Button(GameWindow gameWindow, Texture buttonTexture) {
        this(gameWindow, buttonTexture, null);
//        this.buttonTexture = buttonTexture;
//        this.buttonText = buttonText;
    }

    public Button(GameWindow gameWindow, Texture buttonTexture,
                  String buttonText) {
        super(gameWindow);
        this.setButtonTexture(buttonTexture);
        this.setButtonText(buttonText);
    }

    @Override
    public void ifVisibleThenDraw() {
        if (getButtonTexture() != null) {
            this.getGameWindow().drawBindableRelativeLeftTop(this.getButtonTexture(), this.getLeftTopPosX(), this.getLeftTopPosY(), this.getWidth(), this.getHeight());
        }
        if (this.getButtonText() != null) {
            this.getGameWindow().drawTextFillArea(Font.getCurrentFont(),
                    this.getLeftTopPosX() + this.getWidth() / 2,
                    this.getLeftTopPosY() + this.getHeight() / 2,
                    this.getWidth() / 6 * 4, this.getHeight() / 6 * 4,
                    0, new Vector4f(1, 1, 0, 1), this.getButtonText());
        }
    }

    public Texture getButtonTexture() {
        return buttonTexture;
    }

    public void setButtonTexture(Texture buttonTexture) {
        this.buttonTexture = buttonTexture;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

//    @Override
//    public Event onMouseButtonLeftDown(MouseButtonEvent mouseButtonEvent) {
//        Event res = mouseButtonEvent;
//        super.onMouseButtonLeftDown(mouseButtonEvent);
//        if (this.onMouseButtonLeftDownCallback != null) {
//            res = this.onMouseButtonLeftDownCallback.invoke(mouseButtonEvent);
//        }
//        return res;
//    }
}
