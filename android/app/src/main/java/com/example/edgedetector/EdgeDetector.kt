package com.example.edgedetector

class EdgeDetector {
    companion object {
        // Load the native library defined in CMakeLists.txt (name must match 'native-lib')
        init {
            System.loadLibrary("native-lib")
        }
    }

    // JNI function declarations (Kotlin calls the C++ functions here)
    external fun initNative()
    external fun processFrameNative(yData: ByteArray, width: Int, height: Int, format: Int): ByteArray?
    external fun releaseNative()
}