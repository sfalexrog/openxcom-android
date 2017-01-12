cmake_minimum_required(VERSION 3.4.0)

project(YAML_CPP CXX)

file(GLOB YAML_CPP_SRC
    yaml-cpp/src/*.cpp
    )

set(YAML_CPP_SOURCES
    ${YAML_CPP_SRC}
    )

add_library(YAML_CPP STATIC
    ${YAML_CPP_SOURCES}
    )

target_include_directories(YAML_CPP
    PUBLIC
    yaml-cpp/include
    )

target_include_directories(YAML_CPP
    PRIVATE
    yaml-cpp/src
    )