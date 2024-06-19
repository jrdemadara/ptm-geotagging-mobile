plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.jrdemadara.ptm_geotagging"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.jrdemadara.ptm_geotagging"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        create("customDebugType") {
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity:1.8.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("pl.droidsonroids.gif:android-gif-drawable:1.2.25")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation (platform("com.squareup.okhttp3:okhttp-bom:5.0.0-alpha.7"))
    implementation ("com.squareup.okhttp3:okhttp:5.0.0-alpha.7")
    implementation ("com.squareup.okhttp3:logging-interceptor")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okio:okio:3.1.0")
    implementation ("androidx.recyclerview:recyclerview:1.3.2")
    implementation ("androidx.cardview:cardview:1.0.0")

    implementation ("com.google.android.gms:play-services-location:21.2.0")

    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

    implementation ("com.github.KhairoHumsi:Printer-ktx:1.0.1")
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")
}