package com.alexjlockwood.beesandbombs

import androidx.compose.animation.animatedFloat
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.onActive
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun CircleSquare(modifier: Modifier = Modifier) {
    val animatedProgress = animatedFloat(0f)
    onActive {
        animatedProgress.animateTo(
            targetValue = 1f,
            anim = repeatable(
                iterations = AnimationConstants.Infinite,
                animation = tween(durationMillis = 2500, easing = LinearEasing),
            ),
        )
    }

    val darkColor = if (isSystemInDarkTheme()) Color.White else Color.Black
    val lightColor = if (isSystemInDarkTheme()) Color.Black else Color.White
    val t = animatedProgress.value

    Canvas(modifier = modifier) {
        translate(size.width / 2f, size.height / 2f) {
            if (t <= 0.5) {
                val tt = map(t, 0f, 0.5f, 0f, 1f)
                val rotation = 90f * ease(tt, 3f)

                rotate(rotation, 0f, 0f) {
                    drawCircles(270f, -360f * ease(tt, 3f), darkColor)
                }
            } else {
                val tt = map(t, 0.5f, 1f, 0f, 1f)
                val rotation = -90f * ease(tt, 3f)

                rotate(rotation, 0f, 0f) {
                    drawCircles(360f, 0f, darkColor)
                }

                rotate(-rotation, 0f, 0f) {
                    val rectSize = 2 * size.circleRadius()
                    drawRect(
                        color = lightColor,
                        topLeft = Offset(-rectSize / 2f, -rectSize / 2f),
                        size = Size(rectSize, rectSize),
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawCircles(sweepAngle: Float, rotation: Float, color: Color) {
    val circleRadius = size.circleRadius()
    for (i in 0 until 4) {
        val r = circleRadius * sqrt(2f)
        val theta = (HALF_PI + PI * i) / 2f
        val tx = r * cos(theta)
        val ty = r * sin(theta)
        withTransform({
            translate(-tx, -ty)
            rotate(rotation, 0f, 0f)
        }, {
            val rectSize = 2 * (circleRadius - circleRadius / 16f)
            drawArc(
                color = color,
                startAngle = 90f * (i + 1),
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(-rectSize / 2f, -rectSize / 2f),
                size = Size(rectSize, rectSize),
            )
        })
    }
}

private fun Size.circleRadius(): Float {
    return minDimension / 4f / sqrt(2f)
}
