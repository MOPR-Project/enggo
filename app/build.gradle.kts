plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.enggo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.enggo"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    sourceSets {
        getByName("main") {
            res {
                srcDirs("src\\main\\res", "src\\main\\res\\layouts\\auth",
                    "src\\main\\res",
                    "src\\main\\res\\layouts\\learn", "src\\main\\res", "src\\main\\res\\layouts\\user"
                )
            }
        }
    }
}

dependencies {

    implementation (libs.flexbox)
    implementation (libs.retrofit)
    implementation (libs.converter.gson)

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.glide)
    implementation (libs.threetenabp)
    implementation (libs.play.services.auth)
}