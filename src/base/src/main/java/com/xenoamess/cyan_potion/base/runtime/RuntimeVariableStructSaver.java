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
//package com.xenoamess.cyan_potion.base.runtime;
//
//
//import com.xenoamess.cyan_potion.base.GameManager;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.lang.reflect.InvocationTargetException;
//
///**
// * OK,this class got deleted due to design change. lol.
// */
//public abstract class RuntimeVariableStructSaver<T extends RuntimeVariableStruct> {
//    private static final Logger LOGGER =
//            LoggerFactory.getLogger(GameManager.class);
//
//
//    private final Class<T> runtimeVariableStructClass;
//
//    public RuntimeVariableStructSaver(Class<T> runtimeVariableStructClass) {
//        this.runtimeVariableStructClass = runtimeVariableStructClass;
//    }
//
//    public abstract ObjectOutputStream getCurrentOutputStream();
//
//    public abstract ObjectInputStream getCurrentInputStream();
//
//    /**
//     * prepare to start a save process.
//     */
//    public abstract void saveStart();
//
//    /**
//     * prepare to end a save process.
//     */
//    public abstract void saveEnd();
//
//    /**
//     * prepare to start a load process.
//     */
//    public abstract void loadStart();
//
//    /**
//     * prepare to end a load process.
//     */
//    public abstract void loadEnd();
//
//    public void save(T runtimeVariableStruct) {
//        this.saveStart();
//        try {
//            this.getCurrentOutputStream().writeUTF(runtimeVariableStruct.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        this.saveEnd();
//    }
//
//    public T load() {
//        T result = null;
//        this.loadStart();
//        String string = null;
//        try {
//            string = this.getCurrentInputStream().readUTF();
//            result = runtimeVariableStructClass.getConstructor(String.class).newInstance(string);
//        } catch (IOException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
//            LOGGER.error("cannot load RuntimeVariable T from String, class : {} , String : {}", this.runtimeVariableStructClass, string);
//        }
//        this.loadEnd();
//        return result;
//    }
//
//}
