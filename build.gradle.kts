// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false

    // ✅ Hilt (version explicitly set and applied false)
    id("com.google.dagger.hilt.android") version "2.55" apply false

    // ✅ KSP for Room (version explicitly set and applied false)
    id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false

    // ✅ KAPT for Hilt
    id("org.jetbrains.kotlin.kapt") version "2.0.21" apply false // Add KAPT plugin here for Hilt
}

