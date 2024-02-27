plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.arturbosch.detekt)
    alias(libs.plugins.hilt.android)
    id("com.google.firebase.appdistribution")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.marlon.portalusuario"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.marlon.portalusuario"
        minSdk = 22
        targetSdk = 34
        versionCode = 62
        versionName = "8.0.0-alpha01"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        val supportEmail = System.getenv("SUPPORT_EMAIL")
        debug {
            applicationIdSuffix = ".debug"
            buildConfigField("String", "SUPPORT_EMAIL", "\"$supportEmail\"")
        }
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
            buildConfigField("String", "SUPPORT_EMAIL", "\"$supportEmail\"")
        }
    }

    flavorDimensions += listOf("publish", "api")
    productFlavors {
        create("google-play") {
            dimension = "publish"
        }
        create("pro") {
            dimension = "publish"
            applicationIdSuffix = ".pro"
        }
        create("api22") {
            dimension = "api"
        }
        create("api26") {
            dimension = "api"
            minSdk = 26
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    lint {
        lintConfig = file("$rootDir/android-lint.xml")
        abortOnError = false
        sarifReport = true
    }
    detekt {
        buildUponDefaultConfig = true
        allRules = false
        config.setFrom(files("${rootProject.projectDir}/config/detekt/detekt.yml"))
        autoCorrect = true
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.7"
    }
    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.core.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    implementation(project(":core:ui"))
    implementation(project(":feature:cubacelmanager"))

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)

    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    // Permissions
    implementation(libs.accompanist.permissions)

    // Lottie
    implementation(libs.lottie.compose)

    // coil
    implementation(libs.coil.kt)

    // apklisupdate
    implementation(libs.apklisupdate.non.view)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.crashlytics.ktx)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Detekt rules
    detektPlugins(libs.detekt.rules.compose)

    implementation(libs.zxing.android)

    // BugSend
    implementation(libs.applifycu.bugsend)
}
