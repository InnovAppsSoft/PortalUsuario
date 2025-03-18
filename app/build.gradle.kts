plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.devtools.ksp)
    id("com.google.dagger.hilt.android")
    id("com.google.firebase.appdistribution")
    id("com.google.firebase.crashlytics")
    alias(libs.plugins.arturbosch.detekt)
    alias(libs.plugins.kotlin.plugin.compose)
    id("com.autonomousapps.dependency-analysis")
}

android {
    compileSdk = 34
    buildToolsVersion = "34.0.0"

    defaultConfig {
        resourceConfigurations.add("en")
        applicationId = "com.marlon.portalusuario"
        minSdk = 24
        targetSdk = 34
        versionCode = 81
        versionName = "8.0.0-beta04"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
        multiDexEnabled = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    buildFeatures {
        viewBinding = true
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    lint {
        lintConfig = file("$rootDir/android-lint.xml")
        abortOnError = false
        sarifReport = true
    }

    detekt {
        buildUponDefaultConfig = true
        allRules = false
        autoCorrect = true
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    ndkVersion = "22.0.7026061"
    namespace = "com.marlon.portalusuario"
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)

    // Detekt
    detektPlugins(libs.detekt.rules.compose)

    // SuitEtecsa libs
    implementation(libs.suitetecsa.sdk.android)
    implementation(libs.suitetecsa.sdk.kotlin)

    // Promotions
    implementation(libs.caverock.androidsvg.aar)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.crashlytics.ktx)

    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.activity.ktx)

    // Coroutines
    runtimeOnly(libs.kotlinx.coroutines.android)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.jwtdecode)
    androidTestImplementation(platform(libs.compose.bom))
    debugImplementation(libs.compose.ui.tooling)
    debugRuntimeOnly(libs.compose.ui.test.manifest)
    ksp(libs.hilt.android.compiler)

    // Room
    implementation(libs.room.ktx)
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)

    // Camerax
    runtimeOnly(libs.androidx.camera.core)
    runtimeOnly(libs.androidx.camera.camera2)

    // Glide
    implementation(libs.glide)

    // FileTree
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // AndroidX Libraries
    implementation(libs.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.swiperefreshlayout)

    // Test Libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)

    // UI Libraries
    implementation(libs.circleimageview)
    implementation(libs.material)
    implementation(libs.androidx.cardview)

    // Additional Libraries
    implementation(libs.guava)
    implementation(libs.androidx.work.runtime.ktx)
    runtimeOnly(libs.androidx.work.runtime)
    implementation(libs.stickyswitch)
    implementation(libs.dexter)
    implementation(libs.circleimageview)
    implementation(libs.customtabs)
    implementation(libs.gson)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.sdp.android)
    implementation(libs.intentanimation)
    implementation(libs.play.services.ads)
    implementation(libs.sdp.android)
    implementation(libs.ssp.android)
    implementation(libs.neumorphism)
    implementation(libs.glidetovectoryou)
    runtimeOnly(libs.picasso)
    implementation(libs.colorpreference.core)
    implementation(libs.support)
    implementation(libs.emoji.material)
    implementation(libs.labelview)
    implementation(libs.bottomdrawer)
    implementation(libs.caverock.androidsvg.aar)
    implementation(libs.prettytime)
    implementation(libs.swipetoaction.library)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.sweetalert.library)
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)

    androidTestImplementation(libs.androidx.monitor)
    androidTestImplementation(libs.junit)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.animation.core)
    implementation(libs.androidx.animation)
    implementation(libs.androidx.foundation.layout)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.ui.geometry)
    implementation(libs.androidx.ui.text)
    implementation(libs.androidx.ui.unit)
    implementation(libs.androidx.coordinatorlayout)
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.datastore.preferences.core)
    implementation(libs.androidx.drawerlayout)
    implementation(libs.androidx.lifecycle.common)
    implementation(libs.androidx.lifecycle.livedata.core)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.androidx.navigation.common)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.runtime)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.sqlite)
    implementation(libs.dagger)
    implementation(libs.hilt.core)
    implementation(libs.materialish.progress)
    implementation(libs.ui)
    implementation(libs.javax.inject)
    implementation(libs.kotlinx.coroutines.core)
}

dependencyAnalysis {
    issues {
        onUnusedDependencies {
            severity("fail")
        }
        onUsedTransitiveDependencies {
            severity("warn")
        }
        onIncorrectConfiguration {
            severity("fail")
        }
        onUnusedAnnotationProcessors {
            severity("fail")
        }
        onRedundantPlugins {
            severity("fail")
        }
    }
}
