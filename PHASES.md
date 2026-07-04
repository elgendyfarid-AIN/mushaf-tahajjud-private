# خطة المراحل التفصيلية — مصحف التهجد والقيام

> هذا الملف يُحدَّث في نهاية كل مرحلة. اقرأه مع `CLAUDE_BRIEF.md` قبل البدء
> في أي مرحلة جديدة.

---

## ✅ المرحلة 0 — الهيكل الأساسي وخط الإنتاج
**الحالة:** مكتملة ومُرفوعة

### ما تم بناؤه:
- هيكل مشروع Android كامل (Kotlin + Jetpack Compose)
- ملف `build-apk.yml` لبناء APK تلقائياً عبر GitHub Actions
- مفتاح توقيع ثابت (`debug.keystore`) لضمان تحديث APK بسلاسة
- زر تبديل بين المصحف العادي (604 صفحة) ومصحف التهجد (240 صفحة)
- تنقّل أفقي بين الصفحات (HorizontalPager) مع عدّاد صفحات
- دعم RTL كامل
- أيقونة تطبيق (هلال على خلفية خضراء داكنة)

### الهدف من هذه المرحلة:
التأكد من أن "الأنبوب" كامل يعمل قبل بناء الميزات الفعلية فوقه.

---

## ⏳ المرحلة 1 — نص القرآن الحقيقي والبنية الأساسية
**الحالة:** لم تبدأ بعد

### الهدف:
الانتقال من "أرقام وهمية" إلى تطبيق قرآني حقيقي فعلاً.

### الملفات التي ستُضاف / تُعدَّل:

#### 1. بيانات القرآن (مصدر: QUL وTanzil)
```
app/src/main/assets/
├── quran/
│   ├── quran_text.json      ← نص الآيات (عثماني): {surah, ayah, text, page}
│   ├── pages.json           ← تخطيط الصفحات: {page, surah, ayah_start, hizb, juz, rub}
│   ├── surahs.json          ← معلومات السور: {id, name_ar, verses_count, juz_start}
│   └── metadata.json        ← أرباع الأحزاب: {rub_id, page, juz, hizb, rub_in_hizb}
```

**كيفية الحصول على البيانات:**
- من `qul.tarteel.ai` — قسم "Quran Script" لبيانات النص
- من `qul.tarteel.ai` — قسم "Mushaf Layout" لتخطيط الصفحات
- الملفات ستُنزَّل مرة واحدة وتُضاف كـ assets ثابتة (تطبيق offline بالكامل)

#### 2. قاعدة البيانات المحلية
```
app/src/main/java/com/mushaf/tahajjud/
├── data/
│   ├── QuranDatabase.kt     ← Room database (local SQLite)
│   ├── QuranDao.kt          ← استعلامات: getPage, getSurah, searchAyah
│   └── QuranEntity.kt       ← نموذج البيانات
└── repository/
    └── QuranRepository.kt   ← يقرأ من Room (وليس من JSON مباشرة بعد أول تحميل)
```

**المكتبات الجديدة التي ستُضاف لـ `app/build.gradle.kts`:**
```kotlin
// Room (قاعدة بيانات محلية)
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
// KSP (معالج تعليقات Room)
id("com.google.devtools.ksp") version "2.2.20-2.0.1"
// Gson (قراءة JSON من assets)
implementation("com.google.code.gson:gson:2.11.0")
// DataStore (حفظ آخر صفحة)
implementation("androidx.datastore:datastore-preferences:1.1.1")
```

#### 3. الشاشات والملفات الجديدة
```
app/src/main/java/com/mushaf/tahajjud/
├── ui/
│   ├── MushafScreen.kt      ← شاشة المصحف مع HorizontalPager الحقيقي
│   ├── TahajjudScreen.kt    ← شاشة التهجد مع عرض رقم الربع/الحزب/الجزء
│   └── PageView.kt          ← Composable لعرض صفحة واحدة بنصها الحقيقي
├── viewmodel/
│   └── MushafViewModel.kt   ← يربط Repository بالواجهة + يحفظ آخر صفحة
└── navigation/
    └── AppNavigation.kt     ← Navigation Compose بين الشاشات
```

#### 4. الخطوط
```
app/src/main/assets/fonts/
├── UthmanicHafs_v22.ttf     ← الخط العثماني الأساسي (مصدر: QUL)
└── Amiri-Regular.ttf         ← خط احتياطي
```

### نتيجة المرحلة 1:
- تطبيق يعرض فعلاً نص القرآن بالرسم العثماني الصحيح
- 604 صفحة حقيقية للمصحف العادي
- 240 صفحة حقيقية لمصحف التهجد مع رقم الربع والحزب والجزء
- حفظ آخر صفحة بين الجلسات
- بحث بسيط بالسورة والجزء والحزب والربع

---

## ⏳ المرحلة 2 — التنقل والسمات والعلامات المرجعية
**الحالة:** لم تبدأ بعد

### الهدف:
إضافة كل ميزات القراءة والتخصيص (بدون صوت أو تفسير بعد).

### الملفات التي ستُضاف / تُعدَّل:

#### 1. التمرير العمودي (إضافةً للأفقي)
```kotlin
// في MushafScreen.kt
// VerticalPager من نفس مكتبة foundation.pager
// زر للتبديل بين وضع التمرير الأفقي والعمودي
```

#### 2. التصفح الذاتي (Auto-scroll)
```
app/src/main/java/com/mushaf/tahajjud/
└── service/
    └── AutoScrollService.kt  ← Foreground Service مع إشعار في شريط الحالة
```
- سرعات من 1 إلى 10 (تُحفَظ في DataStore)
- إشعار ثابت في شريط الحالة مع أزرار إيقاف/تسريع/تبطيء

#### 3. السمات الأربعة
```
app/src/main/java/com/mushaf/tahajjud/ui/theme/
├── ThemeManager.kt
└── Themes.kt
```
| السمة | اللون الأساسي | الخلفية |
|---|---|---|
| نهاري | #0E5C4A (أخضر داكن) | أبيض |
| ليلي | #1B4D3E | أسود |
| حرم | #8B6914 (ذهبي) | بيج داكن |
| تهجد | #1A237E (كحلي) | أزرق داكن جداً |

#### 4. تحكم الخطوط
- 3 خطوط على الأقل: Uthmanic Hafs، Uthmanic Warsh، Amiri
- تحكم بحجم الخط (11 مستوى: 12sp إلى 32sp)

#### 5. العلامات المرجعية
```
Room Table: bookmarks
├── id, page, surah, ayah
├── note (ملاحظة نصية)
├── category (enum: GENERAL, MEMORIZATION, REMINDER)
├── created_at
└── color (لون الفئة)
```
- قائمة العلامات كـ BottomSheet
- تصدير قائمة العلامات كملف نصي

#### 6. مؤشرات التقدم
```
Room Table: reading_progress
├── date, pages_read, juz, hizb, rub
└── total_pages_read (مُجمَّع)
```

### نتيجة المرحلة 2:
- تجربة قراءة كاملة متعددة الأوضاع
- تصفح ذاتي مع إشعار
- 4 سمات قابلة للتبديل
- علامات مرجعية مع ملاحظات
- مؤشر تقدم القراءة

---

## ⏳ المرحلة 3 — التلاوات الصوتية
**الحالة:** لم تبدأ بعد

### الهدف:
تشغيل تلاوات مع تلوين النص أثناء الصوت.

### هيكل البيانات:

#### بيانات القراء (من EveryAyah)
```
app/src/main/assets/reciters/
└── reciters.json   ← قائمة القراء مع معرفاتهم وجودة الصوت
```
الملفات الصوتية لن تكون مضمّنة في APK (حجمها ضخم). ستُحمَّل عند الطلب:
```
app/src/main/java/com/mushaf/tahajjud/
└── audio/
    ├── AudioDownloadManager.kt   ← تحميل ملفات MP3 لقارئ معين
    ├── AudioPlayer.kt            ← MediaPlayer wrapper مع callbacks
    └── TimingMapper.kt           ← ربط توقيتات الكلمات بنص الصفحة (من QUL)
```

#### التخزين المحلي للصوت
```
/Android/data/com.mushaf.tahajjud/files/audio/
└── {reciter_id}/{surah_number}/{ayah_number}.mp3
```

#### القراء المستهدفون (7 على الأقل):
1. مشاري راشد العفاسي
2. عبد الباسط عبد الصمد
3. محمود خليل الحصري
4. سعد الغامدي
5. عبد الرحمن السديس
6. أبو بكر الشاطري
7. محمد صديق المنشاوي

#### تلوين النص أثناء الصوت:
- من QUL: ملفات timing JSON تحدد بالمللي ثانية متى تبدأ كل آية
- التلوين بـ `AnnotatedString` في Compose (لون مميز للآية الجارية)

### نتيجة المرحلة 3:
- تحميل صوت لأي قارئ ولأي سورة
- تشغيل مستمر مع انتقال تلقائي بين الآيات والصفحات
- تلوين الآية الجارية أثناء التلاوة
- تحكم: تشغيل/إيقاف/التالي/السابق/تكرار آية

---

## ⏳ المرحلة 4 — التفاسير والترجمات
**الحالة:** لم تبدأ بعد

### الهدف:
إضافة 4 تفاسير وأكثر من 10 ترجمات offline.

### التفاسير:
| الاسم | المصدر | اللغة |
|---|---|---|
| تفسير ابن كثير | Tafsir API (spa5k) | العربية |
| تفسير السعدي | Tafsir API | العربية |
| تفسير الجلالين | Tafsir API | العربية |
| التفسير الميسر | Tafsir API | العربية |

### الترجمات:
```
app/src/main/assets/translations/
├── en_saheeh.json      ← الإنجليزية (Saheeh International)
├── en_pickthall.json   ← الإنجليزية (Pickthall)
├── fr_hamidullah.json  ← الفرنسية
├── ur_jalandhry.json   ← الأردية
├── tr_diyanet.json     ← التركية
├── id_indonesian.json  ← الإندونيسية
├── ru_kuliev.json      ← الروسية
├── de_bubenheim.json   ← الألمانية
├── es_navio.json       ← الإسبانية
├── ms_basmeih.json     ← الملايوية
├── bn_muhiuddin.json   ← البنغالية
└── zh_ma.json          ← الصينية
```
(الترجمات متاحة مجاناً من Tanzil وQuran.com)

### واجهة التفسير والترجمة:
- BottomSheet يظهر عند الضغط الطويل على آية
- تبويبات: التفاسير | الترجمة
- اختيار التفسير/الترجمة المطلوب من قائمة
- خيار عرض الترجمة تحت كل آية مباشرة (inline mode)

### نتيجة المرحلة 4:
- عرض تفسير أي آية بضغطة واحدة
- 12 ترجمة بلغات مختلفة
- كل شيء offline بالكامل

---

## ⏳ المرحلة 5 — الحفظ والمراجعة
**الحالة:** لم تبدأ بعد

### الهدف:
نظام حفظ القرآن مع خوارزمية التكرار المتباعد (Spaced Repetition).

### الميزات:

#### خطة الحفظ اليومية
- المستخدم يختار: كم صفحة / كم آيات / كم ربع في اليوم
- التطبيق يحسب الجدول ويرسل إشعار يومي

#### بطاقات المراجعة (Flashcards)
```
Room Table: memorization_cards
├── ayah_key (surah:ayah)
├── last_review_date
├── next_review_date     ← تُحسَب بخوارزمية SM-2
├── ease_factor          ← يتغير بناءً على أداء المستخدم
├── interval_days        ← المدة للمراجعة التالية
└── repetitions          ← عدد مرات المراجعة الناجحة
```

#### خوارزمية SM-2 (Spaced Repetition):
```kotlin
// في MemorizationEngine.kt
fun calculateNextReview(card: MemorizationCard, quality: Int): MemorizationCard {
    // quality: 0-5 (0=نسيان كامل، 5=حفظ مثالي)
    // يُحسَب ease_factor و interval_days الجديدَين
}
```

#### واجهة المراجعة:
- بطاقة تعرض بداية الآية → المستخدم يكمل → يصنّف نفسه (نسيت/تذكرت/مثالي)
- التطبيق يعرض بطاقات المراجعة المستحقة اليوم فقط
- إحصاءات: كم آية محفوظة، كم يوم متواصل، نسبة التذكّر

### نتيجة المرحلة 5:
- نظام حفظ يعمل بالمراجعة الذكية
- جدول مراجعة مخصص لكل مستخدم
- إحصاءات تقدم الحفظ

---

## 📌 القاعدة الثابتة لكل المراحل

في بداية كل مرحلة جديدة، الـ Claude الذي سيتولى المشروع يقرأ:
1. `CLAUDE_BRIEF.md` ← يفهم البيئة والقرارات الثابتة
2. `PHASES.md` ← هذا الملف، يفهم أين وصلنا وما التالي
3. آخر ملفات الكود المعدَّلة ← يكتب فوقها بشكل متوافق

**قاعدة العمل:** لا يُبنى ملف جديد إلا بعد التأكد من نجاح بناء المرحلة السابقة على GitHub Actions.
