//
// Created by neoend on 2018-10-22.
//
#include <com_neoend_gms_util_SystemCommandJNI.h>
#include <stdio.h>
#include <stdlib.h>

JNIEXPORT jstring JNICALL Java_com_neoend_gms_util_SystemCommandJNI_getprop(JNIEnv *env, jobject obj) {
    system("/system/bin/getprop > /sdcard/getprop.txt");
    return env->NewStringUTF("Message from JNICPP*************************************************************: getprop");
}