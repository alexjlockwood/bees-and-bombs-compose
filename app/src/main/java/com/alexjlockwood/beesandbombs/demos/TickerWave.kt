package com.alexjlockwood.beesandbombs.demos

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp
import com.alexjlockwood.beesandbombs.demos.utils.PI
import com.alexjlockwood.beesandbombs.demos.utils.TWO_PI
import com.alexjlockwood.beesandbombs.demos.utils.animationTimeMillis
import com.alexjlockwood.beesandbombs.demos.utils.ease
import com.alexjlockwood.beesandbombs.demos.utils.lerp
import com.alexjlockwood.beesandbombs.demos.utils.toDegrees
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.sqrt

private val SPACING = sqrt(0.75f)
private val BACKGROUND_COLOR = Color(0xFFF5F4D5)
private val COLORS = listOf(Color(0xFF188C7C), Color(0xFFE6375A), Color(0xFF2C3A77))

@Composable
fun TickerWave(modifier: Modifier = Modifier) {
    val millis by animationTimeMillis()

    Canvas(modifier = modifier.clipToBounds()) {
        drawRect(BACKGROUND_COLOR)

        val t = (0.0002f * millis) % 1
        val s = size.minDimension
        val h = s / 20
        val w = h / 4
        val sp = 2 * h * SPACING
        val n = ceil(.5f * s / sp).toInt() + 1

        translate(s / 2, s / 2) {
            for (c in COLORS.indices) {
                for (i in (-n..n)) {
                    for (j in (-n..n)) {
                        val x = (i + 1f / 3f) * sp + if (j % 2 != 0) 0.5f * sp else 0f
                        val y = (j + 2f / 3f * (c - 1)) * SPACING * sp
                        val dd = max(max(abs(x), abs(0.5f * x + SPACING * y)), abs(0.5f * x - SPACING * y))
                        val tt = (t + 100f - 0.0006f * dd) % 1f
                        val q = lerp(-1.5f, 2.5f, (3 * tt) % 1).coerceIn(0f, 1f)
                        val th = -atan2(x, y) + PI * (3 * tt).toInt() / 3f + ease(q) * TWO_PI / 6
                        withTransform({
                            translate(x, y)
                            rotate(th.toDegrees(), Offset(w / 2f, h / 2f))
                        }, {
                            drawRect(color = COLORS[c], size = Size(w, h))
                        })
                    }
                }
            }
        }

        drawRect(
            color = Color.Black,
            style = Stroke(4.dp.toPx()),
        )
    }
}
