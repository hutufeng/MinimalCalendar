package com.hutudev.calendar.data

import com.nlf.calendar.Solar
import java.time.LocalDate
import java.time.YearMonth

object CalendarEngine {
    
    // 生成日历 7x6 矩阵数据，周日为每周第一天
    fun generateMonthData(year: Int, month: Int): MonthData {
        val yearMonth = YearMonth.of(year, month)
        val firstDayOfMonth = yearMonth.atDay(1)
        
        // LocalDate.dayOfWeek.value: 1 (Mon) .. 7 (Sun)
        val firstDayOfWeekValue = firstDayOfMonth.dayOfWeek.value 
        // 星期一作为第一天，直接取星期序号减 1 即是前导空格天数
        val daysToPrepend = firstDayOfWeekValue - 1
        val startDate = firstDayOfMonth.minusDays(daysToPrepend.toLong())
        
        val daysList = mutableListOf<CalendarDay>()
        var currentDate = startDate
        val today = LocalDate.now()
        
        // 固定生成 42 天 (6 周)
        for (i in 0 until 42) {
            daysList.add(buildCalendarDay(currentDate, month, today))
            currentDate = currentDate.plusDays(1)
        }
        
        return MonthData(year, month, daysList)
    }

    private fun buildCalendarDay(date: LocalDate, currentViewMonth: Int, today: LocalDate): CalendarDay {
        // 利用 lunar-java 高效实现公历转农历/黄历
        val solar = Solar.fromYmd(date.year, date.monthValue, date.dayOfMonth)
        val lunar = solar.lunar
        
        val solarTerm = lunar.jieQi // 可能是空字符串
        
        val festivals = mutableListOf<String>()
        festivals.addAll(lunar.festivals)
        festivals.addAll(solar.festivals)
        val primaryFestival = festivals.firstOrNull()
        
        return CalendarDay(
            date = date,
            lunarMonth = lunar.monthInChinese + "月",
            lunarDay = lunar.dayInChinese,
            lunarYearGanZhi = lunar.yearInGanZhi,
            lunarAnimal = lunar.yearShengXiao,
            solarTerm = if (solarTerm.isNullOrBlank()) null else solarTerm,
            festival = primaryFestival,
            isWeekend = date.dayOfWeek.value == 6 || date.dayOfWeek.value == 7,
            isCurrentMonth = date.monthValue == currentViewMonth,
            isToday = date == today,
            yi = lunar.dayYi,
            ji = lunar.dayJi
        )
    }
}
