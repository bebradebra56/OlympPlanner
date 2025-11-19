package com.olympplanner.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.olympplanner.app.R
import com.olympplanner.app.domain.model.*
import com.olympplanner.app.ui.components.*
import com.olympplanner.app.ui.theme.*
import com.olympplanner.app.ui.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun WeekScreen(
    viewModel: TaskViewModel,
    onNavigateToDay: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentWeekStart by remember { mutableLongStateOf(getWeekStart(System.currentTimeMillis())) }
    var showTaskDialog by remember { mutableStateOf(false) }
    var selectedTask by remember { mutableStateOf<Task?>(null) }
    var selectedDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val weekTasks by viewModel.getTasksForWeek(currentWeekStart).collectAsState(initial = emptyMap())

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Elegant Header
            OlympWeekHeader(
                weekStart = currentWeekStart,
                onPreviousWeek = { currentWeekStart -= 7 * 24 * 60 * 60 * 1000L },
                onNextWeek = { currentWeekStart += 7 * 24 * 60 * 60 * 1000L },
                onToday = { currentWeekStart = getWeekStart(System.currentTimeMillis()) }
            )

//            // Beautiful Filter Chips
//            Surface(
//                modifier = Modifier.fillMaxWidth(),
//                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
//            ) {
//                FlowRow(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 16.dp, vertical = 10.dp),
//                    horizontalArrangement = Arrangement.spacedBy(8.dp),
//                    verticalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    FilterType.entries.forEach { filter ->
//                        OlympFilterChip(
//                            selected = selectedFilter == filter,
//                            onClick = { viewModel.setFilter(filter) },
//                            label = when (filter) {
//                                FilterType.ALL -> stringResource(R.string.week_filter_all)
//                                FilterType.PERSONAL -> stringResource(R.string.week_filter_personal)
//                                FilterType.WORK -> stringResource(R.string.week_filter_work)
//                                FilterType.LEISURE -> stringResource(R.string.week_filter_leisure)
//                                FilterType.DEADLINES -> stringResource(R.string.week_filter_deadlines)
//                            }
//                        )
//                    }
//                }
//            }

            // Week Days Grid
            if (weekTasks.values.flatten().isEmpty()) {
                EmptyWeekState(
                    onAddTask = {
                        selectedTask = null
                        selectedDate = System.currentTimeMillis()
                        showTaskDialog = true
                    },
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(7) { dayIndex ->
                        val dayStart = currentWeekStart + dayIndex * 24 * 60 * 60 * 1000L
                        val dayTasks = weekTasks[dayStart] ?: emptyList()
                        
                        if (dayTasks.isNotEmpty()) {
                            OlympDayCard(
                                date = dayStart,
                                tasks = dayTasks,
                                onTaskClick = { task ->
                                    selectedTask = task
                                    showTaskDialog = true
                                },
                                onDayClick = { onNavigateToDay(dayStart) },
                                onTaskComplete = { task, isCompleted ->
                                    viewModel.toggleTaskCompletion(task.id, isCompleted)
                                },
//                                modifier = Modifier.animateItemPlacement()
                            )
                        }
                    }
                }
            }
        }

        // Elegant FAB
        FloatingActionButton(
            onClick = {
                selectedTask = null
                selectedDate = System.currentTimeMillis()
                showTaskDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .size(64.dp)
                .shadow(12.dp, CircleShape, ambientColor = ElectricBlue, spotColor = ElectricBlue),
            containerColor = ElectricBlue,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add Task",
                modifier = Modifier.size(28.dp)
            )
        }
    }

    if (showTaskDialog) {
        TaskDialog(
            task = selectedTask,
            initialDate = selectedDate,
            onDismiss = { showTaskDialog = false },
            onSave = { task ->
                if (selectedTask != null) {
                    viewModel.updateTask(task)
                } else {
                    viewModel.addTask(task)
                }
                showTaskDialog = false
            },
            onDelete = { task ->
                viewModel.deleteTask(task)
                showTaskDialog = false
            }
        )
    }
}

@Composable
private fun OlympWeekHeader(
    weekStart: Long,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onToday: () -> Unit,
    modifier: Modifier = Modifier
) {
    val weekEnd = weekStart + 6 * 24 * 60 * 60 * 1000L
    val dateFormat = SimpleDateFormat("MMM d", Locale.ENGLISH)
    
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onPreviousWeek,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Icon(
                        Icons.Default.ChevronLeft,
                        contentDescription = "Previous Week",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "${dateFormat.format(Date(weekStart))} — ${dateFormat.format(Date(weekEnd))}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    OutlinedButton(
                        onClick = onToday,
                        modifier = Modifier.height(32.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = ElectricBlue
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp)
                    ) {
                        Icon(
                            Icons.Default.Today,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            stringResource(R.string.week_today),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
                
                IconButton(
                    onClick = onNextWeek,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "Next Week",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun OlympFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = if (selected) ElectricBlue else MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = if (selected) 3.dp else 0.dp
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun OlympDayCard(
    date: Long,
    tasks: List<Task>,
    onTaskClick: (Task) -> Unit,
    onDayClick: () -> Unit,
    onTaskComplete: (Task, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = date
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
    
    val isToday = isSameDay(date, System.currentTimeMillis())
    val completedCount = tasks.count { it.isCompleted }
    val totalCount = tasks.size
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    OlympMarbleCard(
        onClick = onDayClick,
        modifier = modifier,
        isElevated = isToday,
        glowEffect = isToday
    ) {
        // Day Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dayOfMonth.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isToday) AccentGold else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = getDayName(dayOfWeek),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isToday) ElectricBlue else MaterialTheme.colorScheme.onSurface
                )
            }
            
            if (totalCount > 0) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    ElectricBlue.copy(alpha = 0.2f),
                                    ElectricBlue.copy(alpha = 0.3f),
                                    ElectricBlue.copy(alpha = 0.2f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.size(50.dp),
                        color = ElectricBlue,
                        strokeWidth = 4.dp
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = completedCount.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ElectricBlue
                        )
                        Text(
                            text = "/$totalCount",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        if (totalCount > 0) {
            Spacer(modifier = Modifier.height(12.dp))
            LightningDivider(thickness = 1)
            Spacer(modifier = Modifier.height(12.dp))
            
            tasks.take(3).forEach { task ->
                OlympTaskItem(
                    task = task,
                    onClick = { onTaskClick(task) },
                    onComplete = { onTaskComplete(task, !task.isCompleted) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            if (tasks.size > 3) {
                Text(
                    text = stringResource(R.string.week_more_tasks, tasks.size - 3),
                    style = MaterialTheme.typography.bodySmall,
                    color = ElectricBlue,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun OlympTaskItem(
    task: Task,
    onClick: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryColor = when (task.category) {
        Category.WORK -> CategoryWork
        Category.PERSONAL -> CategoryPersonal
        Category.LEISURE -> CategoryLeisure
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .clickable { onClick() }
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Category indicator bar
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(40.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(categoryColor)
        )
        
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = { onComplete() },
            colors = CheckboxDefaults.colors(
                checkedColor = SuccessGreen,
                uncheckedColor = MaterialTheme.colorScheme.outline
            )
        )
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            if (task.priority == Priority.HIGH) {
                Icon(
                    Icons.Default.PriorityHigh,
                    contentDescription = "High Priority",
                    tint = PriorityHigh,
                    modifier = Modifier.size(20.dp)
                )
            }
            if (task.reminders.isNotEmpty()) {
                Icon(
                    Icons.Default.NotificationsActive,
                    contentDescription = "Has Reminder",
                    tint = AccentGold,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyWeekState(
    onAddTask: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
        Icon(
            Icons.Default.CalendarMonth,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = ElectricBlue.copy(alpha = 0.3f)
        )
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.week_empty_state),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Plan your week and achieve greatness",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
            Button(
                onClick = onAddTask,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricBlue
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Add First Task",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

private fun getWeekStart(timeInMillis: Long): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeInMillis
    calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}

private fun isSameDay(time1: Long, time2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = time1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = time2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

@Composable
private fun getDayName(dayOfWeek: Int): String {
    return when (dayOfWeek) {
        Calendar.MONDAY -> stringResource(R.string.day_mon)
        Calendar.TUESDAY -> stringResource(R.string.day_tue)
        Calendar.WEDNESDAY -> stringResource(R.string.day_wed)
        Calendar.THURSDAY -> stringResource(R.string.day_thu)
        Calendar.FRIDAY -> stringResource(R.string.day_fri)
        Calendar.SATURDAY -> stringResource(R.string.day_sat)
        Calendar.SUNDAY -> stringResource(R.string.day_sun)
        else -> ""
    }
}
