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

package com.xenoamess.cyan_potion.base.exceptions;

import com.xenoamess.cyan_potion.base.annotations.AsFinalField;

import java.lang.reflect.Field;

/**
 * @author XenoAmess
 */
public class AsFinalFieldReSetException extends RuntimeException {
    public AsFinalFieldReSetException() {
        super();
    }

    public AsFinalFieldReSetException(Field field) {
        super(field.toGenericString());
    }


    /**
     * Check if object.fieldName is null.
     * If it is null, then set it to a new value.
     * Otherwise throw an AsFinalFieldReSetException.
     * <p>
     * if object is a Class, then will look into the class's static fields instead,
     * but not the fields in the class object.
     * <p>
     * If this field is not an AsFinalField, then do no check and set it to newValue directly.
     *
     * @param object    the object that hold an AsFinalField field.
     * @param fieldName fieldName that represent an AsFinalField field in object.
     * @param newValue  the new value of the AsFinalField field.
     * @see com.xenoamess.cyan_potion.base.annotations.AsFinalField
     * @see AsFinalFieldReSetException
     */
    public static void asFinalFieldSet(Object object, String fieldName, Object newValue) {
        asFinalFieldSet(object, fieldName, null, newValue);
    }


    /**
     * Check if object.fieldName is emptyValue.
     * If it is emptyValue, then set it to a new value.
     * Otherwise throw an AsFinalFieldReSetException.
     * <p>
     * if object is a Class, then will look into the class's static fields instead,
     * but not the fields in the class object.
     * <p>
     * If this field is not an AsFinalField, then do no check and set it to newValue directly.
     *
     * @param object     the object that hold an AsFinalField field.
     * @param fieldName  fieldName that represent an AsFinalField field in object.
     * @param emptyValue the value that thought be empty by that type.
     * @param newValue   the new value of the AsFinalField field.
     * @see com.xenoamess.cyan_potion.base.annotations.AsFinalField
     * @see AsFinalFieldReSetException
     */
    public static void asFinalFieldSet(Object object, String fieldName, Object emptyValue, Object newValue) {
        try {
            Field field;
            if (object instanceof Class) {
                field = ((Class) object).getDeclaredField(fieldName);
            } else {
                field = object.getClass().getDeclaredField(fieldName);
            }

            field.setAccessible(true);
            Object fieldValue = field.get(object);

            if (field.getDeclaredAnnotation(AsFinalField.class) == null || fieldValue == emptyValue) {
                if (object instanceof Class) {
                    field.set(null, newValue);
                } else {
                    field.set(object, newValue);
                }
                return;
            } else {
                throw new AsFinalFieldReSetException(field);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
