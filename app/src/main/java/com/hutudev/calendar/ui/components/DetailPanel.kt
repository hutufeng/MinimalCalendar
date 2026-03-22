package com.hutudev.calendar.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hutudev.calendar.data.CalendarDay
import com.hutudev.calendar.ui.theme.HolidayRed
import com.hutudev.calendar.ui.theme.WorkdayGreen

@Composable
fun DetailPanel(day: CalendarDay) {
    // 引入浮白阴影立体卡片，拉升原本纯文本堆积的扁平化，提升美观度
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // 第一行：由原来的纯文本变成带距离今天天数标记的同行左右排布
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                val weekDayStr = listOf("日", "一", "二", "三", "四", "五", "六")[day.date.dayOfWeek.value % 7]
                Text(
                    text = "${day.date.year}年${day.date.monthValue}月${day.date.dayOfMonth}日  星期$weekDayStr",
                    style = MaterialTheme.typography.titleLarge
                )
                
                // 实时计算距离今天相差的天数
                val diffDays = java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), day.date)
                val diffText = when {
                    diffDays == 0L -> "今天"
                    diffDays > 0L -> "距今 $diffDays 天"
                    else -> "已过 ${-diffDays} 天"
                }

                Text(
                    text = diffText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            val animalAndYear = "农历 ${day.lunarYearGanZhi}年【${day.lunarAnimal}年】"
            val lunarDate = "${day.lunarMonth}${day.lunarDay}"
            Text(
                text = "$animalAndYear $lunarDate",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                if (!day.festival.isNullOrBlank()) {
                    Text(text = "节日: ${day.festival}", color = HolidayRed)
                }
                if (!day.solarTerm.isNullOrBlank()) {
                    Text(text = "节气: ${day.solarTerm}", color = MaterialTheme.colorScheme.onBackground)
                }
                if (!day.holidayName.isNullOrBlank()) {
                    val color = if (day.isStatutoryHoliday) HolidayRed else WorkdayGreen
                    val text = if (day.isStatutoryHoliday) "${day.holidayName} (放假)" else "${day.holidayName}调休 (上班)"
                    Text(text = text, color = color, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "宜", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = WorkdayGreen)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (day.yi.isEmpty()) "无" else day.yi.joinToString(" "),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "忌", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = HolidayRed)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (day.ji.isEmpty()) "无" else day.ji.joinToString(" "),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}
