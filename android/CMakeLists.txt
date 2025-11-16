# Sets the minimum required version of CMake
cmake_minimum_required(VERSION 3.18.1)
project("EdgeDetector")

# Set C++ standard
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Find OpenCV
# CMake uses the path provided by the -DOPENCV_DIR argument from app/build.gradle
find_package(OpenCV REQUIRED)

# Include OpenCV headers
include_directories(${OPENCV_INCLUDE_DIRS})

# Add native library
# Name: native-lib (must match System.loadLibrary call)
add_library(
    native-lib
    SHARED
    src/main/cpp/native-lib.cpp
    src/main/cpp/edge_detection.cpp
)

# Link libraries
target_link_libraries(
    native-lib
    ${OPENCV_LIBRARIES}
    # Link the essential Android logging library
    log
)