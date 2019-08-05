package com.xenoamess.cyan_potion.base.visual;

import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.commons.areas.AbstractArea;
import com.xenoamess.cyan_potion.base.game_window_components.AbstractScene;
import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.base.render.Model;
import org.joml.Vector4f;

/**
 * @author XenoAmess
 */
public abstract class AbstractPicture implements AbstractArea {
    private float centerPosX;
    private float centerPosY;
    private float width;
    private float height;
    private Vector4f colorScale = new Vector4f(1, 1, 1, 1);
    private float rotateRadius = 0f;

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
     * <p>enlargeWidth.</p>
     *
     * @param newWidth a float.
     */
    public void enlargeWidth(float newWidth) {
        this.setWidth(this.getWidth() + newWidth);
    }

    /**
     * <p>enlargeHeight.</p>
     *
     * @param newHeight a float.
     */
    public void enlargeHeight(float newHeight) {
        this.setHeight(this.getHeight() + newHeight);
    }

    /**
     * <p>enlargeSize.</p>
     *
     * @param newWidth  a float.
     * @param newHeight a float.
     */
    public void enlargeSize(float newWidth, float newHeight) {
        this.enlargeWidth(newWidth);
        this.enlargeHeight(newHeight);
    }

    /**
     * <p>scaleWidth.</p>
     *
     * @param ratio a float.
     */
    public void scaleWidth(float ratio) {
        this.setWidth(this.getWidth() * ratio);
    }

    /**
     * <p>scaleHeight.</p>
     *
     * @param ratio a float.
     */
    public void scaleHeight(float ratio) {
        this.setHeight(this.getHeight() * ratio);
    }

    /**
     * <p>scaleSize.</p>
     *
     * @param ratio a float.
     */
    public void scaleSize(float ratio) {
        this.scaleWidth(ratio);
        this.scaleHeight(ratio);
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
     * <p>setSize.</p>
     *
     * @param width  a float.
     * @param height a float.
     */
    public void setSize(float width, float height) {
        this.setWidth(width);
        this.setHeight(height);
    }

    /**
     * <p>setCenterPos.</p>
     *
     * @param centerPosX a float.
     * @param centerPosY a float.
     */
    public void setCenterPos(float centerPosX, float centerPosY) {
        this.setCenterPosX(centerPosX);
        this.setCenterPosY(centerPosY);
    }

    /**
     * <p>setCenter.</p>
     *
     * @param abstractArea area
     */
    public void setCenter(AbstractArea abstractArea) {
        this.setCenterPosX(abstractArea.getCenterPosX());
        this.setCenterPosY(abstractArea.getCenterPosY());
    }

    /**
     * <p>setSize.</p>
     *
     * @param abstractArea area
     */
    public void setSize(AbstractArea abstractArea) {
        this.setWidth(abstractArea.getWidth());
        this.setHeight(abstractArea.getHeight());
    }

    /**
     * <p>cover.</p>
     *
     * @param abstractArea area
     */
    public void cover(AbstractArea abstractArea) {
        this.setCenter(abstractArea);
        this.setSize(abstractArea);
    }

    /**
     * <p>move.</p>
     *
     * @param centerMovementX a float.
     * @param centerMovementY a float.
     */
    public void move(float centerMovementX, float centerMovementY) {
        this.moveX(centerMovementX);
        this.moveY(centerMovementY);
    }

    /**
     * <p>moveX.</p>
     *
     * @param centerMovementX a float.
     */
    public void moveX(float centerMovementX) {
        this.setCenterPosX(this.getCenterPosX() + centerMovementX);
    }

    /**
     * <p>moveY.</p>
     *
     * @param centerMovementY a float.
     */
    public void moveY(float centerMovementY) {
        this.setCenterPosY(this.getCenterPosY() + centerMovementY);
    }

    /**
     * <p>moveTo.</p>
     *
     * @param newCenterPosX a float.
     * @param newCenterPosY a float.
     */
    public void moveTo(float newCenterPosX, float newCenterPosY) {
        this.setCenterPos(newCenterPosX, newCenterPosY);
    }

    /**
     * <p>moveToLeftTop.</p>
     *
     * @param newLeftTopPosX a float.
     * @param newLeftTopPosY a float.
     */
    public void moveToLeftTop(float newLeftTopPosX, float newLeftTopPosY) {
        this.setCenterPos(newLeftTopPosX + this.getWidth() / 2F, newLeftTopPosY + this.getHeight() / 2F);
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
