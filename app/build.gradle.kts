plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    alias(libs.plugins.devtools.ksp)
    id("com.google.dagger.hilt.android")
    id("com.google.firebase.appdistribution")
    id("com.google.firebase.crashlytics")
}

android {
    compileSdk = 34
    buildToolsVersion = "33.0.1"
    defaultConfig {
        resourceConfigurations.add("en")
        applicationId = "com.marlon.portalusuario"
        minSdk = 22
        targetSdk = 34
        versionCode = 59
        versionName = "7.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
        signingConfig = signingConfigs.getByName("debug")
        multiDexEnabled = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
            isDebuggable = false
            isJniDebuggable = false
            isRenderscriptDebuggable = false
            isShrinkResources = true
        }
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    ndkVersion = "22.0.7026061"
    namespace = "com.marlon.portalusuario"
}

dependencies {
    implementation("androidx.multidex:multidex:2.0.1")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.play:core:1.10.3")
    implementation("androidx.preference:preference-ktx:1.2.0")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.navigation:navigation-ui:2.5.3")
    implementation("androidx.navigation:navigation-fragment:2.5.3")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.google.code.gson:gson:2.10.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.guava:guava:29.0-android")

    // Features
    implementation(project(":feature:nauta-nav"))

    // apklisupdate
    implementation(libs.apklisupdate)

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
    kapt(libs.hilt.android.compiler)

    // Room
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    implementation("androidx.work:work-runtime-ktx:2.8.1")
    implementation("androidx.work:work-runtime:2.8.1")

    // ViewPaper2
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("com.github.GwonHyeok:StickySwitch:0.0.15")

    // Library Barcode
    // dexter permissions
    implementation("com.github.yalantis:ucrop:2.2.2")
    implementation("com.google.zxing:android-integration:3.3.0")
    implementation("me.dm7.barcodescanner:zxing:1.9.13")
    implementation("com.karumi:dexter:6.2.3")

    // add these libraries
    implementation("com.rengwuxian.materialedittext:library:2.1.4")
    implementation("de.hdodenhof:circleimageview:3.1.0")

    //noinspection GradleCompatible
    implementation("com.android.support:customtabs:28.0.0")

    implementation("org.jsoup:jsoup:1.16.1")
    implementation("com.google.code.gson:gson:2.10.1")

    // Navigation Compose
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

    // LiveData
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.lifecycle.livedata)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Camerax
    val camerax_version = "1.2.3"
    implementation("androidx.camera:camera-core:${camerax_version}")
    implementation("androidx.camera:camera-camera2:${camerax_version}")
    implementation("androidx.camera:camera-lifecycle:${camerax_version}")
    implementation("androidx.camera:camera-view:${camerax_version}")

    // QR scanner
    implementation("com.google.zxing:core:3.5.2")

    // Tap Tarjet
    implementation("com.getkeepsafe.taptargetview:taptargetview:1.13.3")
    implementation("com.an.deviceinfo:deviceinfo:0.1.5")

    // Nuevas
    implementation("james.signalstrengths:signalstrengths:0.0.4")
    implementation("com.praween.signalview:signalview:1.0")
    implementation("co.trikita:log:1.1.5")
    implementation("io.github.kobakei:ratethisapp:1.2.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.0.3")

    implementation("me.zhanghai.android.materialprogressbar:library:1.6.1")
    implementation("com.intuit.sdp:sdp-android:1.1.0")

    implementation("mobi.gspd:segmentedbarview:1.1.6@aar")
    implementation("com.budiyev.android:code-scanner:2.1.0")
    implementation("com.github.hajiyevelnur92:intentanimation:1.0")
    implementation("com.google.android.gms:play-services-ads:22.1.0")

    implementation("com.intuit.sdp:sdp-android:1.1.0")
    implementation("com.intuit.ssp:ssp-android:1.1.0")
    implementation("com.github.fornewid:neumorphism:0.3.0")
    implementation("com.makeramen:roundedimageview:2.3.0")

    implementation("com.github.2coffees1team:GlideToVectorYou:v2.0.0")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation("com.github.bumptech.glide:annotations:4.15.1")
    implementation("com.github.bumptech.glide:okhttp3-integration:4.15.1") {
        exclude(group = "glide-parent")
    }

    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("the.bot.box:appusagemonitor:2.1.0")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.11")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.11")

    implementation("androidx.vectordrawable:vectordrawable:1.1.0")
    implementation("androidx.palette:palette:1.0.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    implementation("com.synnapps:carouselview:0.1.5")
    implementation("com.github.smarteist:autoimageslider:1.4.0")

    implementation("io.reactivex.rxjava3:rxandroid:3.0.2")
    implementation("io.reactivex.rxjava3:rxjava:3.1.6")

    implementation("com.github.kizitonwose.colorpreference:core:1.1.0")
    implementation("com.github.kizitonwose.colorpreference:support:1.1.0")
    implementation("com.larswerkman:lobsterpicker:1.0.1")

    // ///////////////////
    implementation("com.vanniktech:emoji-material:0.16.0")
    implementation("com.github.linger1216:labelview:v1.1.2")
    implementation("com.nex3z:toggle-button-group:1.2.3")
    implementation("com.github.HeyAlex:BottomDrawer:v1.0.0")
    implementation("androidx.biometric:biometric:1.1.0")
    implementation(group = "com.an.biometric", name = "biometric-auth", version = "0.1.0", ext = "aar", classifier = "")
    implementation("com.caverock:androidsvg-aar:1.4")
    implementation("com.pixplicity.sharp:library:1.1.0")

    // for pretty time format -> hace momentos, hace un dia, etc.
    implementation("org.ocpsoft.prettytime:prettytime:5.0.6.Final")

    // swipe list item
    implementation("co.dift.ui.swipetoaction:library:1.1")

    // first time permission requester
    implementation("moe.feng:MaterialStepperView:0.2.5")

    //
    //noinspection GradleDependency
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.6.2")
    //noinspection GradleDependency
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    //
    implementation("com.getbase:floatingactionbutton:1.10.1")
    // Sweet Alert Dialog
    implementation("com.github.f0ris.sweetalert:library:1.6.2")
    // Show case
    implementation("com.github.amlcurran.showcaseview:library:5.4.3")
    //
}
