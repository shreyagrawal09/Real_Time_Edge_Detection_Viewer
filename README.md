# Real-Time Edge Detection Viewer

A comprehensive Android + OpenCV + OpenGL + TypeScript application
demonstrating:

-   Real-time camera frame capture\
-   Edge detection using OpenCV C++\
-   GPU-accelerated rendering with OpenGL ES 2.0\
-   Web-based frame viewer

------------------------------------------------------------------------

## ✨ Features Implemented

### **Android App**

-   ✅ Camera2 API for real-time frame capture\
-   ✅ Canny edge detection via OpenCV C++\
-   ✅ JNI bridge for Java ↔ Native communication\
-   ✅ OpenGL ES 2.0 texture rendering\
-   ✅ FPS counter and performance stats\
-   ✅ Toggle between processing modes\
-   ✅ \~15--30 FPS real-time performance

### **Web Viewer**

-   ✅ TypeScript-based canvas renderer\
-   ✅ Static frame display with overlay stats\
-   ✅ Filter selection (Edge Detection / Grayscale)\
-   ✅ Frame download capability\
-   ✅ Responsive design

------------------------------------------------------------------------

## 📁 Project Structure

    EdgeDetectorApp/
    ├── android/
    │   ├── app/
    │   │   ├── src/main/
    │   │   │   ├── java/com/example/edgedetector/
    │   │   │   │   ├── MainActivity.kt
    │   │   │   │   ├── CameraManager.kt
    │   │   │   │   ├── GLRenderer.kt
    │   │   │   │   └── EdgeDetector.kt
    │   │   │   ├── cpp/
    │   │   │   │   ├── native-lib.cpp
    │   │   │   │   ├── edge_detection.cpp
    │   │   │   │   └── edge_detection.h
    │   │   │   └── res/
    │   │   └── build.gradle
    │   ├── CMakeLists.txt
    │   └── gradle.properties
    │
    ├── web/
    │   ├── src/
    │   │   ├── index.ts
    │   │   ├── index.html
    │   │   └── styles.css
    │   ├── dist/
    │   ├── package.json
    │   └── tsconfig.json
    │
    ├── .gitignore
    └── README.md

------------------------------------------------------------------------

## 🧩 Architecture & Data Flow

    Camera2 API
        ↓
    ImageReader (YUV data)
        ↓
    Kotlin processFrame()
        ↓
    JNI Bridge → C++
        ↓
    OpenCV: YUV → BGR → Blur → Canny → BGR
        ↓
    Return ByteArray to Kotlin
        ↓
    OpenGL ES 2.0 Renderer
        ↓
    Display on GLSurfaceView

------------------------------------------------------------------------

## 📋 Prerequisites

### **System Requirements**

-   OS: macOS, Linux, Windows (WSL2 recommended)\
-   RAM: 8GB+ (16GB recommended)\
-   Disk: 10GB+ (Android SDK + NDK)

### **Software**

-   Java JDK 11+\
-   Android Studio\
-   NDK 21.4.7075529+\
-   Git\
-   Node.js (for web viewer)\
-   OpenCV Android SDK

------------------------------------------------------------------------

## 🔧 Setup Instructions

### **Step 1: Clone Repository**

``` bash
git clone https://github.com/shreyagrawal09/EdgeDetectorApp.git
cd EdgeDetectorApp
```

### **Step 2: Configure Paths**

Add to `C:\Users\YOUR_USERNAME\.android\local.properties`:

    sdk.dir=C:\Users\YOUR_USERNAME\AppData\Local\Android\sdk
    ndk.dir=C:\Users\YOUR_USERNAME\AppData\Local\Android\sdk\ndk\21.4.7075529
    opencv.dir=C:\opencv-android-sdk\OpenCV-android-sdk

### **Step 3: Build Android App**

-   Import OpenCV module in Android Studio\
-   Sync Gradle\
-   Build APK:

``` bash
./gradlew assembleDebug
./gradlew installDebug
```

### **Step 4: Build Web Viewer**

``` bash
cd web
npm install
npm run build
npm run serve
```

Then open: **http://localhost:8080**

------------------------------------------------------------------------

## 📜 License
© 2025 Shrey Agrawal
