#include "edge_detection.h"
#include <android/log.h>

#define TAG "EdgeDetector"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

EdgeDetector::EdgeDetector() {
    LOGI("EdgeDetector initialized");
}

cv::Mat EdgeDetector::processFrameEdgeDetection(const cv::Mat& frame) {
    if (frame.empty()) {
        LOGE("Input frame is empty!");
        return frame;
    }

    cv::Mat gray, edges, output;

    // Convert to grayscale
    cv::cvtColor(frame, gray, cv::COLOR_BGR2GRAY);

    // Apply Gaussian blur to reduce noise
    cv::GaussianBlur(gray, gray, cv::Size(5, 5), 1.5);

    // Apply Canny edge detection
    cv::Canny(gray, edges, cannyThreshold1, cannyThreshold2);
    // 

    // Convert back to BGR for display (so it can be correctly rendered as color texture)
    cv::cvtColor(edges, output, cv::COLOR_GRAY2BGR);

    LOGI("Edge detection processed: %d x %d", output.cols, output.rows);
    return output;
}

cv::Mat EdgeDetector::processFrameGrayscale(const cv::Mat& frame) {
    if (frame.empty()) {
        LOGE("Input frame is empty!");
        return frame;
    }
    cv::Mat gray, output;
    cv::cvtColor(frame, gray, cv::COLOR_BGR2GRAY);
    cv::cvtColor(gray, output, cv::COLOR_GRAY2BGR);
    return output;
}