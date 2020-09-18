package com.alexjlockwood.beesandbombs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

private const val NUM_DOTS = 16
private const val DOT_PERIOD = 10000
private const val WAVE_PERIOD = DOT_PERIOD / (8 * Math.PI)

@Composable
fun RingOfCircles(modifier: Modifier = Modifier) {
    val darkColor = if (isSystemInDarkTheme()) Color.White else Color.Black
    val lightColor = if (isSystemInDarkTheme()) Color.Black else Color.White

    val state = animationTimeMillis()
    Canvas(modifier = modifier) {
        val millis = state.value
        val (width, height) = size
        val ringRadius = min(width, height) * 0.35f
        val waveRadius = min(width, height) * 0.10f
        val dotRadius = waveRadius / 4f
        val dotGap = dotRadius / 2f

        // Draw the dots below the ring.
        for (i in 0..NUM_DOTS) {
            drawDot(i, millis, true, ringRadius, waveRadius, dotRadius, dotGap, darkColor, lightColor)
        }

        // Draw the ring.
        drawCircle(lightColor, radius = ringRadius, style = Stroke(dotRadius + dotGap * 2))
        drawCircle(darkColor, radius = ringRadius, style = Stroke(dotRadius))

        // Draw the dots above the ring.
        for (i in 0..NUM_DOTS) {
            drawDot(i, millis, false, ringRadius, waveRadius, dotRadius, dotGap, darkColor, lightColor)
        }
    }
}

private fun DrawScope.drawDot(
    index: Int,
    millis: Long,
    below: Boolean,
    ringRadius: Float,
    waveRadius: Float,
    dotRadius: Float,
    dotGap: Float,
    ringColor: Color,
    outlineColor: Color,
) {
    val dotAngle = (index / NUM_DOTS.toFloat() + (millis / -DOT_PERIOD)) % 1f * TWO_PI
    val waveAngle = (dotAngle + (millis / -WAVE_PERIOD)) % TWO_PI

    if (cos(waveAngle) > 0 == below) {
        return
    }

    withTransform({
        rotate(dotAngle.toDegrees())
        translate((ringRadius + sin(waveAngle) * waveRadius).toFloat(), 0f)
    }, {
        drawCircle(outlineColor, radius = dotRadius, style = Stroke(dotGap * 2))
        drawCircle(ringColor, radius = dotRadius)
    })
}