cmake_minimum_required(VERSION 3.4.0)

project(SDL_gfx C)

file(GLOB SDL_gfx_BASE
    SDL_gfx/*.c
)

set(SDL_gfx_SOURCES
    ${SDL_gfx_BASE}
    )

add_library(SDL_gfx
            STATIC
            ${SDL_gfx_SOURCES})

target_link_libraries(SDL_gfx
    SDL2)

target_include_directories(SDL_gfx PUBLIC
        SDL_gfx)
