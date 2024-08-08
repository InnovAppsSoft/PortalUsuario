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
}

android {
    compileSdk = 34
    buildToolsVersion = "34.0.0"

    defaultConfig {
        resourceConfigurations.add("en")
        applicationId = "com.marlon.portalusuario"
        minSdk = 24
        targetSdk = 34
        versionCode = 60
        versionName = "8.0-beta02"
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
    implementation(libs.suitetecsa.sdk.kotlin.old)

    // Promotions
    implementation(libs.caverock.androidsvg.aar)

    // Features
    implementation(project(":feature:nauta-nav"))

    // apklisupdate
    implementation(libs.apklisupdate)

    // Lottie
    implementation(libs.lottie.compose)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.crashlytics.ktx)

    // ViewModel
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.activity.ktx)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.gridlayout)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.jwtdecode)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
    ksp(libs.hilt.android.compiler)

    // Room
    implementation(libs.room.ktx)
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)

    // LiveData
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.lifecycle.livedata)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Camerax
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.camera.view)

    // Glide
    implementation(libs.glide)

    // QR scanner
    implementation(libs.zxing.core)

    // MultiDex
    implementation(libs.androidx.multidex)

    // FileTree
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // AndroidX Libraries
    implementation(libs.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.swiperefreshlayout)

    // Test Libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // UI Libraries
    implementation(libs.circleimageview)
    implementation(libs.material)
    implementation(libs.androidx.cardview)

    // Additional Libraries
    implementation(libs.guava)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.viewpager2)
    implementation(libs.stickyswitch)
    implementation(libs.ucrop)
    implementation(libs.zxing.android.integration)
    implementation(libs.zxing)
    implementation(libs.dexter)
    implementation(libs.library)
    implementation(libs.circleimageview)
    implementation(libs.customtabs)
    implementation(libs.jsoup)
    implementation(libs.gson)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.taptargetview)
    implementation(libs.deviceinfo)
    implementation(libs.signalstrengths)
    implementation(libs.signalview)
    implementation(libs.log)
    implementation(libs.ratethisapp)
    implementation(libs.mpandroidchart)
    implementation(libs.materialprogressbar.library)
    implementation(libs.sdp.android)
    implementation(libs.segmentedbarview)
    implementation(libs.code.scanner)
    implementation(libs.intentanimation)
    implementation(libs.play.services.ads)
    implementation(libs.sdp.android)
    implementation(libs.ssp.android)
    implementation(libs.neumorphism)
    implementation(libs.roundedimageview)
    implementation(libs.glidetovectoryou)
    implementation(libs.picasso)
    implementation(libs.appusagemonitor)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.okhttp)
    implementation(libs.androidx.vectordrawable)
    implementation(libs.androidx.palette)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.carouselview)
    implementation(libs.rxandroid)
    implementation(libs.rxjava)
    implementation(libs.colorpreference.core)
    implementation(libs.support)
    implementation(libs.lobsterpicker)
    implementation(libs.emoji.material)
    implementation(libs.labelview)
    implementation(libs.toggle.button.group)
    implementation(libs.bottomdrawer)
    implementation(libs.caverock.androidsvg.aar)
    implementation(libs.sharp.library)
    implementation(libs.prettytime)
    implementation(libs.swipetoaction.library)
    implementation(libs.materialstepperview)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.floatingactionbutton)
    implementation(libs.sweetalert.library)
    implementation(libs.showcaseview.library)
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)
}
