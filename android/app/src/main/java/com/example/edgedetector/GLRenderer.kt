package com.example.edgedetector

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.ByteOrder

class GLRenderer : GLSurfaceView.Renderer {
    private var textureId = 0
    private var shaderProgram = 0
    private var positionHandle = 0
    private var texCoordHandle = 0
    private var samplerHandle = 0
    private var textureWidth = 0
    private var textureHeight = 0

    // Simple shaders to render a texture to a quad
    private val vertexShader = """
        attribute vec2 position;
        attribute vec2 texCoord;
        varying vec2 fragTexCoord;
        void main() {
            gl_Position = vec4(position, 0.0, 1.0);
            fragTexCoord = texCoord;
        }
    """.trimIndent()

    private val fragmentShader = """
        precision mediump float;
        varying vec2 fragTexCoord;
        uniform sampler2D sampler;
        void main() {
            gl_FragColor = texture2D(sampler, fragTexCoord);
        }
    """.trimIndent()
    // 

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        initShaders()
        textureId = createTexture()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glUseProgram(shaderProgram)
        renderQuad()
    }

    private fun initShaders() {
        val vertexShaderId = compileShader(GLES20.GL_VERTEX_SHADER, vertexShader)
        val fragmentShaderId = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader)

        shaderProgram = GLES20.glCreateProgram()
        GLES20.glAttachShader(shaderProgram, vertexShaderId)
        GLES20.glAttachShader(shaderProgram, fragmentShaderId)
        GLES20.glLinkProgram(shaderProgram)

        positionHandle = GLES20.glGetAttribLocation(shaderProgram, "position")
        texCoordHandle = GLES20.glGetAttribLocation(shaderProgram, "texCoord")
        samplerHandle = GLES20.glGetUniformLocation(shaderProgram, "sampler")

        Log.i(TAG, "Shaders initialized")
    }

    private fun compileShader(type: Int, source: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, source)
        GLES20.glCompileShader(shader)
        return shader
    }

    private fun createTexture(): Int {
        val textureIds = IntArray(1)
        GLES20.glGenTextures(1, textureIds, 0)
        val textureId = textureIds[0]
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        return textureId
    }

    private fun renderQuad() {
        // Quad vertices and texture coordinates
        val vertices = floatArrayOf(
            -1f, -1f, 0f, 1f,   // Bottom-Left (pos, tex)
            1f, -1f, 1f, 1f,    // Bottom-Right
            1f, 1f, 1f, 0f,     // Top-Right
            -1f, 1f, 0f, 0f      // Top-Left
        )

        // The float buffer holds position (x, y) and texture coordinate (u, v) interleaved
        val vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(vertices)
                position(0)
            }
        }
        
        // Define attribute pointers
        // Position (2 floats per vertex, stride 16 bytes, start at offset 0)
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 16, vertexBuffer)
        GLES20.glEnableVertexAttribArray(positionHandle)

        // Texture Coordinate (2 floats per vertex, stride 16 bytes, start at offset 8)
        vertexBuffer.position(2) // Move buffer position to the start of the texture coords
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 16, vertexBuffer)
        GLES20.glEnableVertexAttribArray(texCoordHandle)
        
        // Draw the quad
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4)
        
        // Disable attribute arrays after drawing
        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(texCoordHandle)
    }

    fun updateTexture(data: ByteArray, width: Int, height: Int) {
        if (textureId == 0) return
        textureWidth = width
        textureHeight = height
        
        // Bind texture and upload the BGR byte data from C++
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D, 
            0, 
            GLES20.GL_RGB, // Internal format is RGB
            width, 
            height, 
            0,
            GLES20.GL_RGB, // Input format is RGB
            GLES20.GL_UNSIGNED_BYTE, 
            ByteBuffer.wrap(data)
        )
    }

    companion object {
        private const val TAG = "GLRenderer"
    }
}