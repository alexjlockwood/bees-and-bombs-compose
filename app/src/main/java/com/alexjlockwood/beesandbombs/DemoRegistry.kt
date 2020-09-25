package com.alexjlockwood.beesandbombs

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.alexjlockwood.beesandbombs.demos.*

val DemoRegistry = mapOf<String, @Composable (modifier: Modifier) -> Unit>(
    "AnimalMorph" to { modifier -> AnimalMorph(modifier) },
    "CatmullRomCurves" to { modifier -> CatmullRomCurves(modifier) },
    "CircleSquare" to { modifier -> CircleSquare(modifier) },
    "CircleWave" to { modifier -> CircleWave(modifier) },
    "CircularProgressIndicator" to { modifier -> CircularProgressIndicator(modifier) },
    "LinearProgressIndicator" to { modifier -> LinearProgressIndicator(modifier) },
    "PlayingWithPaths" to { modifier -> PlayingWithPaths(modifier) },
    "RainbowWorm" to { modifier -> RainbowWorm(modifier) },
    "RingOfCircles" to { modifier -> RingOfCircles(modifier) },
    "RotatingGlobe" to { modifier -> RotatingGlobe(modifier) },
    "TickerWave" to { modifier -> TickerWave(modifier) },
    "SquareTwist" to { modifier -> SquareTwist(modifier) },
    "WaveSpiral" to { modifier -> WaveSpiral(modifier) },
    "WaveSquare" to { modifier -> WaveSquare(modifier) },
)
