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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp
import kotlin.math.sqrt

@Composable
fun SquareTwist(modifier: Modifier = Modifier) {
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

    Canvas(modifier = modifier.clipToBounds()) {
        val n = 9
        val s = size.minDimension / n
        val l = s * sqrt(2f) / 2f
        val tt = t * 2 - if (t < 0.5f) 0 else 1
        val rotation = 90f * tt
        if (t < 0.25f || 0.75f < t) {
            drawRect(lightColor)
            for (i in 0..n) {
                for (j in 0..n) {
                    withTransform({
                        translate(i * s, j * s)
                        rotate(rotation, 0f, 0f)
                    }, {
                        drawRect(
                            color = darkColor,
                            topLeft = Offset(-l / 2, -l / 2),
                            size = Size(l, l),
                        )
                    })
                }
            }
        } else {
            drawRect(darkColor)
            for (i in 0..n) {
                for (j in 0..n) {
                    withTransform({
                        translate((i + 0.5f) * s, (j + 0.5f) * s)
                        rotate(rotation, 0f, 0f)
                    }, {
                        drawRect(
                            color = lightColor,
                            topLeft = Offset(-l / 2, -l / 2),
                            size = Size(l, l),
                        )
                    })
                }
            }
        }

        drawRect(
            color = darkColor,
            style = Stroke(16.dp.toPx()),
        )
    }
}
