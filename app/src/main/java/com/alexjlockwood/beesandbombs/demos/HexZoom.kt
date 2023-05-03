package com.alexjlockwood.beesandbombs.demos

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp
import com.alexjlockwood.beesandbombs.demos.utils.animationTimeMillis
import com.alexjlockwood.beesandbombs.demos.utils.dist
import com.alexjlockwood.beesandbombs.demos.utils.ease
import com.alexjlockwood.beesandbombs.demos.utils.lerp
import com.alexjlockwood.beesandbombs.demos.utils.map
import com.alexjlockwood.beesandbombs.demos.utils.toDegrees
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun HexZoom(modifier: Modifier = Modifier) {
    val millis by animationTimeMillis()
    val t = (millis / 4800f) % 1
    val strokeColor = if (isSystemInDarkTheme()) Color.White else Color.Black

    Canvas(modifier = modifier.clipToBounds()) {
        translate(size.width / 2f, size.height / 2f) {
            // Multiply by 2^t to create the zoom effect.
            val r = (size.minDimension / (2 * N)) * 2f.pow(t)
            for (i in -N until N) {
                for (j in -N until N) {
                    val x = r * sqrt(3f) * (i + if (j % 2 == 0) 0f else 0.5f)
                    val y = r * (1.5f * j + 1f)
                    translate(x, y) {
                        val d = dist(0f, 0f, x, y)
                        // Transform t to create a smoother morph animation.
                        var tt = (2.5f * t - 0.0025f * d).coerceIn(0f, 1f)
                        tt = (tt + ease(tt)) / 2f
                        drawHex(t = tt, r = r, color = strokeColor)
                    }
                }
            }
        }

        drawRect(color = strokeColor, style = Stroke(8.dp.toPx()))
    }
}

private fun DrawScope.drawHex(t: Float, r: Float, color: Color) {
    val path = Path()
    for (i in 0 until 3) {
        val numSteps = 16
        for (j in 0 until numSteps) {
            val rt = j / numSteps.toFloat()
            // Break each line into 16 segments and gradually morph each to their new position.
            var tick = abs(2f * map(rt, 0.25f, 0.75f, 0f, 1f).coerceIn(0f, 1f) - 1f)
            tick = map(tick, 0f, 1f, r / 2f * ease((1.5f * t - tick / 2f).coerceIn(0f, 1f)), 0f)
            val x = lerp(-r, r, rt) * sqrt(3f) / 2f
            val y = map(abs(2f * rt - 1f), 0f, 1f, r, r / 2f) - tick
            val x1 = x * cos(TwoPi * i / 3f) + y * sin(TwoPi * i / 3f)
            val y1 = y * cos(TwoPi * i / 3f) - x * sin(TwoPi * i / 3f)
            if (j == 0) {
                path.moveTo(x1, y1)
            } else {
                path.lineTo(x1, y1)
            }
        }
    }

    drawPath(
        path = path,
        color = color,
        style = Stroke(HexStrokeWidth.toPx()),
    )

    // Draw 3 lines to create the new hex shapes.
    val y = lerp(r, r / 2, ease((1.5f * t).coerceIn(0f, 1f)))
    for (i in 0 until 3) {
        rotate(degrees = (TwoPi * i / 3f).toDegrees(), pivot = Offset.Zero) {
            drawLine(
                color = color,
                start = Offset(0f, y),
                end = Offset(0f, y - r / 2f * ease((1.5f * t - 0.25f).coerceIn(0f, 1f))),
                strokeWidth = HexStrokeWidth.toPx(),
            )
        }
    }
}

private const val N = 6
private const val TwoPi = (Math.PI * 2).toFloat()
private val HexStrokeWidth = 2.dp
