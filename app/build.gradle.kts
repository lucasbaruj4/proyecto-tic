plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.freetime"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.freetime"
        minSdk = 26
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity.v140)
    implementation(libs.androidx.constraintlayout.v213)
    implementation(libs.gson.v288)

    // Room dependencies
    implementation(libs.androidx.room.runtime.v242)
    annotationProcessor(libs.androidx.room.compiler.v242)
    testImplementation(libs.androidx.room.testing.v242)

    // Navigation dependencies
    implementation(libs.androidx.navigation.fragment.v242)
    implementation(libs.androidx.navigation.ui.v242)

    // Material Design y WorkManager
    implementation(libs.androidx.work.runtime.v271)

    // Material CalendarView con exclusión de dependencias conflictivas
    implementation(libs.material.calendarview) {
        exclude("com.android.support")
        exclude("support-combat")
    }

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit.v113)
    androidTestImplementation(libs.androidx.espresso.core.v340)

    // Forzar uso de la versión más reciente de AndroidX core
    implementation("androidx.core:core:1.13.0")
}

