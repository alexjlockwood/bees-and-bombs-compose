package com.alexjlockwood.beesandbombs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Box
import androidx.compose.foundation.ContentGravity
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.unit.dp

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Box(modifier = Modifier.fillMaxSize(), gravity = ContentGravity.Center) {
                val modifier = Modifier.aspectRatio(1f).fillMaxSize().padding(16.dp)

                // TODO: implement a screen-based navigation system (for now just uncomment the animation you want to show)
                //AnimalMorph(modifier)
                //CircleSquare(modifier)
                //CircleWave(modifier)
                //CircularProgressIndicator(modifier)
                //PlayingWithPaths(modifier)
                //RingOfCircles(modifier)
                //RotatingGlobe(modifier)
                SquareTwist(modifier)
                //TickerWave(modifier)
                //WaveSpiral(modifier)
                //WaveSquare(modifier)
            }
        }
    }
}
