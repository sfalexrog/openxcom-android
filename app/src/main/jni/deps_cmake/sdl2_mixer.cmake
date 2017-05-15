set(SDL2_MIXER_BASE
    ${SDL2_MIXER_PATH}/dynamic_flac.c
    ${SDL2_MIXER_PATH}/dynamic_fluidsynth.c
    ${SDL2_MIXER_PATH}/dynamic_mod.c
    ${SDL2_MIXER_PATH}/dynamic_modplug.c
    ${SDL2_MIXER_PATH}/dynamic_mp3.c
    ${SDL2_MIXER_PATH}/dynamic_ogg.c
    ${SDL2_MIXER_PATH}/effect_position.c
    ${SDL2_MIXER_PATH}/effects_internal.c
    ${SDL2_MIXER_PATH}/effect_stereoreverse.c
    ${SDL2_MIXER_PATH}/fluidsynth.c
    ${SDL2_MIXER_PATH}/load_aiff.c
    ${SDL2_MIXER_PATH}/load_flac.c
    ${SDL2_MIXER_PATH}/load_mp3.c
    ${SDL2_MIXER_PATH}/load_ogg.c
    ${SDL2_MIXER_PATH}/load_voc.c
    ${SDL2_MIXER_PATH}/mixer.c
    ${SDL2_MIXER_PATH}/music.c
    ${SDL2_MIXER_PATH}/music_cmd.c
    ${SDL2_MIXER_PATH}/music_flac.c
    ${SDL2_MIXER_PATH}/music_mad.c
    ${SDL2_MIXER_PATH}/music_mod.c
    ${SDL2_MIXER_PATH}/music_modplug.c
    ${SDL2_MIXER_PATH}/music_ogg.c
    ${SDL2_MIXER_PATH}/wavestream.c
)

set(SDL2_MIXER_OGG_FILES
    ${SDL2_MIXER_PATH}/external/libvorbisidec-1.2.1/mdct.c
    ${SDL2_MIXER_PATH}/external/libvorbisidec-1.2.1/block.c
    ${SDL2_MIXER_PATH}/external/libvorbisidec-1.2.1/window.c
    ${SDL2_MIXER_PATH}/external/libvorbisidec-1.2.1/synthesis.c
    ${SDL2_MIXER_PATH}/external/libvorbisidec-1.2.1/info.c
    ${SDL2_MIXER_PATH}/external/libvorbisidec-1.2.1/floor1.c
    ${SDL2_MIXER_PATH}/external/libvorbisidec-1.2.1/floor0.c
    ${SDL2_MIXER_PATH}/external/libvorbisidec-1.2.1/vorbisfile.c
    ${SDL2_MIXER_PATH}/external/libvorbisidec-1.2.1/res012.c
    ${SDL2_MIXER_PATH}/external/libvorbisidec-1.2.1/mapping0.c
    ${SDL2_MIXER_PATH}/external/libvorbisidec-1.2.1/registry.c
    ${SDL2_MIXER_PATH}/external/libvorbisidec-1.2.1/codebook.c
    ${SDL2_MIXER_PATH}/external/libvorbisidec-1.2.1/sharedbook.c
    ${SDL2_MIXER_PATH}/external/libogg-1.3.1/src/framing.c
    ${SDL2_MIXER_PATH}/external/libogg-1.3.1/src/bitwise.c
    )

set(MIXER_FLAC_PATH external/flac-1.2.1)

set(SDL2_MIXER_LIBFLAC_FILES
    ${SDL2_MIXER_PATH}/${MIXER_FLAC_PATH}/src/libFLAC/bitmath.c
    ${SDL2_MIXER_PATH}/${MIXER_FLAC_PATH}/src/libFLAC/bitreader.c
    ${SDL2_MIXER_PATH}/${MIXER_FLAC_PATH}/src/libFLAC/bitwriter.c
    ${SDL2_MIXER_PATH}/${MIXER_FLAC_PATH}/src/libFLAC/cpu.c
    ${SDL2_MIXER_PATH}/${MIXER_FLAC_PATH}/src/libFLAC/crc.c
    ${SDL2_MIXER_PATH}/${MIXER_FLAC_PATH}/src/libFLAC/fixed.c
    ${SDL2_MIXER_PATH}/${MIXER_FLAC_PATH}/src/libFLAC/float.c
    ${SDL2_MIXER_PATH}/${MIXER_FLAC_PATH}/src/libFLAC/format.c
    ${SDL2_MIXER_PATH}/${MIXER_FLAC_PATH}/src/libFLAC/lpc.c
    ${SDL2_MIXER_PATH}/${MIXER_FLAC_PATH}/src/libFLAC/md5.c
    ${SDL2_MIXER_PATH}/${MIXER_FLAC_PATH}/src/libFLAC/memory.c
    ${SDL2_MIXER_PATH}/${MIXER_FLAC_PATH}/src/libFLAC/metadata_iterators.c
    ${SDL2_MIXER_PATH}/${MIXER_FLAC_PATH}/src/libFLAC/metadata_object.c
#    ${SDL2_MIXER_PATH}/${MIXER_FLAC_PATH}/src/libFLAC/ogg_decoder_aspect.c
#    ${SDL2_MIXER_PATH}/${MIXER_FLAC_PATH}/src/libFLAC/ogg_encoder_aspect.c
#    ${SDL2_MIXER_PATH}/${MIXER_FLAC_PATH}/src/libFLAC/ogg_helper.c
#    ${SDL2_MIXER_PATH}/${MIXER_FLAC_PATH}/src/libFLAC/ogg_mapping.c
    ${SDL2_MIXER_PATH}/${MIXER_FLAC_PATH}/src/libFLAC/stream_decoder.c
    ${SDL2_MIXER_PATH}/${MIXER_FLAC_PATH}/src/libFLAC/stream_encoder.c
    ${SDL2_MIXER_PATH}/${MIXER_FLAC_PATH}/src/libFLAC/stream_encoder_framing.c
    ${SDL2_MIXER_PATH}/${MIXER_FLAC_PATH}/src/libFLAC/window.c
    )

add_library(LIBFLAC STATIC
    ${SDL2_MIXER_LIBFLAC_FILES})

target_include_directories(LIBFLAC
    PUBLIC
    ${SDL2_MIXER_PATH}/${MIXER_FLAC_PATH}/include)


target_include_directories(LIBFLAC
    PRIVATE
    ${SDL2_MIXER_PATH}/${MIXER_FLAC_PATH}/src/libFLAC/include
    src/libflac_cfg)

target_include_directories(LIBFLAC
    PRIVATE SYSTEM
    src/libflac_cfg)

set_target_properties(LIBFLAC
    PROPERTIES
    COMPILE_FLAGS
    "-DHAVE_CONFIG_H")

set(SDL2_MIXER_SOURCES
    ${SDL2_MIXER_BASE}
    ${SDL2_MIXER_OGG_FILES}
    )

set(SDL2_MIXER_CFLAGS
    "-DOGG_MUSIC -DOGG_USE_TREMOR -DOGG_HEADER=\"<ivorbisfile.h>\"\
    -DFLAC_MUSIC\
    -DWAV_MUSIC")

set(SDL2_MIXER_LIBS
    SDL2)

if(TARGET LIBMAD)
    set(SDL2_MIXER_CFLAGS
        "${SDL2_MIXER_CFLAGS} -DMP3_MAD_MUSIC")
endif(TARGET LIBMAD)

add_library(SDL2_mixer
            ${LIBRARIES_BUILD_TYPE}
            ${SDL2_MIXER_SOURCES})

target_link_libraries(SDL2_mixer
    SDL2
    LIBFLAC)

if(TARGET LIBMAD)
    target_link_libraries(SDL2_mixer
        LIBMAD)
endif(TARGET LIBMAD)

target_include_directories(SDL2_mixer PUBLIC
        SDL2_mixer)

target_include_directories(SDL2_mixer PRIVATE
        ${SDL2_MIXER_PATH}/external/libvorbisidec-1.2.1
        ${SDL2_MIXER_PATH}/external/libogg-1.3.1/include)

set_target_properties(SDL2_mixer
    PROPERTIES
    COMPILE_FLAGS
    ${SDL2_MIXER_CFLAGS})
