package com.hutudev.calendar.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hutudev.calendar.ui.components.*
import kotlin.math.abs

enum class SwipeDirection { LEFT, RIGHT, UP, DOWN, NONE }

@Composable
fun CalendarScreen(viewModel: CalendarViewModel = viewModel()) {
    val currentYearMonth by viewModel.currentYearMonth.collectAsState()
    val monthData by viewModel.monthData.collectAsState()
    val selectedDay by viewModel.selectedDay.collectAsState()

    var swipeDir by remember { mutableStateOf(SwipeDirection.NONE) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        CalendarHeader(
            year = currentYearMonth.first,
            month = currentYearMonth.second,
            onPrevClick = { swipeDir = SwipeDirection.RIGHT; viewModel.shiftMonth(-1) },
            onNextClick = { swipeDir = SwipeDirection.LEFT; viewModel.shiftMonth(1) },
            onTodayClick = { swipeDir = SwipeDirection.NONE; viewModel.goToToday() },
            onYearMonthSet = { y, m -> swipeDir = SwipeDirection.NONE; viewModel.jumpTo(y, m) }
        )

        HorizontalDivider(color = MaterialTheme.colorScheme.surface, thickness = 1.dp)

        WeekHeader()

        monthData?.let { data ->
            AnimatedContent(
                targetState = data,
                transitionSpec = {
                    val duration = 300
                    when (swipeDir) {
                        SwipeDirection.LEFT -> slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(duration)) togetherWith slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(duration))
                        SwipeDirection.RIGHT -> slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(duration)) togetherWith slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(duration))
                        SwipeDirection.UP -> slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(duration)) togetherWith slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(duration))
                        SwipeDirection.DOWN -> slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(duration)) togetherWith slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(duration))
                        else -> fadeIn(animationSpec = tween(duration)) togetherWith fadeOut(animationSpec = tween(duration))
                    }
                },
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) { targetData ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(targetData.year to targetData.month) {
                            var dragX = 0f
                            var dragY = 0f
                            detectDragGestures(
                                onDragEnd = {
                                    val threshold = 100f
                                    if (abs(dragX) > abs(dragY)) {
                                        // 横向手势判定 -> 月份
                                        if (dragX > threshold) {
                                            swipeDir = SwipeDirection.RIGHT
                                            viewModel.shiftMonth(-1)
                                        } else if (dragX < -threshold) {
                                            swipeDir = SwipeDirection.LEFT
                                            viewModel.shiftMonth(1)
                                        }
                                    } else {
                                        // 纵向手势判定 -> 年份
                                        if (dragY > threshold) {
                                            swipeDir = SwipeDirection.DOWN
                                            viewModel.shiftYear(-1)
                                        } else if (dragY < -threshold) {
                                            swipeDir = SwipeDirection.UP
                                            viewModel.shiftYear(1)
                                        }
                                    }
                                    dragX = 0f
                                    dragY = 0f
                                }
                            ) { change, dragAmount ->
                                change.consume()
                                dragX += dragAmount.x
                                dragY += dragAmount.y
                            }
                        }
                ) {
                    CalendarGrid(
                        monthData = targetData,
                        selectedDay = selectedDay,
                        onDayClick = { viewModel.selectDay(it) }
                    )
                }
            }
        } ?: Box(modifier = Modifier.weight(1f).fillMaxWidth())

        HorizontalDivider(color = MaterialTheme.colorScheme.surface, thickness = 1.dp)

        selectedDay?.let { day ->
            DetailPanel(day = day)
        }
    }
}
