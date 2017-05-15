# We don't use SDL's cmake file for some reason (probably because
# I don't really trust Android Studio's cmake to do anything sensible
# with it), so I just use a rough translation of what was in SDL2's Android.mk

# THIS FILE IS AS BAD AS IT GETS, IT ASSUMES IT'S INCLUDED
# IN THE TOP-LEVEL PROJECT AS IS, DON'T DO THAT KIDS

file(GLOB SDL_FILES_BASE
    ${SDL_PATH}/src/*.c)

file(GLOB SDL_FILES_ATOMIC
        ${SDL_PATH}/src/atomic/*.c)

file(GLOB SDL_FILES_AUDIO
        ${SDL_PATH}/src/audio/*.c
        ${SDL_PATH}/src/audio/dummy/*.c
        ${SDL_PATH}/src/audio/android/*.c)

file(GLOB SDL_FILES_CORE
        ${SDL_PATH}/src/core/android/*.c)

file(GLOB SDL_FILES_CPUINFO
        ${SDL_PATH}/src/cpuinfo/*.c)

file(GLOB SDL_FILES_DYNAPI
        ${SDL_PATH}/src/dynapi/*.c)

file(GLOB SDL_FILES_EVENTS
        ${SDL_PATH}/src/events/*.c)

file(GLOB SDL_FILES_FILE
        ${SDL_PATH}/src/file/*.c)

file(GLOB SDL_FILES_FILESYSTEM
        ${SDL_PATH}/src/filesystem/android/*.c)

file(GLOB SDL_FILES_HAPTIC
        ${SDL_PATH}/src/haptic/*.c
        ${SDL_PATH}/src/haptic/dummy/*.c)

file(GLOB SDL_FILES_JOYSTICK
        ${SDL_PATH}/src/joystick/*.c
        ${SDL_PATH}/src/joystick/android/*.c)

file(GLOB SDL_FILES_LOADSO
        ${SDL_PATH}/src/loadso/dlopen/*.c)

file(GLOB SDL_FILES_POWER
        ${SDL_PATH}/src/power/*.c
        ${SDL_PATH}/src/power/android/*.c)

file(GLOB SDL_FILES_RENDER
        ${SDL_PATH}/src/render/*.c
        ${SDL_PATH}/src/render/opengles/*.c
        ${SDL_PATH}/src/render/opengles2/*.c
        ${SDL_PATH}/src/render/software/*.c)

file(GLOB SDL_FILES_STDLIB
        ${SDL_PATH}/src/stdlib/*.c)

file(GLOB SDL_FILES_TEST
        ${SDL_PATH}/src/test/*.c)

file(GLOB SDL_FILES_THREAD
        ${SDL_PATH}/src/thread/*.c
        ${SDL_PATH}/src/thread/pthread/*.c)

file(GLOB SDL_FILES_TIMER
        ${SDL_PATH}/src/timer/*.c
        ${SDL_PATH}/src/timer/unix/*.c)

file(GLOB SDL_FILES_VIDEO
        ${SDL_PATH}/src/video/*.c
        ${SDL_PATH}/src/video/android/*.c)


set(SDL_SOURCES
    ${SDL_FILES_BASE}
    ${SDL_FILES_ATOMIC}
    ${SDL_FILES_AUDIO}
    ${SDL_FILES_CORE}
    ${SDL_FILES_CPUINFO}
    ${SDL_FILES_DYNAPI}
    ${SDL_FILES_EVENTS}
    ${SDL_FILES_FILE}
    ${SDL_FILES_FILESYSTEM}
    ${SDL_FILES_HAPTIC}
    ${SDL_FILES_JOYSTICK}
    ${SDL_FILES_LOADSO}
    ${SDL_FILES_POWER}
    ${SDL_FILES_RENDER}
    ${SDL_FILES_STDLIB}
    ${SDL_FILES_TEST}
    ${SDL_FILES_THREAD}
    ${SDL_FILES_TIMER}
    ${SDL_FILES_VIDEO}
    )

find_library(LOG_LIB log)
find_library(GLES1_LIB GLESv1_CM)
find_library(GLES2_LIB GLESv2)
find_library(ANDROID_LIB android)
find_library(DL_LIB dl)

add_library(SDL2
            ${LIBRARIES_BUILD_TYPE}
            ${SDL_SOURCES})

target_link_libraries(SDL2
        ${LOG_LIB}
        ${GLES1_LIB}
        ${GLES2_LIB}
        ${ANDROID_LIB}
        ${DL_LIB}
        )

target_include_directories(SDL2 PUBLIC
        ${SDL_PATH}/include)

set_target_properties(SDL2 PROPERTIES COMPILE_FLAGS "-DGL_GLEXT_PROTOTYPES")