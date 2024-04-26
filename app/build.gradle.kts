plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.econok.economykanban"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.econok.economykanban"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment:2.7.6")
    implementation("androidx.navigation:navigation-ui:2.7.6")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.activity:activity:1.8.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.transition:transition:1.4.1")
    implementation("net.cachapa.expandablelayout:expandablelayout:2.9.2")

    //FIREBASE
    implementation(platform("com.google.firebase:firebase-bom:32.8.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.android.gms:play-services-auth:20.3.0")
    implementation("com.github.yalantis:ucrop:2.2.8-native")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    implementation("com.google.firebase:firebase-appcheck:17.0.0")
    implementation("com.google.firebase:firebase-appcheck-playintegrity")
    implementation("androidx.transition:transition:1.4.1")
    implementation("net.cachapa.expandablelayout:expandablelayout:2.9.2")

    //Bumptech
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    //Yalantis
    implementation("com.github.yalantis:ucrop:2.2.8-native")

    //System UI Controller - Accompanist
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.28.0")
}