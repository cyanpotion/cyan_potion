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

package com.xenoamess.cyan_potion.coordinate;

import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.gameWindowComponents.AbstractScene;
import com.xenoamess.cyan_potion.coordinate.entity.AbstractDynamicEntity;
import com.xenoamess.cyan_potion.coordinate.entity.StaticEntity;
import com.xenoamess.cyan_potion.coordinate.physic.shapes.AbstractShape;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author XenoAmess
 */
public abstract class AbstractEntityScene extends AbstractScene {
    public static final int BOX_SIZE = 128;

    private final Set<StaticEntity> staticEntitySet = new HashSet<>();
    private final Set<AbstractDynamicEntity> dynamicEntitySet = new HashSet<>();
    private final Map<ImmutablePair<Integer, Integer>, Set<AbstractShape>> boxToShapeMap = new ConcurrentHashMap<>();
    private final Map<AbstractShape, Set<AbstractShape>> shapeCollisionSet = new ConcurrentHashMap<>();

    public AbstractEntityScene(GameWindow gameWindow) {
        super(gameWindow);
    }

    public Set<StaticEntity> getStaticEntitySet() {
        return staticEntitySet;
    }

    public Set<AbstractDynamicEntity> getDynamicEntitySet() {
        return dynamicEntitySet;
    }

    public Map<ImmutablePair<Integer, Integer>, Set<AbstractShape>> getBoxToShapeMap() {
        return boxToShapeMap;
    }

    public Map<AbstractShape, Set<AbstractShape>> getShapeCollisionSet() {
        return shapeCollisionSet;
    }

}
