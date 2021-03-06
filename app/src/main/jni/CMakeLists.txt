# This is an attempt to port the .mk files over to CMake.
# Looks like CMake is the way to go (for now) for all native android
# projects.
#
# Please don't try to use this file as a reference anywhere. I barely
# even know what cmake is, let alone how to write it. The way I write it,
# it's even more rigid than makefiles, which kinda defeats the whole
# purpose of the damn thing.

# Don't try to use this for anything but the android build.
# You've been warned.

# Android Studio seems to use this version
cmake_minimum_required(VERSION 3.4.1)

project(OPENXCOM CXX)

set(SDL_PATH SDL)
set(SDL2_IMAGE_PATH SDL2_image)
set(SDL2_MIXER_PATH SDL2_mixer)
set(SDL_GFX_PATH SDL_gfx)
set(YAML_CPP_PATH yaml-cpp)
set(LIBMAD_PATH libmad-0.15.1b)

set(LIBRARIES_BUILD_TYPE STATIC)

# Build prerequisites first (yeah, by including whole files)
include(deps_cmake/sdl.cmake)
include(deps_cmake/sdl2_image.cmake)
include(deps_cmake/libmad.cmake)
include(deps_cmake/sdl2_mixer.cmake)
include(deps_cmake/sdl_gfx.cmake)
include(deps_cmake/yaml-cpp.cmake)

#file(GLOB OPENXCOM_FILES_BASE
#    OpenXcom/src/*.cpp)
set(OPENXCOM_FILES_BASE
    OpenXcom/src/main.cpp
    OpenXcom/src/lodepng.cpp)

file(GLOB OPENXCOM_FILES_BASESCAPE
    OpenXcom/src/Basescape/*.cpp)

file(GLOB OPENXCOM_FILES_BATTLESCAPE
    OpenXcom/src/Battlescape/*.cpp)

file(GLOB OPENXCOM_FILES_ENGINE
    OpenXcom/src/Engine/*.cpp
    OpenXcom/src/Engine/Adlib/*.cpp
    OpenXcom/src/Engine/Scalers/*.cpp
    )

file(GLOB OPENXCOM_FILES_GEOSCAPE
    OpenXcom/src/Geoscape/*.cpp)

file(GLOB OPENXCOM_FILES_INTERFACE
    OpenXcom/src/Interface/*.cpp)

file(GLOB OPENXCOM_FILES_MENU
    OpenXcom/src/Menu/*.cpp)

file(GLOB OPENXCOM_FILES_MOD
    OpenXcom/src/Mod/*.cpp)

file(GLOB OPENXCOM_FILES_SAVEGAME
    OpenXcom/src/Savegame/*.cpp)

file(GLOB OPENXCOM_FILES_UFOPAEDIA
    OpenXcom/src/Ufopaedia/*.cpp)

set(OPENXCOM_SOURCES
    ${OPENXCOM_FILES_BASE}
    ${OPENXCOM_FILES_BASESCAPE}
    ${OPENXCOM_FILES_BATTLESCAPE}
    ${OPENXCOM_FILES_ENGINE}
    ${OPENXCOM_FILES_GEOSCAPE}
    ${OPENXCOM_FILES_INTERFACE}
    ${OPENXCOM_FILES_MENU}
    ${OPENXCOM_FILES_MOD}
    ${OPENXCOM_FILES_SAVEGAME}
    ${OPENXCOM_FILES_UFOPAEDIA}
    src/SDL_android_main.c
    )

add_library(openxcom
            SHARED
            ${OPENXCOM_SOURCES}
            )

find_library(EGL_LIB EGL)
find_library(LOG_LIB log)
find_library(GLESv2_LIB GLESv2)
find_library(ZLIB z)
find_library(DLLIB dl)

target_link_libraries(openxcom
    SDL2
    SDL2_image
    SDL2_mixer
    SDL_gfx
    YAML_CPP
    ${EGL_LIB}
    ${LOG_LIB}
    ${GLESv2_LIB}
    ${ZLIB}
    ${DLLIB}
    )

set_target_properties(openxcom
    PROPERTIES
    COMPILE_FLAGS
    "-D__NO_OPENGL\
    -D__MOBILE__\
    -DGIT_BUILD"
    )
#if (CMAKE_BUILD_TYPE EQUAL "Release")
#    set(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} -flto")
#    set(CMAKE_MODULE_LINKER_FLAGS "${CMAKE_MODULE_LINKER_FLAGS} -flto")
#    set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} -flto")
#    set(CMAKE_STATIC_LINKER_FLAGS "${CMAKE_STATIC_LINKER_FLAGS} -flto")
#endif()

# Taken from OpenXcom cmake
if ( "${OPENXCOM_VERSION_STRING}" STREQUAL "" )
  find_package ( Git )
  if ( GIT_FOUND )
    message("git found: ${GIT_EXECUTABLE}")
    execute_process ( COMMAND ${GIT_EXECUTABLE} describe --dirty
      WORKING_DIRECTORY ${CMAKE_SOURCE_DIR}
      OUTPUT_VARIABLE git_describe_out
      ERROR_VARIABLE git_describe_error
      RESULT_VARIABLE git_describe_result
      )
    string ( REGEX MATCH "([a-z|0-9|.]*)-([0-9]*)-g([a-z|0-9]*)([-|a-z]*)" git_commit "${git_describe_out}" )
    set ( git_tag ${CMAKE_MATCH_1} )
    set ( git_nb_commit ${CMAKE_MATCH_2} )
    set ( git_commit ${CMAKE_MATCH_3} )
    set ( git_dirty ${CMAKE_MATCH_4} )
    set ( OPENXCOM_VERSION_STRING ".${git_commit}${git_dirty}" )
  else()
# Well, just set the version string to a somewhat sensible default
    set ( OPENXCOM_VERSION_STRING ".nogit-dev" )
  endif()
endif()

add_definitions( -DGIT_BUILD=1 )

configure_file("OpenXcom/src/git_version.h.in" "${CMAKE_CURRENT_BINARY_DIR}/git_version.h" )
include_directories ( "${CMAKE_CURRENT_BINARY_DIR}" )