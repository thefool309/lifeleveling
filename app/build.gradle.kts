plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.gms.google-services")                    // Google service plug-in
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.lifeleveling.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.lifeleveling.app"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Enables Icon Use
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
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
    // Firebase Bill of Materials (keeps all Firebase libraries in sync)
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))

    // Firebase (Authentication, Cloud Firestore, Firebase Cloud Messaging, Firebase Crashlytics)
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-crashlytics")

    // Google Play Services Auth – enables Google Sign-In and OAuth authentication
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    // Firebase Kotlin Extensions to add Kotlin-specific conveniences
    implementation("com.google.firebase:firebase-firestore-ktx")

    //Core KTX - Kotlin Extensions for Android framework APIs
    implementation("androidx.core:core-ktx:1.10.1")

    //Lifecycle Runtime KTX: lifecycle-aware components for Compose and ViewModels
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.4")

    //Activity Compose: integrates Jetpack compose with Activity lifecycle
    implementation("androidx.activity:activity-compose:1.8.0")

    // Jetpack Compose BOM – synchronizes Compose version dependencies
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")

    // Material3 – Material Design components for modern UI styling
    implementation("androidx.compose.material3:material3")

    // Unit Testing – standard JUnit framework
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")

    // UI Testing – Espresso framework for UI automation
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.09.00"))

    // Compose UI Test JUnit4 – allows writing Compose-based UI tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Compose Test Manifest – required manifest for running Compose UI tests
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Android SplashScreen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Jetpack Navigation
    implementation("androidx.navigation:navigation-runtime-ktx:2.9.5")
    implementation("androidx.navigation:navigation-compose:2.9.5")

}