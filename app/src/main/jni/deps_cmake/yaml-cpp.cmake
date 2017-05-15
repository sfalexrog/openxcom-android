# THIS FILE IS FOR DIRECT INCLUSION! Do not use in any other cases.

# Add YAML_CPP target to the project
# You should set YAML_CPP_PATH to a path to yaml-cpp sources

file(GLOB YAML_CPP_SRC
    ${YAML_CPP_PATH}/src/*.cpp
    )

set(YAML_CPP_SOURCES
    ${YAML_CPP_SRC}
    )

add_library(YAML_CPP ${LIBRARIES_BUILD_TYPE}
    ${YAML_CPP_SOURCES}
    )

target_include_directories(YAML_CPP
    PUBLIC
    ${YAML_CPP_PATH}/include
    )

target_include_directories(YAML_CPP
    PRIVATE
    ${YAML_CPP_PATH}/src
    )