package com.godx.cloud.utils;

public class EnvUtil {

    private static String os = System.getProperty("os.name").toLowerCase();

    public static boolean isLinux(){
        return os.indexOf("linux")>=0;
    }

    public static boolean isWindows(){
        return os.indexOf("windows")>=0;
    }

    public static boolean isMac(){
        return os.indexOf("mac")>=0;
    }
}
