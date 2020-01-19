package com.xenoamess.cyan_potion.base.io.input.gamepad;

public class SLF4JPlaceTest {

    public static void main(String[] args) {
        ClassLoader loader = SLF4JPlaceTest.class.getClassLoader();
//        org.slf4j.spi.LocationAwareLogger
        System.out.println(loader.getResource("org/slf4j/spi/LocationAwareLogger.class"));
//        org.apache.commons.vfs2.VFS.getManager
//        WebdavFileProvider
    }
}
