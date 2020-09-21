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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp
import com.alexjlockwood.beesandbombs.utils.CatmullRom
import kotlin.math.*

private const val N = 12
private const val n = 60

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
    val catmullRom = remember { CatmullRom() }
    val t = animatedProgress.value

    Canvas(modifier = modifier) {
        translate(size.width / 2f, size.height / 2f) {
            val l = size.minDimension / N / sqrt(2f)
            val sp = 1.25f * l
            for (i in 0 until N) {
                for (j in 0 until N) {
                    val tx = (i - .5f * (N - 1)) * sp
                    val ty = (j - .5f * (N - 1)) * sp
                    val tt = map(cos(TWO_PI * t + atan2(tx, ty) - dist(tx, ty, 0f, 0f) * 0.01f), 1f, -1f, 0f, 1f)
                    withTransform({
                        translate(tx, ty)
                        if ((i + j) % 2 == 0) {
                            rotate(90f, 0f, 0f)
                        }
                    }) {
                        drawTwistedLine(catmullRom, l, tt, darkColor)
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawTwistedLine(catmullRom: CatmullRom, l: Float, tt: Float, color: Color) {
    catmullRom.lineStart()
    catmullRom.point(-l / 2f, -l / 2f)
    for (i in 0 until n) {
        var qq = i / (n - 1f)
        qq = ease(qq)
        val x = lerp(-l / 2f, l / 2f, qq)
        val y = lerp(-l / 2f, l / 2f, qq)
        val tw = -tt * 10 * ease(1 - abs(2 * qq - 1), 1.5f)
        val xx = x * cos(tw) + y * sin(tw)
        val yy = y * cos(tw) - x * sin(tw)
        catmullRom.point(xx, yy)
    }
    catmullRom.point(l / 2, l / 2)
    catmullRom.lineEnd()
    drawPath(
        path = catmullRom.path,
        color = color,
        style = Stroke(width = 1.dp.toPx(), miter = 1f),
    )
}