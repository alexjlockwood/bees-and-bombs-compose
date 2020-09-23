package com.alexjlockwood.beesandbombs

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.alexjlockwood.beesandbombs.demos.*

class Demo(val content: @Composable (modifier: Modifier) -> Unit)

val DemoRegistry = mapOf(
    "AnimalMorph" to Demo { modifier -> AnimalMorph(modifier) },
    "CatmullRomCurves" to Demo { modifier -> CatmullRomCurves(modifier) },
    "CircleSquare" to Demo { modifier -> CircleSquare(modifier) },
    "CircleWave" to Demo { modifier -> CircleWave(modifier) },
    "CircularProgressIndicator" to Demo { modifier -> CircularProgressIndicator(modifier) },
    "LinearProgressIndicator" to Demo { modifier -> LinearProgressIndicator(modifier) },
    "PlayingWithPaths" to Demo { modifier -> PlayingWithPaths(modifier) },
    "RainbowWorm" to Demo { modifier -> RainbowWorm(modifier) },
    "RingOfCircles" to Demo { modifier -> RingOfCircles(modifier) },
    "RotatingGlobe" to Demo { modifier -> RotatingGlobe(modifier) },
    "TickerWave" to Demo { modifier -> TickerWave(modifier) },
    "SquareTwist" to Demo { modifier -> SquareTwist(modifier) },
    "WaveSpiral" to Demo { modifier -> WaveSpiral(modifier) },
    "WaveSquare" to Demo { modifier -> WaveSquare(modifier) },
)
