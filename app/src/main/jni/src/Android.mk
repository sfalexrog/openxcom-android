LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_CPP_FEATURES += exceptions

LOCAL_MODULE := main

SDL_PATH := ../SDL

SDL_GFX_PATH := ../SDL_gfx

SDL_IMAGE_PATH := ../SDL2_image

SDL_MIXER_PATH := ../SDL2_mixer

YAMLCPP_PATH := ../yaml-cpp

BOOST_PATH := ../boost

OPENXCOM_PATH := ../OpenXcom

LOCAL_C_INCLUDES := $(LOCAL_PATH)/$(SDL_PATH)/include \
	$(LOCAL_PATH)/$(SDL_GFX_PATH) \
	$(LOCAL_PATH)/$(SDL_IMAGE_PATH) \
	$(LOCAL_PATH)/$(SDL_MIXER_PATH)

LOCAL_ARM_MODE := arm

OPENXCOM_VERSION := $(shell git -C $(LOCAL_PATH)/$(OPENXCOM_PATH) rev-parse --short HEAD | sed 's/.*-/-/' | sed 's/.*/\\\"&\\\"/')

LOCAL_CFLAGS += -DOPENXCOM_VERSION_GIT="$(OPENXCOM_VERSION)" -D__MOBILE__

# Disable OpenGL renderer

LOCAL_CFLAGS += -D__NO_OPENGL

LOCAL_CXXFLAGS += -std=c++11

ifneq ($(TARGET_ARCH_ABI),armeabi-v7a)
    LOCAL_ARM_NEON := false
else
    LOCAL_ARM_NEON := true
endif

# Add your application source files here...
# deleted: $(SDL_PATH)/src/main/android/SDL_android_main.c
LOCAL_SRC_FILES := SDL_android_main.c \
	$(subst $(LOCAL_PATH)/,, \
	$(LOCAL_PATH)/$(OPENXCOM_PATH)/src/main.cpp \
	$(LOCAL_PATH)/$(OPENXCOM_PATH)/src/lodepng.cpp \
	$(wildcard $(LOCAL_PATH)/$(OPENXCOM_PATH)/src/Basescape/*.cpp) \
	$(wildcard $(LOCAL_PATH)/$(OPENXCOM_PATH)/src/Battlescape/*.cpp) \
	$(wildcard $(LOCAL_PATH)/$(OPENXCOM_PATH)/src/Engine/*.cpp) \
	$(wildcard $(LOCAL_PATH)/$(OPENXCOM_PATH)/src/Engine/Adlib/*.cpp) \
	$(wildcard $(LOCAL_PATH)/$(OPENXCOM_PATH)/src/Engine/Scalers/*.cpp) \
	$(wildcard $(LOCAL_PATH)/$(OPENXCOM_PATH)/src/Geoscape/*.cpp) \
	$(wildcard $(LOCAL_PATH)/$(OPENXCOM_PATH)/src/Interface/*.cpp) \
	$(wildcard $(LOCAL_PATH)/$(OPENXCOM_PATH)/src/Menu/*.cpp) \
	$(wildcard $(LOCAL_PATH)/$(OPENXCOM_PATH)/src/Resource/*.cpp) \
	$(wildcard $(LOCAL_PATH)/$(OPENXCOM_PATH)/src/Ruleset/*.cpp) \
	$(wildcard $(LOCAL_PATH)/$(OPENXCOM_PATH)/src/Savegame/*.cpp) \
	$(wildcard $(LOCAL_PATH)/$(OPENXCOM_PATH)/src/Ufopaedia/*.cpp))

LOCAL_STATIC_LIBRARIES := SDL2_static SDL2_image SDL2_mixer SDL_gfx

LOCAL_LDLIBS := -lGLESv1_CM -lGLESv2 -llog

LOCAL_EXPORT_LDLIBS := -lSDL2

# yaml-cpp
LOCAL_C_INCLUDES += $(LOCAL_PATH)/$(YAMLCPP_PATH)/include \
					$(LOCAL_PATH)/$(BOOST_PATH)
LOCAL_CPP_INCLUDES += $(LOCAL_PATH)/$(BOOST_PATH)

LOCAL_SRC_FILES += \
	$(subst $(LOCAL_PATH)/,, \
	$(wildcard $(LOCAL_PATH)/$(YAMLCPP_PATH)/src/*.cpp))

include $(BUILD_SHARED_LIBRARY)
