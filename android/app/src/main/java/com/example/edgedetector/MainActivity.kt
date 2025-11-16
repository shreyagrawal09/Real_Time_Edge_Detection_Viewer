package com.example.edgedetector

import android.Manifest
import android.content.pm.PackageManager
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.SurfaceHolder
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    
    private lateinit var glSurfaceView: GLSurfaceView
    private lateinit var glRenderer: GLRenderer
    private lateinit var cameraManager: CameraManager
    private lateinit var edgeDetector: EdgeDetector
    private lateinit var statusText: TextView
    private lateinit var toggleButton: Button
    
    private var isProcessing = true
    private var frameCount = 0
    private var fps = 0
    private var lastFpsTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Request permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            initializeApp()
        }
    }

    private fun initializeApp() {
        glSurfaceView = findViewById(R.id.glSurfaceView)
        statusText = findViewById(R.id.statusText)
        toggleButton = findViewById(R.id.toggleButton)

        glRenderer = GLRenderer()
        
        // Configure GLSurfaceView
        glSurfaceView.setEGLContextClientVersion(2) // Request OpenGL ES 2.0
        glSurfaceView.setPreserveEGLContextOnPause(true)
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        glSurfaceView.setRenderer(glRenderer)
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY // Only render when new frame is ready

        // Initialize JNI and C++ side
        edgeDetector = EdgeDetector()
        edgeDetector.initNative()

        // Initialize and start camera feed
        cameraManager = CameraManager(this)
        cameraManager.startCamera(glSurfaceView.holder) { yData, width, height ->
            // This lambda runs every time a new frame is captured
            processFrame(yData, width, height)
        }

        toggleButton.setOnClickListener {
            isProcessing = !isProcessing
            toggleButton.text = if (isProcessing) "Stop Processing" else "Start Processing"
        }
    }

    private fun processFrame(yData: ByteArray, width: Int, height: Int) {
        if (!isProcessing) {
            // If processing is stopped, we still need to handle the camera queue, 
            // but we skip the heavy edge detection work.
            return
        }
        
        // FPS calculation
        frameCount++
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastFpsTime >= 1000) {
            fps = frameCount
            frameCount = 0
            lastFpsTime = currentTime
            
            // Post status update back to the main UI thread
            runOnUiThread {
                statusText.text = "FPS: $fps | Resolution: ${width}x${height}"
            }
        }

        // Call the native C++ code to process the frame using OpenCV
        val processedData = edgeDetector.processFrameNative(yData, width, height, 0)

        // Update the texture on the GL thread and request a render
        processedData?.let {
            glSurfaceView.queueEvent {
                glRenderer.updateTexture(it, width, height)
            }
            glSurfaceView.requestRender()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                initializeApp()
            } else {
                // Handle denial
                runOnUiThread {
                    statusText.text = "Camera permission denied. App cannot run."
                }
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraManager.stopCamera()
        edgeDetector.releaseNative()
    }

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
    }
}