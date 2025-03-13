import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    id ("kotlin-kapt")
    id ("dagger.hilt.android.plugin")
}

android {
    namespace = "com.example.kaloritakip"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.kaloritakip"
        minSdk = 25
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        val localProperties = Properties().apply {
            load(rootProject.file("local.properties").inputStream())
        }

        val rapidApiKey = localProperties.getProperty("rapidApiKey") ?: ""
        buildConfigField("String", "rapidApiKey", "\"$rapidApiKey\"")

        val nutritionApiKey = localProperties.getProperty("nutritionApiKey") ?: ""
        buildConfigField("String", "nutritionApiKey", "\"$nutritionApiKey\"")

        val nutritionAppId = localProperties.getProperty("nutritionAppId") ?: ""
        buildConfigField("String", "nutritionAppId", "\"$nutritionAppId\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
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
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.androidx.compose.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.3")

    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.5")

    implementation ("androidx.navigation:navigation-compose:2.8.5")

    implementation ("com.google.dagger:hilt-android:2.48")
    kapt ("com.google.dagger:hilt-compiler:2.48")

    implementation ("androidx.hilt:hilt-navigation-compose:1.0.0")

    implementation ("com.github.bumptech.glide:compose:1.0.0-beta01")

    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation("com.github.skydoves:landscapist-glide:2.2.10")

    implementation("androidx.compose.animation:animation:1.6.0")
    implementation("androidx.compose.foundation:foundation-layout-android:1.7.7")

    implementation("androidx.datastore:datastore-preferences:1.1.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    implementation("androidx.compose.material:material:1.7.8")

    implementation ("androidx.compose.animation:animation:1.7.8")

}