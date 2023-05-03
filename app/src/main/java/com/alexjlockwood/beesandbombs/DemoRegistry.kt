package com.alexjlockwood.beesandbombs

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alexjlockwood.beesandbombs.demos.*

val DemoRegistry = mapOf<String, @Composable (modifier: Modifier) -> Unit>(
    "AnimalMorph" to { modifier -> AnimalMorph(modifier) },
    "CatmullRomCurves" to { modifier -> CatmullRomCurves(modifier) },
    "CircleSquare" to { modifier -> CircleSquare(modifier) },
    "CircleWave" to { modifier -> CircleWave(modifier) },
    "CircularProgressIndicator" to { modifier -> CircularProgressIndicator(modifier) },
    "HexZoom" to { modifier -> HexZoom(modifier) },
    "LinearProgressIndicator" to { modifier -> LinearProgressIndicator(modifier) },
    "MazeVisualization" to { MazeVisualization(Modifier.fillMaxSize()) },
    "PlayingWithPaths" to { modifier -> PlayingWithPaths(modifier) },
    "RainbowWorm" to { modifier -> RainbowWorm(modifier) },
    "RingOfCircles" to { modifier -> RingOfCircles(modifier) },
    "RotatingGlobe" to { modifier -> RotatingGlobe(modifier) },
    "TickerWave" to { modifier -> TickerWave(modifier) },
    "SlidingCubes" to { modifier -> SlidingCubes(modifier) },
    "SquareTwist" to { modifier -> SquareTwist(modifier) },
    "WaveSpiral" to { modifier -> WaveSpiral(modifier) },
    "WaveSquare" to { modifier -> WaveSquare(modifier) },
)
