package com.xenoamess.cyan_potion.coordinate.physic.shapeComparators;

import com.xenoamess.cyan_potion.coordinate.physic.shapes.AbstractShape;

/**
 * @author XenoAmess
 */
public interface ShapeRelationJudger<K extends AbstractShape,
        V extends AbstractShape> {
    int relation(K k, V v, boolean rough);
//    public abstract int ifCollide(K k, V v);
}
