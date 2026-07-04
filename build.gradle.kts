// هذا الملف الجذري لا يحتوي كود بناء فعلي، فقط يعرّف نسخ الأدوات (plugins)
// المستخدمة في موديول app، بدون تطبيقها هنا (apply false).

plugins {
    id("com.android.application") version "8.13.0" apply false
    id("org.jetbrains.kotlin.android") version "2.2.20" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.20" apply false
}
