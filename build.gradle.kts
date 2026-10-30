// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(apps.plugins.android.application) apply false
    alias(apps.plugins.google.hilt) apply false
    alias(apps.plugins.google.ksp) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.mavenPublish) apply false
}
