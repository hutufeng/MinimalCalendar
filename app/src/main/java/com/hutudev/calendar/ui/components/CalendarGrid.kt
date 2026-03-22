package com.hutudev.calendar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hutudev.calendar.data.CalendarDay
import com.hutudev.calendar.data.MonthData
import com.hutudev.calendar.ui.theme.HolidayRed
import com.hutudev.calendar.ui.theme.WorkdayGreen

@Composable
fun CalendarGrid(
    monthData: MonthData,
    selectedDay: CalendarDay?,
    onDayClick: (CalendarDay) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        val weeks = monthData.days.chunked(7)
        weeks.forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { day ->
                    DayCell(
                        day = day,
                        isSelected = day.date == selectedDay?.date,
                        onClick = { onDayClick(day) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun DayCell(
    day: CalendarDay,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCurrentMonth = day.isCurrentMonth
    val alpha = if (isCurrentMonth) 1f else 0.3f
    
    val bgColor = if (isSelected) MaterialTheme.colorScheme.onBackground.copy(alpha = alpha) else Color.Transparent
    
    val txtColor = if (isSelected) {
        MaterialTheme.colorScheme.background
    } else if (day.isWeekend) {
        HolidayRed.copy(alpha = 0.8f)
    } else {
        MaterialTheme.colorScheme.onBackground
    }
    
    // 如果是今天但没有被选中，加入淡淡的弱边框作高级感标识
    val borderModifier = if (day.isToday && !isSelected) {
        Modifier.border(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha=0.2f), RoundedCornerShape(8.dp))
    } else {
        Modifier
    }
    
    val lunarText = day.festival ?: day.solarTerm ?: day.lunarDay
    val badgeText = if (day.isStatutoryHoliday) "休" else if (day.isMakeupWorkday) "班" else ""
    val badgeColor = if (day.isStatutoryHoliday) HolidayRed else WorkdayGreen

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clip(RoundedCornerShape(8.dp))
            .then(borderModifier)
            .background(bgColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = day.date.dayOfMonth.toString(),
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                color = txtColor.copy(alpha = alpha)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = lunarText,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = if (day.festival != null && !isSelected) HolidayRed.copy(alpha = alpha) else txtColor.copy(alpha = alpha),
                maxLines = 1
            )
        }
        
        if (badgeText.isNotEmpty()) {
            Text(
                text = badgeText,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold),
                color = if (isSelected) txtColor else badgeColor.copy(alpha = alpha),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 4.dp, end = 4.dp)
            )
        }
    }
}
