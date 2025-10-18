plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.gms.google-services")
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
        vectorDrawables { useSupportLibrary = true }
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
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true }
}

dependencies {
    // Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))

    // Firebase libs
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    androidTestImplementation("com.google.firebase:firebase-auth-ktx")
    androidTestImplementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")

    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:21.4.0")

    // Core / Lifecycle / Compose
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.4")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // Navigation
    implementation("androidx.navigation:navigation-runtime-ktx:2.9.5")
    implementation("androidx.navigation:navigation-compose:2.9.5")

    // Splashscreen
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.test.ext:junit-ktx:1.3.0")

    // Tests
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.09.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    // TODO: add the below to figma
    val coroutinesVersion = "1.8.1"
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:$coroutinesVersion")
    // Use for integration tests
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")

    // Use for local unit tests
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.0")
    androidTestImplementation("org.jetbrains.kotlin:kotlin-test:1.9.0")
    androidTestImplementation("androidx.test:core:1.5.0")
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("androidx.test:runner:1.7.0")
    androidTestImplementation("androidx.test:runner:1.7.0")
    testImplementation("androidx.test.ext:junit-ktx:1.3.0")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.3.0")
    testImplementation("androidx.test:rules:1.7.0")
    androidTestImplementation("androidx.test:rules:1.7.0")


}
