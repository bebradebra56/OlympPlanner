package com.olympplanner.app.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.olympplanner.app.domain.model.Category
import com.olympplanner.app.domain.model.Priority
import com.olympplanner.app.ui.theme.*

@Composable
fun Dp.toPx() = with(LocalDensity.current) { this@toPx.toPx() }

@Composable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }

fun Category.toColor() = when (this) {
    Category.WORK -> CategoryWork
    Category.PERSONAL -> CategoryPersonal
    Category.LEISURE -> CategoryLeisure
}

fun Priority.toColor() = when (this) {
    Priority.LOW -> PriorityLow
    Priority.MEDIUM -> PriorityMedium
    Priority.HIGH -> PriorityHigh
}

fun Priority.toDisplayText() = when (this) {
    Priority.LOW -> "Low"
    Priority.MEDIUM -> "Medium"
    Priority.HIGH -> "High"
}

fun Category.toDisplayText() = when (this) {
    Category.WORK -> "Work"
    Category.PERSONAL -> "Personal"
    Category.LEISURE -> "Leisure"
}

