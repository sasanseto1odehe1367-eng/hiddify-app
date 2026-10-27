plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "com.sasan.vpn"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.sasanvpn.nativeclient"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0-test"
        ndk { abiFilters += listOf("arm64-v8a") }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    packaging { jniLibs { useLegacyPackaging = true } }
}
dependencies {
    implementation(files("libs/libbox.aar"))
}
