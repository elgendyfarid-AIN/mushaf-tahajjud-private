plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.mushaf.tahajjud"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mushaf.tahajjud"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0-phase0"
    }

    // مفتاح توقيع ثابت ومُرفَق داخل المستودع (app/debug.keystore).
    // الهدف: كل نسخة APK تخرج من GitHub Actions تكون موقّعة بنفس المفتاح،
    // عشان تقدر تحدّث التطبيق على هاتفك من نسخة لنسخة بدون رسالة
    // "App not installed" أو الحاجة لمسح التطبيق القديم أولاً.
    signingConfigs {
        getByName("debug") {
            storeFile = file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            // مؤقتًا بنوقع release بنفس مفتاح debug، لأن التطبيق لسه في طور
            // التجربة الشخصية. لاحقًا لو حبيت توزيع رسمي، نعمل مفتاح خاص
            // ونخزنه كـ GitHub Secret بدل ما يكون داخل المستودع.
            signingConfig = signingConfigs.getByName("debug")
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
        compose = true
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2026.06.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.activity:activity-compose:1.10.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.foundation:foundation")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
