///*
// * MIT License
// *
// * Copyright (c) 2020 XenoAmess
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in all
// * copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// * SOFTWARE.
// */
//
//package com.xenoamess.cyan_potion.base;
//
//import com.xenoamess.commons.as_final_field.AsFinalField;
//import com.xenoamess.commons.as_final_field.AsFinalFieldReSetException;
//import lombok.Setter;
//
//public class LombokNewFeatureTest {
//    @Setter
//    @AsFinalField
//    private int value;
//    private boolean _lombok_generated_ifSet_value = false;
//
//    public void setValue(int newValue) {
//        if (!_lombok_generated_ifSet_value) {
//            value = newValue;
//            _lombok_generated_ifSet_value = true;
//        } else {
//            throw new AsFinalFieldReSetException(
//                    LombokNewFeatureTest.class, "value"
//            );
//        }
//    }
//}
