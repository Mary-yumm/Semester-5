# Specify the project name
project(VOLLEY)

# Minimum CMake version required
cmake_minimum_required(VERSION 3.0)

# Set compilation flags for debug and release builds
set(CMAKE_CXX_FLAGS_DEBUG_INIT "-Wextra -Wall -pedantic --std=c++17 -g2")
set(CMAKE_CXX_FLAGS_RELEASE_INIT "-Wextra -Wall -pedantic --std=c++17 -O3")

# Include directories for header files
include_directories(include /home/mary/SFML/include)

# Source files for the project
set(SRC_FILES 
    src/main.cpp
    src/volley.cpp
    src/entity.cpp
    src/utils.cpp
    src/ball.cpp
    src/character.cpp
    src/score.cpp
    src/sound.cpp
    src/menu.cpp
    src/pauseMenu.cpp
)

# Specify the runtime output directory for the executable
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin)

# Link the SFML libraries
set(SFML_LIB_DIR /home/mary/SFML/lib)
link_directories(${SFML_LIB_DIR})

# Add the required libraries (adjusted for SFML dependencies)
link_libraries(sfml-graphics sfml-window sfml-system sfml-audio pthread X11)

# Add the executable target
add_executable(volley ${SRC_FILES})

# Copy resources directory to the output folder after the build
add_custom_command(TARGET volley POST_BUILD
    COMMAND ${CMAKE_COMMAND} -E copy_directory
        "${PROJECT_SOURCE_DIR}/resources/"
        $<TARGET_FILE_DIR:volley>)

