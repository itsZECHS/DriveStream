import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.devtools.ksp")
}

val localProperties = gradleLocalProperties(rootDir, providers)

android {

    namespace = "zechs.drive.stream"

    packaging {
        resources {
            excludes += setOf("META-INF/DEPENDENCIES")
        }
    }

    compileSdk = 35

    defaultConfig {
        applicationId = "zechs.drive.stream"
        minSdk = 21
        targetSdk = 35
        versionCode = 12
        versionName = "1.4.0"
        tasks.withType<Jar> {
            archiveBaseName.set("$applicationId-$versionCode")
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        resValue("string", "ad_app_id", localProperties.getProperty("ad.appid"))
        buildConfigField(
            "String",
            "ad_home_banner",
            "\"${localProperties.getProperty("ad.home.banner")}\""
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    splits {
        abi {
            isEnable = true
            reset()
            include("x86", "x86_64", "armeabi-v7a", "arm64-v8a")
            isUniversalApk = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("extension-*.aar"))))
    implementation(project(":mpv"))

    val exoPlayerVersion = "2.19.1"
    val moshiVersion = "1.15.2"
    val retrofitVersion = "2.9.0"
    val roomVersion = "2.6.1"

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    // Analytics
    implementation("com.google.firebase:firebase-analytics-ktx")
    // Crashlytics
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    // Admob
    implementation("com.google.android.gms:play-services-ads:23.6.0")

    // Room
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // Gson
    implementation("com.google.code.gson:gson:2.11.0")

    // Moshi
    implementation("com.squareup.moshi:moshi:$moshiVersion")
    implementation("com.squareup.moshi:moshi-kotlin:$moshiVersion")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:$moshiVersion")

    // OkHttp
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.9.3"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofitVersion")

    // Exoplayer
    implementation("com.google.android.exoplayer:exoplayer-core:$exoPlayerVersion")
    implementation("com.google.android.exoplayer:exoplayer-dash:$exoPlayerVersion")
    implementation("com.google.android.exoplayer:exoplayer-ui:$exoPlayerVersion")

    // Datastore
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    ksp("com.github.bumptech.glide:ksp:4.14.2")
    implementation("com.github.bumptech.glide:okhttp3-integration:4.14.2")

    // Coroutine Lifecycle Scopes
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    // Dagger hilt
    implementation("com.google.dagger:hilt-android:2.54")
    ksp("com.google.dagger:hilt-compiler:2.54")

    // Splash
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Navigational Components
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.5")

    //Kotlin
    implementation("androidx.core:core-ktx:1.15.0")

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}