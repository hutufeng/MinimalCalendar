package com.hutudev.calendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hutudev.calendar.ui.CalendarScreen
import com.hutudev.calendar.ui.theme.MinimalCalendarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MinimalCalendarTheme {
                CalendarScreen()
            }
        }
    }
}
