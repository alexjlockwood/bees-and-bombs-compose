package com.alexjlockwood.beesandbombs.demos

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alexjlockwood.beesandbombs.R
import com.alexjlockwood.beesandbombs.demos.utils.ease
import com.alexjlockwood.beesandbombs.demos.utils.lerp

@Composable
fun AnimalMorph(modifier: Modifier = Modifier) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2500, easing = LinearEasing),
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
        painter = rememberVectorPainter(
            defaultWidth = 409.6.dp,
            defaultHeight = 280.6.dp,
            viewportWidth = 409.6f,
            viewportHeight = 280.6f,
            autoMirror = false,
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
        contentDescription = null,
        modifier = modifier,
    )
}
