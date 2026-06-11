plugins {
    id("com.android.application")
    alias(libs.plugins.google.services)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.firebase.appdistribution)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.arturbosch.detekt)
    alias(libs.plugins.kotlin.plugin.compose)
    alias(libs.plugins.ktlint)
}

android {
    compileSdk = 35
    buildToolsVersion = "35.0.0"

    defaultConfig {
        resourceConfigurations.add("en")
        applicationId = "com.marlon.portalusuario"
        minSdk = 26
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
    // ── Compose (BOM manages versions) ──
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)

    // ── Firebase ──
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.crashlytics.ktx)

    // ── Hilt ──
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.dagger)
    ksp(libs.hilt.android.compiler)

    // ── Room ──
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    // ── Lifecycle ──
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.fragment.ktx)

    // ── Navigation ──
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.runtime)

    // ── DataStore ──
    implementation(libs.androidx.datastore.preferences)

    // ── WorkManager ──
    implementation(libs.androidx.work.runtime.ktx)

    // ── Coroutines ──
    runtimeOnly(libs.kotlinx.coroutines.android)

    // ── SuiteTecsa SDK ──
    implementation(libs.suitetecsa.sdk.android)
    implementation(libs.suitetecsa.sdk.kotlin)

    // ── AndroidX Core ──
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.annotation)

    // ── Views / Layouts (XML legacy) ──
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.coordinatorlayout)
    implementation(libs.androidx.drawerlayout)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.material)

    // ── CameraX ──
    runtimeOnly(libs.androidx.camera.core)
    runtimeOnly(libs.androidx.camera.camera2)

    // ── UI Libraries ──
    implementation(libs.circleimageview)
    implementation(libs.sdp.android)
    implementation(libs.ssp.android)
    implementation(libs.glidetovectoryou)
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)
    implementation(libs.neumorphism)

    // ── Image Loading ──
    implementation(libs.glide)
    runtimeOnly(libs.picasso)

    // ── Utilities ──
    implementation(libs.caverock.androidsvg.aar)
    implementation(libs.customtabs)
    implementation(libs.dexter)
    implementation(libs.gson)
    implementation(libs.guava)
    implementation(libs.jwtdecode)
    implementation(libs.prettytime)
    implementation(libs.swipetoaction.library)
    implementation(libs.ui.library)

    // ── Testing ──
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.androidx.test.ext.junit)
    debugImplementation(libs.compose.ui.tooling)
    debugRuntimeOnly(libs.compose.ui.test.manifest)

    // ── JARs ──
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}
