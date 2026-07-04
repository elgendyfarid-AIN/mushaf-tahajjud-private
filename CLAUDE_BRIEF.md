# تقرير استئناف المشروع — مصحف التهجد والقيام
## 📋 للـ Claude الجديد: اقرأ هذا الملف كاملاً قبل أي رد

هذا الملف يحتوي على كل ما تحتاجه لمتابعة المشروع بدون أي شرح إضافي من صاحبه.

---

## 1. بيئة العمل والقيود الصارمة

| الأداة | الوضع | التفاصيل |
|---|---|---|
| **Samsung Note10+** | بيئة التطوير الرئيسية | الكود يُكتب هنا يدوياً (AndroidIDE أو أي محرر نصوص) |
| **كمبيوتر العمل** | للـ GitHub فقط | ممنوع تثبيت برامج — يُستخدم فقط لرفع الملفات بالسحب والإفلات على github.com وتحميل APK |
| **GitHub Actions** | محرك البناء الوحيد | يبني المشروع تلقائيًا عند كل push على فرع `main` |
| **Android Studio** | غير متاح | لا على الهاتف ولا على الكمبيوتر |
| **تشغيل Gradle محلياً** | غير مطلوب | GitHub Actions يتكفل بكل شيء |

**خلاصة ثابتة:** كل اقتراح تقني لازم يناسب هذه البيئة تحديداً. أي ملف يُقترح، المستخدم يكتبه يدوياً على الهاتف أو يحمّله من كود تولّده أنت.

**لغة التواصل:** صاحب المشروع بيتكلم بالعامية المصرية وبيخلط عربي وإنجليزي بشكل طبيعي.

---

## 2. فكرة التطبيق

**الاسم:** مصحف التهجد والقيام

**المنصة:** Android فقط، يُوزَّع كـ APK مباشرة (بدون Google Play).

**الفكرة المحورية:** مصحفان في تطبيق واحد:
1. **المصحف العادي**: 604 صفحة مطابقة لمصحف المدينة المنورة.
2. **مصحف التهجد**: 240 صفحة — كل صفحة = ربع حزب واحد — مصمم لتسهيل القراءة في صلاة القيام.

**المستوى المستهدف:** مماثل لتطبيق "آيات" من حيث الميزات (تلاوة، تفسير، ترجمة، حفظ، بحث، علامات، ثيمات).

---

## 3. القرارات التقنية الثابتة (لا تُغيَّر إلا باتفاق صريح)

| القرار | القيمة | السبب |
|---|---|---|
| لغة البرمجة | Kotlin | وحيدة مع Android ومدعوم كامل |
| واجهة المستخدم | Jetpack Compose | ملف .kt واحد = واجهة + منطق — أسهل في التحرير اليدوي من الهاتف |
| Android Gradle Plugin | **8.13.0** (ليس 9.x) | AGP 9 فيه breaking changes تجعل التشخيص عبر CI فقط صعباً جداً |
| Kotlin version | 2.2.20 | آخر stable |
| Compose BOM | 2026.06.00 | آخر stable |
| JDK | 17 (Temurin) | المتطلب الرسمي لـ AGP 8+ |
| Gradle | 8.14.5 | مطلوب من AGP 8.13 |
| minSdk | 26 (Android 8.0) | يغطي Note10+ وأغلب الأجهزة الحديثة |
| compileSdk / targetSdk | 35 | آخر stable |
| applicationId | `com.mushaf.tahajjud` | ثابت، لا يُغيَّر |
| Signing | debug.keystore ثابت داخل الريبو | يضمن تحديث APK على الهاتف بدون مسح التطبيق القديم |
| البناء | `assembleDebug` عبر GitHub Actions | توليد APK مباشرة قابل للتثبيت |

---

## 4. هيكل الملفات الكامل (المرحلة 0)

```
mushaf-tahajjud/
├── .github/
│   └── workflows/
│       └── build-apk.yml          ← GitHub Actions (يبني APK عند كل push)
├── .gitignore
├── README.md                       ← شرح الخطوات للمستخدم بالعربي
├── CLAUDE_BRIEF.md                 ← هذا الملف (تقرير الاستئناف)
├── PHASES.md                       ← خطة المراحل التفصيلية
├── build.gradle.kts                ← الجذر: تعريف plugins فقط
├── settings.gradle.kts             ← اسم المشروع + include :app
├── gradle.properties               ← JVM args + AndroidX flags
└── app/
    ├── build.gradle.kts            ← dependencies + signingConfig + compose
    ├── debug.keystore              ← مفتاح التوقيع الثابت (مقصود رفعه)
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/com/mushaf/tahajjud/
        │   └── MainActivity.kt     ← كل الكود (Compose)
        └── res/
            ├── drawable/
            │   └── ic_launcher_foreground.xml  ← أيقونة هلال vector
            ├── mipmap-anydpi-v26/
            │   └── ic_launcher.xml             ← adaptive icon
            └── values/
                ├── colors.xml      ← #0E5C4A (خضر داكن خلفية أيقونة)
                ├── strings.xml     ← app_name: مصحف التهجد والقيام
                └── themes.xml      ← Material Light NoActionBar
```

---

## 5. محتوى ملفات الـ Gradle الأساسية (للرجوع إليها)

### `settings.gradle.kts`
```kotlin
pluginManagement {
    repositories { google(); mavenCentral(); gradlePluginPortal() }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories { google(); mavenCentral() }
}
rootProject.name = "mushaf-tahajjud"
include(":app")
```

### `build.gradle.kts` (الجذر)
```kotlin
plugins {
    id("com.android.application") version "8.13.0" apply false
    id("org.jetbrains.kotlin.android") version "2.2.20" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.20" apply false
}
```

### `app/build.gradle.kts`
```kotlin
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
    signingConfigs {
        getByName("debug") {
            storeFile = file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }
    buildTypes {
        release { isMinifyEnabled = false; signingConfig = signingConfigs.getByName("debug") }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true }
}
dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2026.06.00")
    implementation(composeBom)
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
```

### `.github/workflows/build-apk.yml`
```yaml
name: Build APK
on:
  push:
    branches: ["main"]
  workflow_dispatch: {}
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { distribution: temurin, java-version: "17" }
      - uses: gradle/actions/setup-gradle@v4
        with: { gradle-version: "8.14.5" }
      - run: gradle assembleDebug --no-daemon --stacktrace
      - uses: actions/upload-artifact@v4
        with:
          name: mushaf-tahajjud-debug-apk
          path: app/build/outputs/apk/debug/*.apk
```

---

## 6. المصادر المعتمدة لبيانات القرآن (للمراحل القادمة)

| المصدر | الاستخدام | الرابط |
|---|---|---|
| **QUL — Quranic Universal Library** | بيانات تخطيط الصفحات (سطر بسطر لـ 604 صفحة)، خطوط QCF، بيانات الأرباع والأحزاب، توقيتات الصوت للمزامنة | qul.tarteel.ai |
| **Tanzil** | نص عثماني موثّق + ترجمات بعدة لغات | tanzil.net |
| **EveryAyah** | ملفات صوتية لكل آية بعدة قراء | everyayah.com |
| **Tafsir API (spa5k)** | تفاسير مجانية: ابن كثير، السعدي، الجلالين، الميسر (JSON) | github.com/spa5k/tafsir_api |
| **QPC Fonts** | خطوط القرآن الكريم الرسمية من مجمع الملك فهد | عبر QUL |

**ملاحظة:** جميع هذه المصادر مجانية للتطبيقات الإسلامية غير التجارية. عند دمجها في الكود، يجب عدم تعديل النص القرآني أو نسبه لغير مصدره.

---

## 7. الحالة الراهنة للمشروع

### ✅ المرحلة 0 — مُسلَّمة
- **الملفات:** كاملة ومرفوعة في المستودع.
- **حالة البناء:** يُفترض أن يعطي GitHub Actions علامة ✔ خضراء (تُأكَّد بعد أول push).
- **ما يعمل فعلاً:** شريط علوي، زر تبديل بين المصحفين، تنقّل أفقي (HorizontalPager) بعدّاد صفحات حقيقي (604/240)، RTL كامل، أيقونة، APK قابل للتثبيت.
- **ما ليس فيه بعد:** نص القرآن الحقيقي، صوت، تفسير، بحث، علامات، سمات — كل ذلك في المراحل القادمة.

### ⏳ المرحلة التالية: 1
انظر `PHASES.md` للتفاصيل الكاملة لكل مرحلة.

---

## 8. ملاحظات تقنية مهمة يجب أن يعرفها الـ Claude الجديد

1. **لا Gradle Wrapper في المشروع** — عمداً. بدلاً منه، GitHub Actions تثبّت Gradle مباشرة عبر إجراء `gradle/actions/setup-gradle`. لا تضيف ملفات `gradlew` أو `gradle/wrapper`.

2. **debug.keystore مرفوع في المستودع** — عمداً. هو مفتاح تجريبي فقط، غرضه الوحيد ضمان تحديث APK بدون مسح التطبيق. لا تضفه لـ .gitignore.

3. **لا AGP 9.x** — تم اختيار 8.13 عمداً. لا تقترح الترقية إلا بعد إتمام المشروع بشكل كامل.

4. **Compose Compiler Plugin** — ابتداءً من Kotlin 2.0، لم يعد موجوداً كإعداد منفصل (`kotlinCompilerExtensionVersion`). هو الآن Plugin مستقل مرتبط بنفس إصدار Kotlin (`org.jetbrains.kotlin.plugin.compose`). هذا الإعداد موجود في الملفات الحالية بشكل صحيح.

5. **التحرير اليدوي على الهاتف** — أي ملف كود تولّده لازم يكون:
   - بسيط وواضح (تعليقات بالعربي مساعدة)
   - بدون فراغات غير ضرورية أو رموز خاصة قد يعيد المحرر تنسيقها
   - بدون اعتماد على مكتبات تتطلب إعداداً إضافياً معقداً

6. **خط العمل الكامل:**
   ```
   Claude يولّد الكود → المستخدم يكتبه/يعدّله على الهاتف →
   يرفع على GitHub (سحب وإفلات من كمبيوتر العمل) →
   GitHub Actions يبني → المستخدم يحمّل APK → يثبّته على الهاتف →
   يرجع لـ Claude بنتيجة التجربة
   ```
