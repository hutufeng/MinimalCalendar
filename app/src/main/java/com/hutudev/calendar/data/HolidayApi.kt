package com.hutudev.calendar.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url

data class HolidayYearResponse(
    val year: Int,
    val days: List<HolidayDetail>?
)

data class HolidayDetail(
    val name: String,
    val date: String,
    val isOffDay: Boolean
)

interface HolidayApiService {
    // 支持动态传入绝对路径以实现多节点备用容灾
    @GET
    suspend fun getHolidaysByUrl(@Url url: String): HolidayYearResponse

    companion object {
        fun create(): HolidayApiService {
            return Retrofit.Builder()
                // 当通过 @Url 传入完整绝对路径时，baseUrl 仅作为占位符
                .baseUrl("https://localhost/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(HolidayApiService::class.java)
        }
    }
}
