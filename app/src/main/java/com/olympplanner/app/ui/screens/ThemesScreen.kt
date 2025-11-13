package com.olympplanner.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.olympplanner.app.R
import com.olympplanner.app.ui.theme.*
import com.olympplanner.app.ui.viewmodel.ThemeViewModel

@Composable
fun ThemesScreen(
    viewModel: ThemeViewModel,
    modifier: Modifier = Modifier
) {
    val themeSettings by viewModel.themeSettings.collectAsState()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Personalize Your Olympus",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Text(
                text = "Choose Your Theme",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ThemeModeCard(
                    mode = ThemeMode.DAY,
                    isSelected = themeSettings.mode == ThemeMode.DAY,
                    onClick = { viewModel.updateThemeMode(ThemeMode.DAY) },
                    modifier = Modifier.weight(1f)
                )
                
                ThemeModeCard(
                    mode = ThemeMode.NIGHT,
                    isSelected = themeSettings.mode == ThemeMode.NIGHT,
                    onClick = { viewModel.updateThemeMode(ThemeMode.NIGHT) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Customization",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    // Glow Intensity
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.themes_glow_intensity),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "${(themeSettings.glowIntensity * 100).toInt()}%",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Slider(
                            value = themeSettings.glowIntensity,
                            onValueChange = { viewModel.updateGlowIntensity(it) },
                            valueRange = 0f..1f
                        )
                    }

                    // Accent Saturation
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.themes_accent_saturation),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "${(themeSettings.accentSaturation * 100).toInt()}%",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Slider(
                            value = themeSettings.accentSaturation,
                            onValueChange = { viewModel.updateAccentSaturation(it) },
                            valueRange = 0f..1f
                        )
                    }

                    // Show Columns
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.themes_show_columns),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Display marble columns in the background",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = themeSettings.showColumns,
                            onCheckedChange = { viewModel.updateShowColumns(it) }
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Themes change the entire app appearance, including background gradients and accent colors.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeModeCard(
    mode: ThemeMode,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.outline
        },
        label = "border_color"
    )

    val backgroundColor = when (mode) {
        ThemeMode.DAY -> Brush.verticalGradient(
            colors = listOf(SkyBlueLight, SkyBlueDark)
        )
        ThemeMode.NIGHT -> Brush.verticalGradient(
            colors = listOf(NightSkyLight, NightSkyDark)
        )
    }

    Card(
        modifier = modifier
            .aspectRatio(0.8f)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (isSelected) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Selected",
                            tint = ElectricBlue,
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    }
                }

                Column {
                    when (mode) {
                        ThemeMode.DAY -> {
                            Icon(
                                Icons.Default.WbSunny,
                                contentDescription = null,
                                tint = AccentGold,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        ThemeMode.NIGHT -> {
                            Icon(
                                Icons.Default.Nightlight,
                                contentDescription = null,
                                tint = TextPrimaryNight,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = when (mode) {
                            ThemeMode.DAY -> stringResource(R.string.themes_day)
                            ThemeMode.NIGHT -> stringResource(R.string.themes_night)
                        },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (mode) {
                            ThemeMode.DAY -> TextPrimaryDay
                            ThemeMode.NIGHT -> TextPrimaryNight
                        }
                    )
                }
            }
        }
    }
}

