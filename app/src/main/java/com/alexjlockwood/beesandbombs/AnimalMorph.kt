package com.alexjlockwood.beesandbombs

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
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.graphics.vector.PathNode
import androidx.compose.ui.graphics.vector.PathNode.MoveTo
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

/**
 * WORK IN PROGRESS
 */
@Composable
fun AnimalMorph(modifier: Modifier = Modifier) {
    val animatedProgress = animatedFloat(0f)
    onActive {
        animatedProgress.animateTo(
            targetValue = 1f,
            anim = repeatable(
                iterations = AnimationConstants.Infinite,
                animation = tween(durationMillis = 2500, easing = LinearEasing),
            ),
        )
    }

    val hippoColor = Color(0xFF78909C)
    val elephantColor = Color(0xFFBDBDBD)
    val buffaloColor = Color(0xFF795548)

    val hippoPathData = stringResource(R.string.hippo)
    val elephantPathData = stringResource(R.string.elephant)
    val buffaloPathData = stringResource(R.string.buffalo)

    val hippoPathNodes = remember { addPathNodes(hippoPathData) }
    val elephantPathNodes = remember { addPathNodes(elephantPathData) }
    val buffaloPathNodes = remember { addPathNodes(buffaloPathData) }

    Image(
        painter = VectorPainter(
            defaultWidth = 409.6.dp,
            defaultHeight = 280.6.dp,
            viewportWidth = 409.6f,
            viewportHeight = 280.6f,
        ) { _, _ ->
            Path(
                pathData = lerp(hippoPathNodes, elephantPathNodes, animatedProgress.value),
                fill = SolidColor(hippoColor),
            )
        },
        modifier = modifier,
    )
}

private fun lerp(a: List<PathNode>, b: List<PathNode>, t: Float): List<PathNode> {
    return a.zip(b).map {
        val (first, second) = it
        if (first is MoveTo && second is MoveTo) {
            MoveTo(
                lerp(first.x, second.x, t),
                lerp(first.y, second.y, t),
            )
        } else if (first is PathNode.CurveTo && second is PathNode.CurveTo) {
            val curveTo = PathNode.CurveTo(
                lerp(first.x1, second.x1, t),
                lerp(first.y1, second.y1, t),
                lerp(first.x2, second.x2, t),
                lerp(first.y2, second.y2, t),
                lerp(first.x3, second.x3, t),
                lerp(first.y3, second.y3, t),
            )
            curveTo
        } else {
            throw IllegalStateException("Unsupported SVG PathNode command")
        }
    }
}