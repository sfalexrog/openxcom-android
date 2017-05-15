set(LIBMAD_FILES
        ${LIBMAD_PATH}/bit.c
        ${LIBMAD_PATH}/decoder.c
        ${LIBMAD_PATH}/fixed.c
        ${LIBMAD_PATH}/frame.c
        ${LIBMAD_PATH}/huffman.c
        ${LIBMAD_PATH}/layer3.c
        ${LIBMAD_PATH}/layer12.c
        ${LIBMAD_PATH}/stream.c
        ${LIBMAD_PATH}/synth.c
        ${LIBMAD_PATH}/timer.c
        ${LIBMAD_PATH}/version.c
        )

add_library(LIBMAD ${LIBRARIES_BUILD_TYPE}
        ${LIBMAD_FILES})

target_include_directories(LIBMAD BEFORE PRIVATE
        ${LIBMAD_PATH})

target_include_directories(LIBMAD INTERFACE
        ${LIBMAD_PATH})

set_target_properties(LIBMAD
        PROPERTIES
        COMPILE_FLAGS
        "-DHAVE_CONFIG_H -DFPM_DEFAULT -DPIC")