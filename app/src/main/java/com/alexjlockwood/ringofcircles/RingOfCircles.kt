package com.alexjlockwood.ringofcircles

import androidx.compose.*
import androidx.lifecycle.whenStarted
import androidx.ui.core.LifecycleOwnerAmbient
import androidx.ui.core.Modifier
import androidx.ui.foundation.Canvas
import androidx.ui.graphics.Color
import androidx.ui.graphics.drawscope.DrawScope
import androidx.ui.graphics.drawscope.Stroke
import androidx.ui.graphics.drawscope.withTransform
import androidx.ui.layout.fillMaxSize
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

private const val NUM_DOTS = 16
private const val DOT_PERIOD = 10000.0
private const val WAVE_PERIOD = DOT_PERIOD / (8 * Math.PI)

/**
 * Creates a composable ring-of-circles animation.
 *
 * https://twitter.com/alexjlockwood/status/1269417448651886592
 */
@Composable
fun RingOfCircles() {
    val state = animationTimeMillis()
    Canvas(modifier = Modifier.fillMaxSize()) {
        val millis = state.value
        val width = size.width
        val height = size.height
        val ringRadius = min(width, height) * 0.35f
        val waveRadius = min(width, height) * 0.10f
        val dotRadius = waveRadius / 4f
        val dotGap = dotRadius / 2f

        // Draw the dots below the ring.
        for (i in 0..NUM_DOTS) {
            drawDot(i, millis, true, ringRadius, waveRadius, dotRadius, dotGap)
        }

        // Draw the ring.
        drawCircle(Color.White, radius = ringRadius, style = Stroke(dotRadius + dotGap * 2))
        drawCircle(Color.Black, radius = ringRadius, style = Stroke(dotRadius))

        // Draw the dots above the ring.
        for (i in 0..NUM_DOTS) {
            drawDot(i, millis, false, ringRadius, waveRadius, dotRadius, dotGap)
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
    dotGap: Float
) {
    val dotAngle = (index / NUM_DOTS.toDouble() + (millis / -DOT_PERIOD)) % 1.0 * (2 * Math.PI)
    val waveAngle = (dotAngle + (millis / -WAVE_PERIOD)) % (2 * Math.PI)

    if (cos(waveAngle) > 0 == below) {
        return
    }

    withTransform({
        rotate(Math.toDegrees(dotAngle).toFloat())
        translate((ringRadius + sin(waveAngle) * waveRadius).toFloat(), 0f)
    }, {
        drawCircle(Color.White, radius = dotRadius, style = Stroke(dotGap * 2))
        drawCircle(Color.Black, radius = dotRadius)
    })
}

/**
 * Returns a [State] holding a local animation time in milliseconds. The value always starts
 * at `0L` and stops updating when the call leaves the composition.
 */
@Composable
private fun animationTimeMillis(): State<Long> {
    val millisState = mutableStateOf(0L)
    val lifecycleOwner = LifecycleOwnerAmbient.current
    launchInComposition {
        val startTime = awaitFrameMillis { it }
        lifecycleOwner.whenStarted {
            while (true) {
                awaitFrameMillis { millisState.value = it - startTime }
            }
        }
    }
    return millisState
}