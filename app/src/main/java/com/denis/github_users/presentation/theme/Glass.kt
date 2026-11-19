package com.denis.github_users.presentation.theme

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

val AuroraTop = Color(0xFF1A1040)
val AuroraBottom = Color(0xFF0B1E3A)
private val BlobPurple = Color(0xFF8E5CFF)
private val BlobTeal = Color(0xFF03DAC5)
private val BlobPink = Color(0xFFFF5C9E)
private const val BLOB_CYCLE_MILLIS = 14_000
private const val SHIMMER_MILLIS = 1_200

/** Slowly drifting colour blobs that give the glass surfaces something to refract. */
@Composable
fun AuroraBackground(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    val transition = rememberInfiniteTransition(label = "aurora")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(BLOB_CYCLE_MILLIS, easing = LinearEasing)),
        label = "aurora_phase",
    )
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(AuroraTop, AuroraBottom))),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = min(size.width, size.height) * 0.75f
            fun blob(color: Color, cx: Float, cy: Float) = drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(color.copy(alpha = 0.55f), Color.Transparent),
                    center = Offset(cx, cy),
                    radius = radius,
                ),
                radius = radius,
                center = Offset(cx, cy),
            )
            blob(BlobPurple, size.width * (0.2f + 0.15f * cos(phase)), size.height * (0.2f + 0.1f * sin(phase)))
            blob(BlobTeal, size.width * (0.85f + 0.1f * sin(phase)), size.height * (0.5f + 0.15f * cos(phase)))
            blob(BlobPink, size.width * (0.3f + 0.2f * sin(phase)), size.height * (0.9f - 0.1f * cos(phase)))
        }
        content()
    }
}

/** Frosted translucent fill with a light-catching gradient border. */
fun Modifier.glass(shape: Shape): Modifier = this
    .clip(shape)
    .background(
        Brush.linearGradient(
            listOf(Color.White.copy(alpha = 0.22f), Color.White.copy(alpha = 0.06f)),
        ),
    )
    .border(
        width = 1.dp,
        brush = Brush.linearGradient(
            listOf(Color.White.copy(alpha = 0.6f), Color.White.copy(alpha = 0.05f)),
        ),
        shape = shape,
    )

/** Diagonal highlight sweeping across skeleton placeholders. */
fun Modifier.shimmer(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val progress by transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(SHIMMER_MILLIS, easing = LinearEasing), RepeatMode.Restart),
        label = "shimmer_progress",
    )
    background(
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.08f),
                Color.White.copy(alpha = 0.28f),
                Color.White.copy(alpha = 0.08f),
            ),
            start = Offset(progress * 1000f - 300f, 0f),
            end = Offset(progress * 1000f + 300f, 300f),
        ),
    )
}
