plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.edgedetector"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.edgedetector"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // --- START: NATIVE CODE (NDK/C++) CONFIGURATION ---

        // 1. Tell Android Gradle Plugin about C++ build settings
        externalNativeBuild {
            cmake {
                // Specify C++ standard
                cppFlags += "-std=c++17"
            }
        }

        // 2. Specify which CPU architectures to build for
        ndk {
            // Common architectures for modern phones that OpenCV supports
            abiFilters 'armeabi-v7a', 'arm64-v8a'
        }
        // --- END: NATIVE CODE (NDK/C++) CONFIGURATION ---
    }

    buildTypes {
        release {
            isMinifyEnabled = true // Changed to true for production build
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }
    
    // --- START: SOURCE SETS & CMAKE PATH CONFIGURATION ---
    
    // 3. Define where the C++ build script is located (relative to the /android folder)
    externalNativeBuild {
        cmake {
            path = file('../CMakeLists.txt') // CMakeLists.txt is in the parent 'android' directory
        }
    }
    
    // 4. Configure source sets to match your manual structure (essential for NDK)
    sourceSets {
        main {
            // Set the path for Kotlin/Java source files
            java.srcDirs = ['src/main/java']

            // Set the path for C++ source files
            jniLibs.srcDirs = ['src/main/cpp']

            // Ensure Android Studio recognizes the files are in the package structure
            manifest.srcFile 'src/main/AndroidManifest.xml'
            res.srcDirs = ['src/main/res']
            assets.srcDirs = ['src/main/assets']
        }
    }
    // --- END: SOURCE SETS & CMAKE PATH CONFIGURATION ---

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    
    // You might also need a specific dependency for your camera/GL/utility logic
    // For now, we will assume standard libraries cover it.

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}