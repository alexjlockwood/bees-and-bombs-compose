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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.*

private const val N = 12
private const val n = 360

/**
 * Creates a composable wave spiral animation.
 */
@Composable
fun WaveSpiral(modifier: Modifier = Modifier) {
    val animatedProgress = animatedFloat(0f)
    onActive {
        animatedProgress.animateTo(
            targetValue = 1f,
            anim = repeatable(
                iterations = AnimationConstants.Infinite,
                animation = tween(durationMillis = 5000, easing = LinearEasing),
            ),
        )
    }

    val darkColor = if (isSystemInDarkTheme()) Color.White else Color.Black
    val path = remember { Path() }
    val t = animatedProgress.value

    Canvas(modifier = modifier) {
        withTransform({
            translate(size.width / 2f, size.height / 2f)
        }, {
            val l = size.minDimension / N / sqrt(2f)
            val sp = 1.25f * l
            for (i in 0 until N) {
                for (j in 0 until N) {
                    val X = (i - .5f * (N - 1)) * sp
                    val Y = (j - .5f * (N - 1)) * sp
                    val tt = map(cos(TWO_PI * t + atan2(X, Y) - dist(X, Y, 0f, 0f) * 0.01f), 1f, -1f, 0f, 1f)
                    withTransform({
                        translate(X, Y)
                        if ((i + j) % 2 == 0) {
                            rotate(90f, 0f, 0f)
                        }
                    }) {
                        path.reset()
                        path.moveTo(-l / 2f, -l / 2f)
                        for (i in 0 until n) {
                            var qq = i / (n - 1f)
                            qq = ease(qq)
                            val x = lerp(-l / 2f, l / 2f, qq)
                            val y = lerp(-l / 2f, l / 2f, qq)
                            val tw = -tt * 10 * ease(1 - abs(2 * qq - 1), 1.5f)
                            val xx = x * cos(tw) + y * sin(tw)
                            val yy = y * cos(tw) - x * sin(tw)
                            path.lineTo(xx, yy)
                        }
                        path.lineTo(l / 2, l / 2)
                        drawPath(
                            path = path,
                            color = darkColor,
                            style = Stroke(width = 2f, miter = 1f),
                        )
                    }
                }
            }
        })
    }
}

fun twistLine(path: Path, q: Float) {

}
