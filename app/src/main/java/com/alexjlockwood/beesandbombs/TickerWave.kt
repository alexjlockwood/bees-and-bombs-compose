package com.alexjlockwood.beesandbombs

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp
import kotlin.math.*

private const val mn = 0.866025f
private val cs = arrayOf(Color(0xFF188C7C), Color(0xFFE6375A), Color(0xFF2C3A77))

@Composable
fun TickerWave(modifier: Modifier = Modifier) {
    val state = animationTimeMillis()

    Canvas(modifier = modifier.clipToBounds()) {
        val millis = state.value

        val t = (0.0002f * millis) % 1
        drawRect(Color(0xFFF5F4D5))

        val (width, height) = size
        val h = sqrt(width * height) * 0.05f
        val w = h / 4
        val sp = 2 * h * mn
        val n = ceil(.5f * max(width, height) / sp).toInt() +1

        translate(width / 2, height / 2) {
            for (a in 0 until 3) {
                for (i in (-n..n)) {
                    for (j in (-n..n)) {
                        var x = (i + 1f / 3f) * sp
                        val y = (j + 2f / 3f * (a - 1)) * mn * sp
                        if (j % 2 != 0) {
                            x += 0.5f * sp
                        }
                        val mouseX = width / 2f
                        val mouseY = height / 2f
                        val xx = x - mouseX + width / 2
                        val yy = y - mouseY + height / 2
                        var dd = max(abs(xx), abs(0.5f * xx + mn * yy))
                        dd = max(dd, abs(0.5f * xx - mn * yy))
                        val tt = (t + 100f - 0.0006f * dd) % 1f
                        val q = constrain(lerp(-1.5f, 2.5f, (3 * tt) % 1), 0f, 1f)
                        val th = -atan2(xx, yy) + PI * (3 * tt).toInt() / 3f + ease(q) * TWO_PI / 6
                        withTransform({
                            translate(x, y)
                            rotate(toDegrees(th), w / 2f, h / 2f)
                        }, {
                            drawRect(
                                color = cs[a],
                                size = Size(w, h),
                            )
                        })
                    }
                }
            }
        }

        val strokeWidth = 4.dp.toPx()
        drawRect(
            color = Color.Black,
            style = Stroke(strokeWidth),
        )
    }
}
