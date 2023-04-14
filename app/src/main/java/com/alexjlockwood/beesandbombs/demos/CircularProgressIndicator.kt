package com.alexjlockwood.beesandbombs.demos

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.Group
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.alexjlockwood.beesandbombs.demos.utils.PathEasing

@Composable
fun CircularProgressIndicator(modifier: Modifier = Modifier) {
    val darkColor = if (isSystemInDarkTheme()) Color.White else Color.Black

    val transition = rememberInfiniteTransition()
    val trimPathStart by transition.animateFloat(0f, 0.75f, infiniteRepeatable(tween(durationMillis = 1333, easing = TrimPathStartEasing)))
    val trimPathEnd by transition.animateFloat(0.03f, 0.78f, infiniteRepeatable(tween(durationMillis = 1333, easing = TrimPathEndEasing)))
    val trimPathOffset by transition.animateFloat(0f, 0.25f, infiniteRepeatable(tween(durationMillis = 1333, easing = LinearEasing)))
    val rotation by transition.animateFloat(0f, 720f, infiniteRepeatable(tween(durationMillis = 4444, easing = LinearEasing)))

    Image(
        painter = rememberVectorPainter(
            defaultWidth = 48.dp,
            defaultHeight = 48.dp,
            viewportWidth = 48f,
            viewportHeight = 48f,
            autoMirror = false,
        ) { _, _ ->
            Group(
                translationX = 24f,
                translationY = 24f,
                rotation = rotation,
            ) {
                Path(
                    pathData = remember { addPathNodes("m 0 -18 a 18 18 0 1 1 0 36 a 18 18 0 1 1 0 -36") },
                    stroke = SolidColor(darkColor),
                    strokeLineCap = StrokeCap.Square,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineWidth = 4f,
                    trimPathStart = trimPathStart,
                    trimPathEnd = trimPathEnd,
                    trimPathOffset = trimPathOffset,
                )
            }
        },
        contentDescription = null,
        modifier = modifier,
    )
}

private val TrimPathStartEasing = PathEasing("M 0 0 L 0.5 0 C 0.7 0 0.6 1 1 1")
private val TrimPathEndEasing = PathEasing("M 0 0 C 0.2 0 0.1 1 0.5 0.96 C 0.96 0.96 1 1 1 1")
