package com.olympplanner.app.ui.screens

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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.olympplanner.app.R
import com.olympplanner.app.domain.model.*
import com.olympplanner.app.ui.components.*
import com.olympplanner.app.ui.theme.*
import com.olympplanner.app.ui.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun DayScreen(
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val tasks by viewModel.getTasksForDay(selectedDate).collectAsState(initial = emptyList())
    
    var showTaskDialog by remember { mutableStateOf(false) }
    var selectedTask by remember { mutableStateOf<Task?>(null) }
    var selectedSegment by remember { mutableStateOf<TimeSegment?>(null) }

    val completedCount = tasks.count { it.isCompleted }
    val totalCount = tasks.size
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Elite Day Header
            OlympDayHeader(
                date = selectedDate,
                progress = progress,
                completedCount = completedCount,
                totalCount = totalCount,
                onPreviousDay = {
                    viewModel.setSelectedDate(selectedDate - 24 * 60 * 60 * 1000L)
                },
                onNextDay = {
                    viewModel.setSelectedDate(selectedDate + 24 * 60 * 60 * 1000L)
                }
            )

            // Time Segments Filter
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
            ) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TimeSegmentChip(
                        label = "All",
                        icon = Icons.Default.AllInclusive,
                        selected = selectedSegment == null,
                        onClick = { selectedSegment = null }
                    )
                    
                    TimeSegment.entries.forEach { segment ->
                        TimeSegmentChip(
                            label = when (segment) {
                                TimeSegment.MORNING -> stringResource(R.string.day_morning)
                                TimeSegment.AFTERNOON -> stringResource(R.string.day_afternoon)
                                TimeSegment.EVENING -> stringResource(R.string.day_evening)
                            },
                            icon = when (segment) {
                                TimeSegment.MORNING -> Icons.Default.WbSunny
                                TimeSegment.AFTERNOON -> Icons.Default.LightMode
                                TimeSegment.EVENING -> Icons.Default.Nightlight
                            },
                            selected = selectedSegment == segment,
                            onClick = { selectedSegment = segment }
                        )
                    }
                }
            }

            // Tasks List
            if (tasks.isEmpty()) {
                EmptyDayState()
            } else {
                val filteredTasks = if (selectedSegment != null) {
                    tasks.filter { task -> getTimeSegment(task.dateTime) == selectedSegment }
                } else {
                    tasks
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredTasks, key = { it.id }) { task ->
                        DayTaskCard(
                            task = task,
                            onClick = {
                                selectedTask = task
                                showTaskDialog = true
                            },
                            onComplete = {
                                viewModel.toggleTaskCompletion(task.id, !task.isCompleted)
                            },
                            modifier = Modifier.animateItemPlacement()
                        )
                    }
                }
            }
        }

        // Premium FAB
        FloatingActionButton(
            onClick = {
                selectedTask = null
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
private fun OlympDayHeader(
    date: Long,
    progress: Float,
    completedCount: Int,
    totalCount: Int,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("EEEE, MMM d", Locale.ENGLISH)
    
    val infiniteTransition = rememberInfiniteTransition(label = "lightning")
    val lightningRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
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
                    onClick = onPreviousDay,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Icon(
                        Icons.Default.ChevronLeft,
                        contentDescription = "Previous Day",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = dateFormat.format(Date(date)),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    if (totalCount > 0) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Bolt,
                                contentDescription = "Progress",
                                tint = ElectricBlue,
                                modifier = Modifier
                                    .size(24.dp)
                                    .rotate(lightningRotation / 10)
                            )
                            
                            Box(
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(progress)
                                        .background(
                                            brush = Brush.horizontalGradient(
                                                colors = listOf(
                                                    ElectricBlue,
                                                    AccentGold
                                                )
                                            )
                                        )
                                )
                            }
                            
                            Text(
                                text = "$completedCount / $totalCount",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = ElectricBlue
                            )
                        }
                    }
                }
                
                IconButton(
                    onClick = onNextDay,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "Next Day",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun TimeSegmentChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = if (selected) ElectricBlue else MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = if (selected) 3.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
private fun DayTaskCard(
    task: Task,
    onClick: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)
    val categoryColor = when (task.category) {
        Category.WORK -> CategoryWork
        Category.PERSONAL -> CategoryPersonal
        Category.LEISURE -> CategoryLeisure
    }

    val priorityColor = when (task.priority) {
        Priority.LOW -> PriorityLow
        Priority.MEDIUM -> PriorityMedium
        Priority.HIGH -> PriorityHigh
    }

    OlympMarbleCard(
        onClick = onClick,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Category Accent Bar
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .height(70.dp)
                    .clip(RoundedCornerShape(2.5.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                categoryColor,
                                categoryColor.copy(alpha = 0.6f)
                            )
                        )
                    )
            )
            
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onComplete() },
                colors = CheckboxDefaults.colors(
                    checkedColor = SuccessGreen,
                    uncheckedColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = categoryColor.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Circle,
                                contentDescription = null,
                                modifier = Modifier.size(8.dp),
                                tint = categoryColor
                            )
                            Text(
                                text = when (task.category) {
                                    Category.WORK -> stringResource(R.string.category_work)
                                    Category.PERSONAL -> stringResource(R.string.category_personal)
                                    Category.LEISURE -> stringResource(R.string.category_leisure)
                                },
                                style = MaterialTheme.typography.labelMedium,
                                color = categoryColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AccessTime,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = timeFormat.format(Date(task.dateTime)),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    if (task.duration > 0) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "${task.duration}min",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
            
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Priority indicator
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    repeat(task.priority.ordinal + 1) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(priorityColor)
                        )
                    }
                }
                
                if (task.reminders.isNotEmpty()) {
                    Surface(
                        shape = CircleShape,
                        color = AccentGold.copy(alpha = 0.15f)
                    ) {
                        Icon(
                            Icons.Default.NotificationsActive,
                            contentDescription = "Has Reminders",
                            tint = AccentGold,
                            modifier = Modifier
                                .padding(6.dp)
                                .size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyDayState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.EventAvailable,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = ElectricBlue.copy(alpha = 0.2f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.day_empty_state),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap + to add your first task",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun getTimeSegment(timeInMillis: Long): TimeSegment {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeInMillis
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    
    return when (hour) {
        in 0..11 -> TimeSegment.MORNING
        in 12..17 -> TimeSegment.AFTERNOON
        else -> TimeSegment.EVENING
    }
}
