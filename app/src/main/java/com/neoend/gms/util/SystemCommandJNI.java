package com.neoend.gms.util;

public class SystemCommandJNI {

    static {
        System.loadLibrary("SystemCommandJNI");
    }

    public native String getprop();

    private static SystemCommandJNI systemCommandJNI;

    public static SystemCommandJNI getInstance() {
        if (systemCommandJNI == null) {
            systemCommandJNI = new SystemCommandJNI();
        }
        return systemCommandJNI;
    }
}
