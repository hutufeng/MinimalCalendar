package com.hutudev.calendar.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

private val Context.dataStore by preferencesDataStore(name = "holiday_cache")

class CalendarRepository(private val context: Context) {

    private val apiService = HolidayApiService.create()
    private val gson = Gson()

    suspend fun getMonthData(year: Int, month: Int): MonthData = withContext(Dispatchers.Default) {
        val baseMonthData = CalendarEngine.generateMonthData(year, month)
        
        val holidayList = getHolidaysForYear(year)
        val holidayMap = holidayList.associateBy { it.date }
        
        if (holidayMap.isEmpty()) return@withContext baseMonthData
        
        val mergedDays = baseMonthData.days.map { day ->
            // GitHub json 直接是 YYYY-MM-DD 格式
            val dateStr = String.format("%04d-%02d-%02d", day.date.year, day.date.monthValue, day.date.dayOfMonth)
            val holidayDetail = holidayMap[dateStr]
            
            if (holidayDetail != null) {
                day.copy(
                    isStatutoryHoliday = holidayDetail.isOffDay,
                    isMakeupWorkday = !holidayDetail.isOffDay,
                    holidayName = holidayDetail.name
                )
            } else {
                day
            }
        }
        
        MonthData(year, month, mergedDays)
    }

    private suspend fun getHolidaysForYear(year: Int): List<HolidayDetail> {
        val cacheKey = stringPreferencesKey("holidays_list_$year")
        val cachedJson = context.dataStore.data.first()[cacheKey]
        
        if (!cachedJson.isNullOrEmpty()) {
            try {
                val type = object : TypeToken<List<HolidayDetail>>() {}.type
                return gson.fromJson(cachedJson, type) ?: emptyList()
            } catch (e: Exception) {}
        }
        
        return withContext(Dispatchers.IO) {
            // 多路节点容灾配置表，依次循环探测直到连通
            val fallbackUrls = listOf(
                "https://fastly.jsdelivr.net/gh/NateScarlet/holiday-cn@master/$year.json",
                "https://cdn.jsdelivr.net/gh/NateScarlet/holiday-cn@master/$year.json",
                "https://mirror.ghproxy.com/https://raw.githubusercontent.com/NateScarlet/holiday-cn/master/$year.json",
                "https://raw.githubusercontent.com/NateScarlet/holiday-cn/master/$year.json"
            )

            for (url in fallbackUrls) {
                try {
                    val response = apiService.getHolidaysByUrl(url)
                    if (response.days != null) {
                        val list = response.days
                        context.dataStore.edit { prefs ->
                            prefs[cacheKey] = gson.toJson(list)
                        }
                        return@withContext list
                    }
                } catch (e: Exception) {
                    // 当前节点异常/封锁，吃掉崩溃异常并自动无感切换至下一个节点
                    continue
                }
            }
            
            // 全节点阵亡，静默放弃，依靠离线纯历法接管天下
            emptyList()
        }
    }
}
