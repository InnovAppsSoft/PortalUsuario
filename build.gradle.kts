import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath(libs.androidx.navigation.safe.args.gradle.plugin)
        classpath(libs.gradle)
        classpath(libs.google.services)
        classpath(libs.kotlin.gradle.plugin)
        classpath(libs.firebase.crashlytics.gradle)
        classpath(libs.firebase.appdistribution.gradle)
        classpath(libs.dependency.analysis.gradle.plugin)
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

plugins {
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.devtools.ksp) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.arturbosch.detekt) apply false
    id("com.autonomousapps.dependency-analysis") version "2.12.0"
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory.get().asFile)
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = "17"
}

tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = "17"
}

dependencyAnalysis {
    structure {
        ignoreKtx(true) // default is false
        bundle("kotlin-stdlib") {
            includeGroup("org.jetbrains.kotlin")
        }
    }

}
