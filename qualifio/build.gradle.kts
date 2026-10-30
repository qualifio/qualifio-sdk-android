import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.mavenPublish)
}

group = libs.versions.maven.artifactGroup.get().toString()
version = libs.versions.maven.artifactVersion.get().toString()

android {
    namespace = "com.qualifio"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 23

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
            explicitApi()
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    api(libs.androidx.browser)
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    coordinates(
        groupId = project.group.toString(),
        artifactId = project.name,
        version = project.version.toString(),
    )

    pom {
        name.set("Qualifio Android SDK")
        description.set("Android SDK for integrating Qualifio campaigns into mobile apps.")
        url.set("https://github.com/qualifio/qualifio-sdk-android/")

        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/license/mit")
                distribution.set("https://opensource.org/license/mit")
            }
        }

        developers {
            developer {
                id.set("Qualifio")
                name.set("Qualifio")
                url.set("https://github.com/qualifio/")
            }
        }

        scm {
            url.set("https://github.com/qualifio/qualifio-sdk-android/")
            connection.set("scm:git:git://github.com/qualifio/qualifio-sdk-android.git")
            developerConnection.set("scm:git:ssh://git@github.com/qualifio/qualifio-sdk-android.git")
        }
    }
}
