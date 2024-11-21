plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin) apply false
    id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false
    id("com.autonomousapps.dependency-analysis") version "2.4.2"
}