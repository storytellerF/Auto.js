plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    compileSdk = 33

    defaultConfig {
        minSdk = 19
        targetSdk = 33
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        named("release") {
            isMinifyEnabled = false
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android.txt"),
                    "proguard-rules.pro"
                )
            )
        }
    }
    namespace = "com.stardust.autojs.apkbuilder"
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.10")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.core:core-ktx:1.9.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    api(fileTree("libs") { include("*.jar") })
    api(files("libs/tiny-sign-0.9.jar"))
    api(files("libs/commons-io-2.5.jar"))
}
repositories {
    mavenCentral()
}
