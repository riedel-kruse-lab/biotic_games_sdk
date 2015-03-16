LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := OpenCVLibrary-2.4.9
LOCAL_SRC_FILES := OpenCVLibrary-2.4.9.cpp

include $(BUILD_SHARED_LIBRARY)
