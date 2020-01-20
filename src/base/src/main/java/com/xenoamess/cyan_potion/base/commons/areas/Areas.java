package com.xenoamess.cyan_potion.base.commons.areas;

/**
 * Utility class for Area.
 *
 * @author XenoAmess
 */
public class Areas {
    private Areas() {
    }

    /**
     * generate a SimpleImmutableArea from center position.
     *
     * @param centerPosX a float.
     * @param centerPosY a float.
     * @param width      a float.
     * @param height     a float.
     * @return return
     */
    public static SimpleImmutableArea generateImmutableAreaFromArea(
            float centerPosX, float centerPosY, float width, float height) {
        return new SimpleImmutableArea(centerPosX - height / 2F, centerPosY - height / 2F, width, height);
    }

    /**
     * generate a SimpleImmutableArea from area.
     *
     * @param abstractArea area
     * @return return
     */
    public static SimpleImmutableArea generateImmutableAreaFromArea(
            AbstractArea abstractArea) {
        return new SimpleImmutableArea(abstractArea);
    }

    /**
     * generate a SimpleImmutableArea from left top position.
     *
     * @param leftTopPosX a float.
     * @param leftTopPosY a float.
     * @param width       a float.
     * @param height      a float.
     * @return return
     */
    public static SimpleImmutableArea generateImmutableAreaFromLeftTop(
            float leftTopPosX, float leftTopPosY, float width, float height) {
        return new SimpleImmutableArea(leftTopPosX, leftTopPosY, width, height);
    }

    /**
     * generate a SimpleMutableArea from center position.
     *
     * @param centerPosX a float.
     * @param centerPosY a float.
     * @param width      a float.
     * @param height     a float.
     * @return return
     */
    public static SimpleMutableArea generateMutableAreaFromArea(
            float centerPosX, float centerPosY, float width, float height) {
        return new SimpleMutableArea(centerPosX - height / 2F, centerPosY - width / 2F, width, height);
    }

    /**
     * generate a SimpleMutableArea from area.
     *
     * @param abstractArea area
     * @return return
     */
    public static SimpleMutableArea generateMutableAreaFromArea(
            AbstractArea abstractArea) {
        return new SimpleMutableArea(abstractArea);
    }

    /**
     * generate a SimpleMutableArea from left top position.
     *
     * @param leftTopPosX a float.
     * @param leftTopPosY a float.
     * @param width       a float.
     * @param height      a float.
     * @return return
     */
    public static SimpleMutableArea generateMutableAreaFromLeftTop(
            float leftTopPosX, float leftTopPosY, float width, float height) {
        return new SimpleMutableArea(leftTopPosX, leftTopPosY, width, height);
    }
}
