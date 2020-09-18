package com.alexjlockwood.beesandbombs.playingwithpaths

import androidx.compose.animation.animatedFloat
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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

/**
 * Creates a composable 'playing with paths' polygon animation.
 */
@Composable
fun PlayingWithPaths(modifier: Modifier = Modifier) {
    val animatedProgress = animatedFloat(0f)
    onActive {
        // Begin the animation as soon as the first composition is applied.
        animatedProgress.animateTo(
            targetValue = 1f,
            anim = repeatable(
                iterations = AnimationConstants.Infinite,
                animation = tween(durationMillis = 10000, easing = LinearEasing),
            ),
        )
    }

    val vectorPainter = VectorPainter(
        defaultWidth = 48.dp,
        defaultHeight = 48.dp,
        viewportWidth = ViewportWidth,
        viewportHeight = ViewportHeight,
    ) { vw, vh ->
        // Draw a white background rect.
        Path(
            pathData = remember { addPathNodes("h $vw v $vh h -$vw v -$vh") },
            fill = SolidColor(Color.White),
        )

        Polygons.forEach {
            // Create a colored stroke path for each polygon.
            Path(
                pathData = it.pathNodes,
                stroke = SolidColor(it.color),
                strokeLineWidth = 4f,
            )
        }
        // Memoize the path nodes to avoid parsing the SVG path data string on each animation frame.
        val dotPathNodes = remember { addPathNodes("m 0 -8 a 8 8 0 1 1 0 16 a 8 8 0 1 1 0 -16") }
        Polygons.forEach {
            // Draw a black, circular path for each dot and translate it
            // to its current animated location along the polygon path.
            val dotPoint = it.getPointAlongPath(animatedProgress.value)
            Group(
                translationX = dotPoint.x,
                translationY = dotPoint.y,
            ) {
                Path(
                    pathData = dotPathNodes,
                    fill = SolidColor(Color.Black),
                )
            }
        }
    }
    Image(
        painter = vectorPainter,
        modifier = modifier,
    )
}

// TODO: Should these be capitalized? Who knows... 🤷
internal const val ViewportWidth = 1080f
internal const val ViewportHeight = 1080f

private val Polygons = arrayOf(
    Polygon(Color(0xffe84c65), 15, 362f, 2),
    Polygon(Color(0xffe84c65), 14, 338f, 3),
    Polygon(Color(0xffd554d9), 13, 314f, 4),
    Polygon(Color(0xffaf6eee), 12, 292f, 5),
    Polygon(Color(0xff4a4ae6), 11, 268f, 6),
    Polygon(Color(0xff4294e7), 10, 244f, 7),
    Polygon(Color(0xff6beeee), 9, 220f, 8),
    Polygon(Color(0xff42e794), 8, 196f, 9),
    Polygon(Color(0xff5ae75a), 7, 172f, 10),
    Polygon(Color(0xffade76b), 6, 148f, 11),
    Polygon(Color(0xffefefbb), 5, 128f, 12),
    Polygon(Color(0xffe79442), 4, 106f, 13),
    Polygon(Color(0xffe84c65), 3, 90f, 14),
)

