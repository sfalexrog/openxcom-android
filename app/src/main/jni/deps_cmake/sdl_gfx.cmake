
file(GLOB SDL_gfx_BASE
    ${SDL_GFX_PATH}/*.c
)

set(SDL_gfx_SOURCES
    ${SDL_gfx_BASE}
    )

add_library(SDL_gfx
            ${LIBRARIES_BUILD_TYPE}
            ${SDL_gfx_SOURCES})

target_link_libraries(SDL_gfx
    SDL2)

target_include_directories(SDL_gfx PUBLIC
        SDL_gfx)
