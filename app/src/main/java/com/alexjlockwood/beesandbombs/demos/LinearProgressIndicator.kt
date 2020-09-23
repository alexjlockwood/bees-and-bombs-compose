package com.alexjlockwood.beesandbombs.demos

import androidx.compose.animation.animatedFloat
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.onActive
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.Group
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import com.alexjlockwood.beesandbombs.demos.utils.PathEasing
import com.alexjlockwood.beesandbombs.demos.utils.PathKeyframeSet

@Composable
fun LinearProgressIndicator(modifier: Modifier = Modifier) {
    val animatedProgress = animatedFloat(0f)
    onActive {
        animatedProgress.animateTo(
            targetValue = 1f,
            anim = repeatable(
                iterations = AnimationConstants.Infinite,
                animation = tween(durationMillis = 2000, easing = LinearEasing),
            ),
        )
    }

    val scaleKeyframeSet1 = remember { PathKeyframeSet("M 0 0.1 L 1 0.571 L 2 0.91 L 3 0.1") }
    val translateKeyframeSet1 =
        remember { PathKeyframeSet("M -197.6 0 C -183.318 0 -112.522 0 -62.053 0 C -7.791 0 28.371 0 106.19 0 C 250.912 0 422.6 0 422.6 0") }
    val scaleKeyframeSet2 = remember { PathKeyframeSet("M 0 0.1 L 1 0.826 L 2 0.1") }
    val translateKeyframeSet2 = remember { PathKeyframeSet("M -522.6 0 C -473.7 0 -356.573 0 -221.383 0 C -23.801 0 199.6 0 199.6 0") }
    val scaleEasing1 =
        remember { PathEasing("M 0 0 C 0.068 0.02 0.192 0.159 0.333 0.349 C 0.384 0.415 0.549 0.681 0.667 0.683 C 0.753 0.682 0.737 0.879 1 1") }
    val translateEasing1 =
        remember { PathEasing("M 0 0 C 0.037 0 0.129 0.09 0.25 0.219 C 0.322 0.296 0.437 0.418 0.483 0.49 C 0.69 0.81 0.793 0.95 1 1") }
    val scaleEasing2 = remember { PathEasing("M 0 0 L 0.366 0 C 0.473 0.062 0.615 0.5 0.683 0.5 C 0.755 0.5 0.757 0.815 1 1") }
    val translateEasing2 = remember { PathEasing("M 0 0 L 0.2 0 C 0.395 0 0.474 0.206 0.591 0.417 C 0.715 0.639 0.816 0.974 1 1") }

    val t = animatedProgress.value
    val scale1 = scaleKeyframeSet1.getPointAlongPath(scaleEasing1(t)).y
    val translate1 = translateKeyframeSet1.getPointAlongPath(translateEasing1(t)).x
    val scale2 = scaleKeyframeSet2.getPointAlongPath(scaleEasing2(t)).y
    val translate2 = translateKeyframeSet2.getPointAlongPath(translateEasing2(t)).x

    val darkColor = if (isSystemInDarkTheme()) Color.White else Color.Black
    Image(
        painter = VectorPainter(
            defaultWidth = 360.dp,
            defaultHeight = 10.dp,
            viewportWidth = 360f,
            viewportHeight = 10f,
        ) { _, _ ->
            Group(
                translationX = 180f,
                translationY = 5f,
            ) {
                Path(
                    pathData = remember { addPathNodes("M -180 -1 l 360 0 l 0 2 l -360 0 Z") },
                    fill = SolidColor(darkColor),
                    fillAlpha = 0.3f,
                )

                Group(
                    scaleX = scale1,
                    translationX = translate1,
                ) {
                    Path(
                        pathData = remember { addPathNodes("M -144 -1 l 288 0 l 0 2 l -288 0 Z") },
                        fill = SolidColor(darkColor),
                    )
                }

                Group(
                    scaleX = scale2,
                    translationX = translate2,
                ) {
                    Path(
                        pathData = remember { addPathNodes("M -144 -1 l 288 0 l 0 2 l -288 0 Z") },
                        fill = SolidColor(darkColor),
                    )
                }
            }
        },
        modifier = modifier,
    )
}
