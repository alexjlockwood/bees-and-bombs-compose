package com.alexjlockwood.beesandbombs.demos.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.dispatch.withFrameMillis
import androidx.compose.runtime.launchInComposition
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LifecycleOwnerAmbient
import androidx.lifecycle.whenStarted

/**
 * Returns a [State] holding a local animation time in milliseconds. The value always starts
 * at `0L` and stops updating when the call leaves the composition.
 */
@Composable
fun animationTimeMillis(): State<Long> {
    val millisState = mutableStateOf(0L)
    val lifecycleOwner = LifecycleOwnerAmbient.current
    launchInComposition {
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

