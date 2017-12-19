package com.neoend.gms.check.version;

import android.graphics.drawable.Drawable;

/**
 * Created by jeonghun.ye on 2016-06-16.
 */
public class AppInfo {
    public String name;
    public String versionCode;
    public String pkg;
    public Drawable icon;
    public String uid;

    public AppInfo(String name, String version, String pkg) {
        this.name = name;
        this.versionCode = version;
        this.pkg = pkg;
    }

    public AppInfo(String name, String version, String pkg, Drawable icon, int uid) {
        this.name = name;
        this.versionCode = version;
        this.pkg = pkg;
        this.icon = icon;
        this.uid = String.valueOf(uid);
    }
}
