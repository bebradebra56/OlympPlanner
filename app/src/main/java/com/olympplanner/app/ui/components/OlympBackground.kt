package com.olympplanner.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.olympplanner.app.ui.theme.ThemeMode
import kotlin.math.sin

@Composable
fun OlympBackground(
    themeMode: ThemeMode,
    showColumns: Boolean = true,
    glowIntensity: Float = 0.5f,
    modifier: Modifier = Modifier
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    
    val infiniteTransition = rememberInfiniteTransition(label = "olymp_animation")
    
    val lightningAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lightning_alpha"
    )
    
    val cloudsOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "clouds_offset"
    )

    Canvas(modifier = modifier
        .fillMaxSize()
        .background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    primaryContainer,
                    backgroundColor.copy(alpha = 0.95f),
                    backgroundColor
                ),
                startY = 0f,
                endY = Float.POSITIVE_INFINITY
            )
        )
    ) {
        // Draw subtle clouds/mist effect
        drawClouds(cloudsOffset, themeMode)
        
        // Draw marble columns
        if (showColumns) {
            drawMarbleColumns(themeMode, glowIntensity)
        }
        
        // Draw subtle stars for night theme with glow intensity
        if (themeMode == ThemeMode.NIGHT) {
            drawStars(glowIntensity)
        }
    }
}

private fun DrawScope.drawClouds(offset: Float, themeMode: ThemeMode) {
    val cloudColor = when (themeMode) {
        ThemeMode.DAY -> Color.White.copy(alpha = 0.05f)
        ThemeMode.NIGHT -> Color.White.copy(alpha = 0.02f)
    }
    
    for (i in 0..3) {
        val x = (offset + i * 250f) % (size.width + 200f) - 100f
        val y = size.height * (0.1f + i * 0.15f)
        
        drawCircle(
            color = cloudColor,
            radius = 80f + i * 20f,
            center = Offset(x, y),
            alpha = 0.3f
        )
    }
}

private fun DrawScope.drawMarbleColumns(themeMode: ThemeMode, glowIntensity: Float) {
    // Base column color increases with glow intensity
    val columnColor = when (themeMode) {
        ThemeMode.DAY -> Color.White.copy(alpha = 0.15f + 0.15f * glowIntensity)
        ThemeMode.NIGHT -> Color.White.copy(alpha = 0.04f + 0.08f * glowIntensity)
    }
    
    // More visible glow effect
    val glowColor = when (themeMode) {
        ThemeMode.DAY -> Color.White.copy(alpha = 0.05f + 0.25f * glowIntensity)
        ThemeMode.NIGHT -> Color(0xFFEAF3FF).copy(alpha = 0.02f + 0.18f * glowIntensity)
    }
    
    val columnWidth = 50f
    val columnCount = 3
    val spacing = size.width / (columnCount + 1)
    
    for (i in 1..columnCount) {
        val x = spacing * i
        val columnHeight = size.height * 0.65f
        val columnTop = size.height * 0.18f
        
        // Draw column with gradient
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    columnColor.copy(alpha = 0.5f),
                    columnColor,
                    columnColor.copy(alpha = 0.5f)
                ),
                startX = x - columnWidth / 2,
                endX = x + columnWidth / 2
            ),
            topLeft = Offset(x - columnWidth / 2, columnTop),
            size = androidx.compose.ui.geometry.Size(columnWidth, columnHeight)
        )
        
        // Draw column glow - size increases with intensity
        if (glowIntensity > 0.05f) {
            val glowSize = 5f + 20f * glowIntensity  // Glow expands from 5px to 25px
            drawRect(
                color = glowColor,
                topLeft = Offset(x - columnWidth / 2 - glowSize, columnTop - glowSize),
                size = androidx.compose.ui.geometry.Size(columnWidth + glowSize * 2, columnHeight + glowSize * 2)
            )
        }
        
        // Draw capital (top of column)
        drawRect(
            color = columnColor,
            topLeft = Offset(x - columnWidth / 1.5f, columnTop - 15f),
            size = androidx.compose.ui.geometry.Size(columnWidth * 1.3f, 15f)
        )
        
        // Draw base
        drawRect(
            color = columnColor,
            topLeft = Offset(x - columnWidth / 1.5f, columnTop + columnHeight),
            size = androidx.compose.ui.geometry.Size(columnWidth * 1.3f, 12f)
        )
    }
}

private fun DrawScope.drawLightningBolts(alpha: Float) {
    val lightningColor = Color(0xFFFFFFFF).copy(alpha = alpha)
    val glowColor = Color(0xFFEAF3FF).copy(alpha = alpha * 0.5f)
    
    // Main lightning bolt - left side
    val path1 = Path().apply {
        moveTo(size.width * 0.25f, 0f)
        lineTo(size.width * 0.28f, size.height * 0.15f)
        lineTo(size.width * 0.24f, size.height * 0.15f)
        lineTo(size.width * 0.30f, size.height * 0.35f)
        lineTo(size.width * 0.22f, size.height * 0.35f)
        lineTo(size.width * 0.26f, size.height * 0.55f)
    }
    
    // Draw glow first
    drawPath(
        path = path1,
        color = glowColor,
        style = Stroke(width = 6f)
    )
    
    // Draw main bolt
    drawPath(
        path = path1,
        color = lightningColor,
        style = Stroke(width = 2.5f)
    )
    
    // Secondary lightning bolt - right side
    val path2 = Path().apply {
        moveTo(size.width * 0.75f, 0f)
        lineTo(size.width * 0.72f, size.height * 0.12f)
        lineTo(size.width * 0.76f, size.height * 0.12f)
        lineTo(size.width * 0.70f, size.height * 0.28f)
        lineTo(size.width * 0.78f, size.height * 0.28f)
        lineTo(size.width * 0.74f, size.height * 0.48f)
    }
    
    // Draw glow
    drawPath(
        path = path2,
        color = glowColor,
        style = Stroke(width = 6f)
    )
    
    // Draw main bolt
    drawPath(
        path = path2,
        color = lightningColor,
        style = Stroke(width = 2.5f)
    )
}

private fun DrawScope.drawStars(glowIntensity: Float) {
    val starPositions = listOf(
        Offset(size.width * 0.15f, size.height * 0.1f),
        Offset(size.width * 0.85f, size.height * 0.15f),
        Offset(size.width * 0.60f, size.height * 0.08f),
        Offset(size.width * 0.30f, size.height * 0.18f),
        Offset(size.width * 0.75f, size.height * 0.12f),
        Offset(size.width * 0.45f, size.height * 0.20f)
    )
    
    // Star brightness and glow increase with intensity
    val starAlpha = 0.3f + 0.4f * glowIntensity
    val glowAlpha = 0.05f + 0.25f * glowIntensity
    val glowRadius = 4f + 10f * glowIntensity
    
    starPositions.forEach { position ->
        // Star core
        drawCircle(
            color = Color.White.copy(alpha = starAlpha),
            radius = 2f,
            center = position
        )
        
        // Star glow - expands and brightens with intensity
        drawCircle(
            color = Color.White.copy(alpha = glowAlpha),
            radius = glowRadius,
            center = position
        )
    }
}

