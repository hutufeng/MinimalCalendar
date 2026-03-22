package com.hutudev.calendar.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hutudev.calendar.ui.theme.ThemeConfig

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CalendarHeader(
    year: Int,
    month: Int,
    themeConfig: ThemeConfig,
    onThemeToggle: () -> Unit,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
    onTodayClick: () -> Unit,
    onYearMonthSet: (Int, Int) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        YearMonthInputDialog(
            currentYear = year,
            currentMonth = month,
            onDismiss = { showDialog = false },
            onConfirm = { y, m ->
                showDialog = false
                onYearMonthSet(y, m)
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 加入极具动感的向上/向下翻滚切换文字动画
        AnimatedContent(
            targetState = "${year}年${month}月",
            transitionSpec = {
                (slideInVertically { height -> -height } + fadeIn()).togetherWith(
                    slideOutVertically { height -> height } + fadeOut())
            },
            label = "header_anim"
        ) { targetText ->
            Text(
                text = targetText,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .clickable { showDialog = true }
                    .padding(4.dp)
            )
        }
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val themeIcon = when (themeConfig) {
                ThemeConfig.SYSTEM -> "⚙️"
                ThemeConfig.LIGHT -> "🌞"
                ThemeConfig.DARK -> "🌙"
            }
            Text(
                text = themeIcon,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.clickable { onThemeToggle() }.padding(4.dp)
            )

            Text(
                text = "回到今天",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.clickable { onTodayClick() }.padding(4.dp)
            )
             
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "<",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.clickable { onPrevClick() }.padding(4.dp)
                )
                Text(
                    text = ">",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.clickable { onNextClick() }.padding(4.dp)
                )
             }
        }
    }
}

@Composable
fun YearMonthInputDialog(
    currentYear: Int,
    currentMonth: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    var yearTxt by remember { mutableStateOf(currentYear.toString()) }
    var monthTxt by remember { mutableStateOf(currentMonth.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("跳转到指定年月") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = yearTxt,
                    onValueChange = { yearTxt = it },
                    label = { Text("年份 (如: 2024)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = monthTxt,
                    onValueChange = { monthTxt = it },
                    label = { Text("月份 (1-12)") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val y = yearTxt.toIntOrNull() ?: currentYear
                val m = monthTxt.toIntOrNull()?.coerceIn(1, 12) ?: currentMonth
                onConfirm(y, m)
            }) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}
