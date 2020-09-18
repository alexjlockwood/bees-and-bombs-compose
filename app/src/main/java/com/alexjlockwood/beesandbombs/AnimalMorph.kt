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
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.graphics.vector.PathNode
import androidx.compose.ui.graphics.vector.PathNode.MoveTo
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.res.colorResource
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
                animation = tween(durationMillis = 3000, easing = LinearEasing),
            ),
        )
    }

    val animalColors = listOf(
        colorResource(R.color.hippo),
        colorResource(R.color.elephant),
        colorResource(R.color.buffalo),
    )

    val hippoPathData = stringResource(R.string.hippo)
    val elephantPathData = stringResource(R.string.elephant)
    val buffaloPathData = stringResource(R.string.buffalo)
    val animalPathNodes = remember {
        listOf(
            addPathNodes(hippoPathData),
            addPathNodes(elephantPathData),
            addPathNodes(buffaloPathData),
        )
    }

    val t = animatedProgress.value
    val startIndex = (t * 3).toInt()
    val endIndex = (startIndex + 1) % animalPathNodes.size
    val tt = t * 3 - (if (t < 1f / 3f) 0 else if (t < 2f / 3f) 1 else 2)
    val color = lerp(animalColors[startIndex], animalColors[endIndex], ease(tt, 3f))
    val pathNodes = lerp(animalPathNodes[startIndex], animalPathNodes[endIndex], ease(tt, 3f))

    Image(
        painter = VectorPainter(
            defaultWidth = 409.6.dp,
            defaultHeight = 280.6.dp,
            viewportWidth = 409.6f,
            viewportHeight = 280.6f,
        ) { vw, vh ->
            // Draw a white background rect.
            Path(
                pathData = remember { addPathNodes("h $vw v $vh h -$vw v -$vh") },
                fill = SolidColor(Color.White),
            )
            // Draw the morphed animal path.
            Path(
                pathData = pathNodes,
                fill = SolidColor(color),
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
            PathNode.CurveTo(
                lerp(first.x1, second.x1, t),
                lerp(first.y1, second.y1, t),
                lerp(first.x2, second.x2, t),
                lerp(first.y2, second.y2, t),
                lerp(first.x3, second.x3, t),
                lerp(first.y3, second.y3, t),
            )
        } else {
            throw IllegalStateException("Unsupported SVG PathNode command")
        }
    }
}