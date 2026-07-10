plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "aeu.ratha.beckie_deal_app"
    compileSdk = 36

    defaultConfig {
        applicationId = "aeu.ratha.beckie_deal_app"
        minSdk = 24
        targetSdk = 36
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Add CardView for product cards
    implementation("androidx.cardview:cardview:1.0.0")

    // RecyclerView for efficient scrolling product grids
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Glide - Image loading library for product images (Latest version)
    implementation("com.github.bumptech.glide:glide:5.0.0-rc01")
    annotationProcessor("com.github.bumptech.glide:compiler:5.0.0-rc01")

    // Material Design (Latest compatible version)
    implementation("com.google.android.material:material:1.13.0-alpha03")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}