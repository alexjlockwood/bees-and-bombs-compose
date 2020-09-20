package com.alexjlockwood.beesandbombs

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

private const val N = 360
private const val SPEED = 1f / 1000f
private const val SHIFT = TWO_PI / 3f
private const val FREQUENCY = 8

@Composable
fun CircleWave(modifier: Modifier = Modifier) {
    val state = animationTimeMillis()
    val path = remember { Path() }
    val colors = listOf(Color.Cyan, Color.Magenta, Color.Yellow)

    Canvas(modifier = modifier) {
        drawRect(Color.White)

        val (width, height) = size
        val waveAmplitude = size.minDimension / 20
        val circleRadius = size.minDimension / 2f - 2 * waveAmplitude

        translate(width / 2f, height / 2f) {
            colors.forEachIndexed { colorIndex, color ->
                path.reset()
                for (i in 0 until N) {
                    val a = i * TWO_PI / N
                    val t = state.value * SPEED
                    val c = cos(a * FREQUENCY - colorIndex * SHIFT + t)
                    val p = ((1 + cos(a - t)) / 2).pow(3)
                    val r = circleRadius + waveAmplitude * c * p
                    val x = r * sin(a)
                    val y = r * -cos(a)
                    if (i == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                }
                path.close()

                drawPath(
                    path = path,
                    color = color,
                    style = Stroke(
                        width = 8.dp.toPx(),
                        join = StrokeJoin.Round,
                    ),
                    blendMode = BlendMode.Darken,
                )
            }
        }
    }
}
