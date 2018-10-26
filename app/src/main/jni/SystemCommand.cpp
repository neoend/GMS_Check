//
// Created by neoend on 2018-10-22.
//
#include <com_neoend_gms_util_SystemCommandJNI.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

JNIEXPORT jstring JNICALL Java_com_neoend_gms_util_SystemCommandJNI_getprop(JNIEnv *env, jobject obj) {
    system("/system/bin/getprop > /sdcard/getprop.txt");
    return env->NewStringUTF("Message from JNICPP*************************************************************: getprop");
}

JNIEXPORT jstring JNICALL Java_com_neoend_gms_util_SystemCommandJNI_execute(JNIEnv *env, jobject thiz, jstring cmdStr) {
    // Step 1: Convert the JNI String (jstring) into C-String (char*)
    const char *cmd = env->GetStringUTFChars(cmdStr, NULL);
    char buf[100];
    char *str = NULL;
    char *temp = NULL;
    unsigned int size = 1;  // start with size of 1 to make room for null terminator
    unsigned int strlength;

    FILE *pfile;
    if (NULL == (pfile = popen(cmd, "r"))) {
        perror("popen error");
        exit(EXIT_FAILURE);
    }

    while (fgets(buf, sizeof(buf), pfile) != NULL) {
        strlength = strlen(buf);
        temp = (char*)realloc(str, size + strlength);  // allocate room for the buf that gets appended
        if (temp == NULL) {
            // allocation error
            printf("allocation error");
        } else {
            str = temp;
        }
        strcpy(str + size - 1, buf);     // append buffer to str
        size += strlength;
    }
    pclose(pfile);

    // Step 3: Convert the C-string (char*) into JNI String (jstring) and return
    return env->NewStringUTF(str);
}
