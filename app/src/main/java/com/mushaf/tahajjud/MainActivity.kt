package com.mushaf.tahajjud

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

/**
 * المرحلة 0: هيكل تأسيسي فقط.
 * الهدف هنا إثبات أن خط الإنتاج كامل يعمل: كتابة كود -> رفع على GitHub
 * -> بناء عبر GitHub Actions -> تثبيت APK على الهاتف.
 * نص القرآن الفعلي بالرسم العثماني، والتفاسير، والصوتيات، والحفظ...
 * كل ده هيُضاف تدريجيًا في المراحل القادمة فوق هذا الأساس.
 */
enum class MushafMode(val label: String, val pageCount: Int) {
    STANDARD("المصحف العادي", 604),
    TAHAJJUD("مصحف التهجد", 240),
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                MaterialTheme(colorScheme = lightColorScheme()) {
                    MushafApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MushafApp() {
    var mode by remember { mutableStateOf(MushafMode.STANDARD) }
    val pagerState = rememberPagerState(pageCount = { mode.pageCount })
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("مصحف التهجد والقيام") })
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // زر التبديل بين المصحف العادي ومصحف التهجد
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                MushafMode.entries.forEachIndexed { index, m ->
                    SegmentedButton(
                        selected = mode == m,
                        onClick = {
                            mode = m
                            scope.launch { pagerState.scrollToPage(0) }
                        },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = MushafMode.entries.size
                        )
                    ) {
                        Text(m.label)
                    }
                }
            }

            // التنقل الأفقي بين الصفحات (سحب يمين/يسار)
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) { page ->
                Box(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "صفحة ${page + 1}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "من ${mode.pageCount} — ${mode.label}")
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "هنا سيُعرض نص الصفحة بالرسم العثماني\n(في المرحلة القادمة)",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Text(
                text = "الصفحة ${pagerState.currentPage + 1} / ${mode.pageCount}",
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}
