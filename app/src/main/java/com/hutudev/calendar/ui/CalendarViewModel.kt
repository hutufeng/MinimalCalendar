package com.hutudev.calendar.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hutudev.calendar.data.CalendarDay
import com.hutudev.calendar.data.CalendarRepository
import com.hutudev.calendar.data.MonthData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class CalendarViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CalendarRepository(application)
    private val monthCache = mutableMapOf<String, MonthData>()

    private val _monthData = MutableStateFlow<MonthData?>(null)
    val monthData: StateFlow<MonthData?> = _monthData.asStateFlow()

    private val _currentYearMonth = MutableStateFlow(Pair(LocalDate.now().year, LocalDate.now().monthValue))
    val currentYearMonth = _currentYearMonth.asStateFlow()

    private val _selectedDay = MutableStateFlow<CalendarDay?>(null)
    val selectedDay: StateFlow<CalendarDay?> = _selectedDay.asStateFlow()

    init {
        goToToday()
    }

    fun selectDay(day: CalendarDay) {
        _selectedDay.value = day
    }

    fun goToToday() {
        val today = LocalDate.now()
        _currentYearMonth.value = today.year to today.monthValue
        loadData(today.year, today.monthValue, selectToday = true)
    }

    fun jumpTo(year: Int, month: Int) {
        _currentYearMonth.value = year to month
        loadData(year, month)
    }

    fun shiftMonth(offset: Int) {
        var (y, m) = _currentYearMonth.value
        m += offset
        while (m > 12) { m -= 12; y++ }
        while (m < 1) { m += 12; y-- }
        jumpTo(y, m)
    }

    fun shiftYear(offset: Int) {
        val (y, m) = _currentYearMonth.value
        jumpTo(y + offset, m)
    }

    private fun loadData(year: Int, month: Int, selectToday: Boolean = false) {
        // 瞬间同步计算基础矩阵，解决启动与切换时由于读取缓存/网络导致的短暂白屏
        val basicData = com.hutudev.calendar.data.CalendarEngine.generateMonthData(year, month)
        _monthData.value = basicData
        
        viewModelScope.launch {
            val key = "$year-$month"
            val data = if (monthCache.containsKey(key)) {
                monthCache[key]!!
            } else {
                val fetched = repository.getMonthData(year, month)
                monthCache[key] = fetched
                fetched
            }
            _monthData.value = data

            if (_selectedDay.value == null || selectToday) {
                val today = LocalDate.now()
                if (year == today.year && month == today.monthValue) {
                    _selectedDay.value = data.days.find { it.date == today }
                } else if (!selectToday && _selectedDay.value == null) {
                    _selectedDay.value = data.days.find { it.isCurrentMonth && it.date.dayOfMonth == 1 }
                }
            }
        }
    }
}
