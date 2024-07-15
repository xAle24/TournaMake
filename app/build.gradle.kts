plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.tournaMake"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.tournaMake"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    implementation ("com.google.android.material:material:1.12.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.3")
    implementation ("androidx.lifecycle:lifecycle-runtime-compose:2.8.3")
    // For saving preferences and data store
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    // For dependency injection (giving access to objects to classes that require it)
    implementation("io.insert-koin:koin-androidx-compose:3.5.3")
    // For managing image uris in a simple way
    implementation("io.coil-kt:coil-compose:2.6.0")
    // Used to convert LiveData to state
    implementation ("androidx.compose.runtime:runtime-livedata:1.6.8")
    //new library for charts
    implementation("io.github.dautovicharis:charts:1.3.1")
    // Library for compose tournaments
    // implementation("com.adammcneilly:composetournamentbracket:0.0.1")
    // to make tournament library work
    // https://mvnrepository.com/artifact/com.google.accompanist/accompanist-systemuicontroller
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.32.0")
    // Nav controller, still for the tournament dependency to work
    implementation("androidx.navigation:navigation-compose:2.7.7")
    // Pager, needed by the library, again
    // https://mvnrepository.com/artifact/androidx.compose.foundation/foundation
    //implementation("androidx.compose.foundation:foundation:1.6.8")
    //implementation("androidx.compose.foundation:foundation:1.4.3")
}