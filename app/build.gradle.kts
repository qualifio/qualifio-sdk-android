import groovy.json.StringEscapeUtils
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
    alias(apps.plugins.android.application)
    alias(apps.plugins.kotlin.android)
    alias(apps.plugins.kotlin.compose)
    alias(apps.plugins.qualifio.services)
    alias(apps.plugins.google.hilt)
    alias(apps.plugins.google.ksp)
    alias(apps.plugins.kotlin.serialization)
}

val properties = loadProperties("local.properties")

android {
    namespace = "com.qualifio.sample"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.qualifio.sample.app"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"

        buildConfigField(
            "String",
            "SECURE_TOKEN_SECRET",
            "\"${StringEscapeUtils.escapeJava(properties.getProperty("secure.token.secret", ""))}\"",
        )

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(apps.androidx.core.ktx)
    implementation(apps.androidx.lifecycle.runtime.ktx)
    implementation(apps.androidx.activity.compose)
    implementation(platform(apps.androidx.compose.bom))
    implementation(apps.bundles.androidx.compose.ui)
    implementation(apps.androidx.compose.material3)
    implementation(apps.androidx.datastore)
    implementation(apps.androidx.lifecycle.viewModel.compose)
    implementation(apps.bundles.androidx.navigation3)

    implementation(apps.timber)

    implementation(apps.moshi.kotlin)
    implementation(apps.moshi.adapters)
    ksp(apps.moshi.codegen)

    implementation(apps.google.hilt)
    implementation(apps.androidx.hilt.navigation.compose)
    ksp(apps.google.hilt.compiler)
    ksp(apps.androidx.hilt.compiler)

    implementation(apps.kotlinx.serialization.json)
    implementation(apps.auth0.jwt)

    implementation(project(":qualifio"))
    implementation(project(":qualifio-compose"))
}
