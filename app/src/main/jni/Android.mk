LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := SystemCommandJNI
LOCAL_SRC_FILES := SystemCommand.cpp

include $(BUILD_SHARED_LIBRARY)
