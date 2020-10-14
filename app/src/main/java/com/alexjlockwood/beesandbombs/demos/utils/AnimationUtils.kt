package com.alexjlockwood.beesandbombs.demos.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedTask
import androidx.compose.runtime.State
import androidx.compose.runtime.dispatch.withFrameMillis
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LifecycleOwnerAmbient
import androidx.lifecycle.whenStarted
import kotlin.math.pow
import kotlin.math.sin

/**
 * Returns a [State] holding a local animation time in milliseconds. The value always starts
 * at `0L` and stops updating when the call leaves the composition.
 */
@Composable
fun animationTimeMillis(): State<Long> {
    val millisState = mutableStateOf(0L)
    val lifecycleOwner = LifecycleOwnerAmbient.current
    LaunchedTask {
        val startTime = withFrameMillis { it }
        lifecycleOwner.whenStarted {
            while (true) {
                withFrameMillis { frameTime ->
                    millisState.value = frameTime - startTime
                }
            }
        }
    }
    return millisState
}

/**
 * Easing function that interpolates a rainbow through RGB space.
 */
fun sinebow(t: Float): Color {
    return Color(
        red = sin(PI * (t + 0f / 3f)).pow(2),
        green = sin(PI * (t + 1f / 3f)).pow(2),
        blue = sin(PI * (t + 2f / 3f)).pow(2),
    )
}
