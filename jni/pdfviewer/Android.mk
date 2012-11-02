LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := pdfviewer

LOCAL_ARM_MODE := arm

LOCAL_CFLAGS := 

LOCAL_SRC_FILES := \
	pdfviewerjni.c \
	pdfbridge.c
	
LOCAL_C_INCLUDES := \
	$(LOCAL_PATH)/../mupdf/mupdf/fitz \
	$(LOCAL_PATH)/../mupdf/mupdf/pdf
	
LOCAL_STATIC_LIBRARIES := mupdf jpeg

LOCAL_LDLIBS := -llog -lz

include $(BUILD_SHARED_LIBRARY)