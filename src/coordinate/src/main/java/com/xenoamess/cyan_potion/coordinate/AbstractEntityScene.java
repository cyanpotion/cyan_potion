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

package com.xenoamess.cyan_potion.coordinate;

import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.game_window_components.AbstractScene;
import com.xenoamess.cyan_potion.coordinate.entity.AbstractDynamicEntity;
import com.xenoamess.cyan_potion.coordinate.entity.StaticEntity;
import com.xenoamess.cyan_potion.coordinate.physic.shapes.AbstractShape;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Abstract AbstractEntityScene class.</p>
 *
 * @author XenoAmess
 * @version 0.162.3
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public abstract class AbstractEntityScene extends AbstractScene {
    /**
     * Constant <code>BOX_SIZE=128</code>
     */
    public static final int BOX_SIZE = 128;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Getter
    private final List<StaticEntity> staticEntitySetList = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Getter
    private final List<AbstractDynamicEntity> dynamicEntityList = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Getter
    private final Map<ImmutablePair<Integer, Integer>, Set<AbstractShape>> boxToShapeMap = new ConcurrentHashMap<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Getter
    private final Map<AbstractShape, Set<AbstractShape>> shapeCollisionSet = new ConcurrentHashMap<>();

    /**
     * <p>Constructor for AbstractEntityScene.</p>
     *
     * @param gameWindow gameWindow
     */
    public AbstractEntityScene(GameWindow gameWindow) {
        super(gameWindow);
    }
}
