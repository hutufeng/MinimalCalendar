package com.hutudev.calendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hutudev.calendar.ui.CalendarScreen
import com.hutudev.calendar.ui.CalendarViewModel
import com.hutudev.calendar.ui.theme.MinimalCalendarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: CalendarViewModel = viewModel()
            val themeConfig by viewModel.themeConfig.collectAsState()
            
            MinimalCalendarTheme(themeConfig = themeConfig) {
                CalendarScreen(viewModel = viewModel)
            }
        }
    }
}
