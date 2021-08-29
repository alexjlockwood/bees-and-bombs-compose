package com.alexjlockwood.beesandbombs

import androidx.activity.OnBackPressedDispatcher
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier

private val DarkColorPalette = darkColors()
private val LightColorPalette = lightColors()

@Composable
fun BeesAndBombsApp(backDispatcher: OnBackPressedDispatcher) {
    val navigator: Navigator<DemoScreen> = rememberSaveable(saver = Navigator.saver(backDispatcher)) {
        Navigator(DemoScreen.DemoList, backDispatcher)
    }
    MaterialTheme(colors = if (isSystemInDarkTheme()) DarkColorPalette else LightColorPalette) {
        Surface(modifier = Modifier.fillMaxSize()) {
            val demoListState = rememberLazyListState()
            Crossfade(navigator.current) { destination ->
                when (destination) {
                    DemoScreen.DemoList -> DemoList(demoListState) { title -> navigator.navigate(DemoScreen.DemoDetails(title)) }
                    is DemoScreen.DemoDetails -> DemoDetails(destination)
                }
            }
        }
    }
}
