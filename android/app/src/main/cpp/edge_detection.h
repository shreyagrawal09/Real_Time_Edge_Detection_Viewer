#ifndef EDGE_DETECTION_H
#define EDGE_DETECTION_H

#include <opencv2/opencv.hpp>

class EdgeDetector {
public:
    EdgeDetector();
    cv::Mat processFrameEdgeDetection(const cv::Mat& frame);
    cv::Mat processFrameGrayscale(const cv::Mat& frame);

private:
    int cannyThreshold1 = 50;
    int cannyThreshold2 = 150;
};

#endif // EDGE_DETECTION_H