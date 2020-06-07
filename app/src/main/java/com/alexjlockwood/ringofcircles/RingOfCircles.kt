package com.alexjlockwood.ringofcircles

import android.os.SystemClock
import android.view.Choreographer
import androidx.compose.Composable
import androidx.compose.MutableState
import androidx.compose.state
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

@Composable
fun RingOfCircles() {
    val state = state { SystemClock.elapsedRealtime() }
    startLooping(state)
    DrawContent(state)
}

@Composable
private fun DrawContent(state: MutableState<Long>) {
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
            drawDot(i, millis, false, ringRadius, waveRadius, dotRadius, dotGap)
        }

        // Draw the ring.
        drawCircle(color = Color.White, radius = ringRadius, style = Stroke(dotRadius + dotGap * 2))
        drawCircle(color = Color.Black, radius = ringRadius, style = Stroke(dotRadius))

        // Draw the dots above the ring.
        for (i in 0..NUM_DOTS) {
            drawDot(i, millis, true, ringRadius, waveRadius, dotRadius, dotGap)
        }
    }
}

private fun DrawScope.drawDot(
    index: Int,
    millis: Long,
    above: Boolean,
    ringRadius: Float,
    waveRadius: Float,
    dotRadius: Float,
    dotGap: Float
) {
    val dotAngle = (index / NUM_DOTS.toDouble() + (millis / -DOT_PERIOD)) % 1.0 * (2 * Math.PI)
    val waveAngle = (dotAngle + (millis / -WAVE_PERIOD)) % (2 * Math.PI)

    if (cos(waveAngle) < 0 == above) {
        return
    }

    withTransform({
        rotate(Math.toDegrees(dotAngle).toFloat())
        translate((ringRadius + sin(waveAngle) * waveRadius).toFloat(), 0f)
    }, {
        drawCircle(color = Color.White, radius = dotRadius, style = Stroke(width = dotGap * 2))
        drawCircle(color = Color.Black, radius = dotRadius)
    })
}

// TODO: figure out how to stop this loop when the window loses focus
private fun startLooping(state: MutableState<Long>) {
    Choreographer.getInstance().postFrameCallback(object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            state.value = SystemClock.elapsedRealtime()
            Choreographer.getInstance().postFrameCallback(this)
        }
    })
}
