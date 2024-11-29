package com.alexjlockwood.beesandbombs.demos

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ArcTwist(modifier: Modifier = Modifier) {
    val animatedProgress = remember { Animatable(0f) }
    val t = animatedProgress.value

    LaunchedEffect(Unit) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 6000, easing = LinearEasing),
            ),
        )
    }

    Canvas(
        modifier = modifier
            .aspectRatio(1f)
            .clipToBounds(),
    ) {
        val tt = t * 4 - when {
            t < 0.25f -> 0
            t < 0.5f -> 1
            t < 0.75f -> 2
            else -> 3
        }

        val n = 16
        val radius = size.minDimension / n

        if ((0 <= t && t < 0.25f) || (0.5f <= t && t < 0.75f)) {
            drawRect(color = Blue)

            // Draw the semicircles.
            for (i in 0..n) {
                for (j in 0..n) {
                    if (i % 2 == j % 2) {
                        withTransform({
                            rotate(if ((t < 0.25f && i % 2 == 0) || (0.5f <= t && i % 2 == 1)) 0f else 90f)
                            translate(i * radius * 2, j * radius * 2)
                            rotate(tt * 90f, pivot = Offset.Zero)
                        }) {
                            drawSemicircles(radius = radius)
                        }
                    }
                }
            }
        } else {
            drawRect(color = Red)

            // Draw the stars.
            for (i in 0..n) {
                for (j in 0..n) {
                    if (i % 2 == 1 && j % 2 == 1) {
                        withTransform({
                            rotate(if (t < 0.5f) 90f else 0f)
                            translate(i * radius * 2, (j - 1) * radius * 2)
                            rotate(tt * 90f, pivot = Offset.Zero)
                        }) {
                            drawStar(radius = radius)
                        }
                    }
                }
            }

            // Draw the squares.
            for (i in 0..n) {
                for (j in 0..n) {
                    if (i % 2 == 0 && j % 2 == 0) {
                        withTransform({
                            rotate(if (0.75f <= t) 90f else 0f)
                            translate((i - 1) * radius * 2, j * radius * 2)
                            rotate(tt * 90f, pivot = Offset.Zero)
                        }) {
                            drawSquare(radius = radius)
                        }
                    }
                }
            }
        }

        drawRect(color = Black, style = Stroke(16.dp.toPx()))
    }
}

private fun DrawScope.drawSemicircles(radius: Float) {
    drawSemicircles(radius = radius, color = Red, style = Fill)
    drawSemicircles(radius = radius, color = Black, style = strokeStyle)
}

private fun DrawScope.drawSemicircles(radius: Float, color: Color, style: DrawStyle) {
    drawArc(
        color = color,
        startAngle = 0f,
        sweepAngle = 180f,
        useCenter = true,
        size = Size(2 * radius, 2 * radius),
        topLeft = Offset(-radius, -2 * radius),
        style = style,
    )

    drawArc(
        color = color,
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        size = Size(2 * radius, 2 * radius),
        topLeft = Offset(-radius, 0f),
        style = style,
    )
}

private fun DrawScope.drawSquare(radius: Float) {
    val topLeft = Offset(-radius, -radius)
    val size = Size(2 * radius, 2 * radius)
    drawRect(color = Blue, topLeft = topLeft, size = size)
    drawRect(color = Black, topLeft = topLeft, size = size, style = strokeStyle)
}

private fun DrawScope.drawStar(radius: Float) {
    val path = Path()

    fun arcTo(rectOffset: Offset, startAngle: Float) {
        path.arcTo(
            rect = Rect(offset = rectOffset, size = Size(radius * 2, radius * 2)),
            startAngleDegrees = startAngle,
            sweepAngleDegrees = 90f,
            forceMoveTo = false,
        )
    }

    arcTo(rectOffset = Offset(-2 * radius, -3 * radius), startAngle = 0f)
    arcTo(rectOffset = Offset(-3 * radius, -2 * radius), startAngle = 0f)
    arcTo(rectOffset = Offset(-3 * radius, 0f), startAngle = 270f)
    arcTo(rectOffset = Offset(-2 * radius, radius), startAngle = 270f)
    arcTo(rectOffset = Offset(0f, radius), startAngle = 180f)
    arcTo(rectOffset = Offset(radius, 0f), startAngle = 180f)
    arcTo(rectOffset = Offset(radius, -2 * radius), startAngle = 90f)
    arcTo(rectOffset = Offset(0f, -3 * radius), startAngle = 90f)

    drawPath(path = path, color = Blue)
    drawPath(path = path, color = Black, style = strokeStyle)
}

private val DrawScope.strokeStyle: DrawStyle
    get() = Stroke(width = StrokeWidth.toPx())

private val StrokeWidth = 2.dp
private val Red = Color(0xffff2500)
private val Blue = Color(0xff0a8cb2)


@Preview(device = Devices.PIXEL_TABLET)
@Composable
private fun ArcTwistPreview() {
    ArcTwist()
}
