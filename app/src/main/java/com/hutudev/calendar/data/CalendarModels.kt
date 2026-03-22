package com.hutudev.calendar.data

import java.time.LocalDate

data class CalendarDay(
    val date: LocalDate,
    val lunarMonth: String,
    val lunarDay: String,
    val lunarYearGanZhi: String,
    val lunarAnimal: String,
    val solarTerm: String?,
    val festival: String?,
    val isWeekend: Boolean,
    val isCurrentMonth: Boolean,
    val isToday: Boolean = false,
    
    // 传统黄历宜、忌
    val yi: List<String>,
    val ji: List<String>,
    
    // 法定节假日/调休标记 (后续由 API 填充)
    val isStatutoryHoliday: Boolean = false,
    val isMakeupWorkday: Boolean = false,
    val holidayName: String? = null
)

data class MonthData(
    val year: Int,
    val month: Int,
    val days: List<CalendarDay>
)
