#include <jni.h>
#include <opencv2/opencv.hpp>
#include <android/log.h>
#include "edge_detection.h"

#define TAG "NativeLib"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

// Global instance of the C++ EdgeDetector class
static EdgeDetector* g_edgeDetector = nullptr;

extern "C" {

JNIEXPORT void JNICALL
Java_com_example_edgedetector_EdgeDetector_initNative(JNIEnv* env, jobject obj) {
    if (!g_edgeDetector) {
        g_edgeDetector = new EdgeDetector();
        LOGI("Native EdgeDetector initialized");
    }
}

JNIEXPORT jobject JNICALL
Java_com_example_edgedetector_EdgeDetector_processFrameNative(
        JNIEnv* env, jobject obj,
        jbyteArray yData, jint width, jint height, jint format) {
        
    if (!g_edgeDetector) {
        LOGE("EdgeDetector not initialized!");
        return nullptr;
    }
    
    try {
        // Get byte array elements from Java
        jbyte* yDataPtr = env->GetByteArrayElements(yData, nullptr);
        
        // Create Mat from YUV NV21 data
        // The NV21 format includes Y plane (width * height) + UV planes (width * height / 2)
        cv::Mat yuvData(height + height / 2, width, CV_8UC1, yDataPtr);
        cv::Mat bgr;
        
        // Convert YUV (Android camera format) to BGR (OpenCV format)
        cv::cvtColor(yuvData, bgr, cv::COLOR_YUV2BGR_NV21);
        
        // Process frame (calls the Canny implementation)
        cv::Mat processed = g_edgeDetector->processFrameEdgeDetection(bgr);
        
        // Convert the processed OpenCV Mat back to a byte array for OpenGL
        int outputSize = processed.total() * processed.elemSize();
        jbyteArray result = env->NewByteArray(outputSize);
        env->SetByteArrayRegion(result, 0, outputSize, (jbyte*)processed.data);
        
        // Release the array elements
        env->ReleaseByteArrayElements(yData, yDataPtr, JNI_ABORT);
        
        LOGI("Frame processed and converted: %d bytes", outputSize);
        return result;
    } catch (cv::Exception& e) {
        LOGE("OpenCV exception: %s", e.what());
        return nullptr;
    }
}

JNIEXPORT void JNICALL
Java_com_example_edgedetector_EdgeDetector_releaseNative(JNIEnv* env, jobject obj) {
    if (g_edgeDetector) {
        delete g_edgeDetector;
        g_edgeDetector = nullptr;
        LOGI("Native EdgeDetector released");
    }
}

} // extern "C"