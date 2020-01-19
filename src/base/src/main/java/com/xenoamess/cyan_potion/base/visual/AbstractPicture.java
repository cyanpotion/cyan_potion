package com.xenoamess.cyan_potion.base.visual;

import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.commons.areas.AbstractMutableArea;
import com.xenoamess.cyan_potion.base.game_window_components.AbstractScene;
import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.base.render.Model;
import org.joml.Vector4f;

/**
 * @author XenoAmess
 */
public abstract class AbstractPicture implements AbstractMutableArea, Bindable {
    private float centerPosX;
    private float centerPosY;
    private float width;
    private float height;
    private Vector4f colorScale = new Vector4f(1, 1, 1, 1);
    private float rotateRadius = 0f;

    @Override
    public void bind(int sampler) {
        this.getCurrentBindable().bind(sampler);
    }

    /**
     * <p>draw.</p>
     *
     * @param gameWindow gameWindow
     */
    public void draw(GameWindow gameWindow) {
        if (this.getCurrentBindable() == null) {
            return;
        }
        gameWindow.drawBindableRelative(
                this.getCurrentBindable(),
                this.getCenterPosX(),
                this.getCenterPosY(),
                this.getWidth(),
                this.getHeight(),
                Model.COMMON_MODEL,
                this.getColorScale(),
                this.getRotateRadius()
        );
    }

    /**
     * <p>draw.</p>
     *
     * @param scene scene
     */
    public void draw(AbstractScene scene) {
        if (this.getCurrentBindable() == null) {
            return;
        }
        scene.drawBindableAbsolute(
                scene.getCamera(),
                scene.getScale(),
                this.getCurrentBindable(),
                this.getCenterPosX(),
                this.getCenterPosY(),
                this.getWidth(),
                this.getHeight(),
                Model.COMMON_MODEL,
                this.getColorScale(),
                this.getRotateRadius()
        );
    }

    /**
     * <p>rotate.</p>
     *
     * @param newRotateRadius a float.
     */
    public void rotate(float newRotateRadius) {
        this.setRotateRadius(this.getRotateRadius() + newRotateRadius);
    }

    /**
     * <p>rotateTo.</p>
     *
     * @param newRotateRadius a float.
     */
    public void rotateTo(float newRotateRadius) {
        this.setRotateRadius(newRotateRadius);
    }


    /**
     * get current bindable of this picture.
     *
     * @return return
     */
    public abstract Bindable getCurrentBindable();

    //--- getters and setters ---

    /**
     * {@inheritDoc}
     */
    @Override
    public float getCenterPosX() {
        return centerPosX;
    }

    /**
     * <p>Setter for the field <code>centerPosX</code>.</p>
     *
     * @param centerPosX a float.
     */
    @Override
    public void setCenterPosX(float centerPosX) {
        this.centerPosX = centerPosX;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getCenterPosY() {
        return centerPosY;
    }

    /**
     * <p>Setter for the field <code>centerPosY</code>.</p>
     *
     * @param centerPosY a float.
     */
    @Override
    public void setCenterPosY(float centerPosY) {
        this.centerPosY = centerPosY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getWidth() {
        return width;
    }

    /**
     * <p>Setter for the field <code>width</code>.</p>
     *
     * @param width a float.
     */
    @Override
    public void setWidth(float width) {
        this.width = width;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getHeight() {
        return height;
    }

    /**
     * <p>Setter for the field <code>height</code>.</p>
     *
     * @param height a float.
     */
    @Override
    public void setHeight(float height) {
        this.height = height;
    }

    /**
     * <p>Getter for the field <code>colorScale</code>.</p>
     *
     * @return return
     */
    public Vector4f getColorScale() {
        return colorScale;
    }

    /**
     * <p>Setter for the field <code>colorScale</code>.</p>
     *
     * @param colorScale colorScale
     */
    public void setColorScale(Vector4f colorScale) {
        this.colorScale = colorScale;
    }

    /**
     * <p>Getter for the field <code>rotateRadius</code>.</p>
     *
     * @return a float.
     */
    public float getRotateRadius() {
        return rotateRadius;
    }

    /**
     * <p>Setter for the field <code>rotateRadius</code>.</p>
     *
     * @param rotateRadius a float.
     */
    public void setRotateRadius(float rotateRadius) {
        this.rotateRadius = rotateRadius;
    }
}
