package com.alexjlockwood.beesandbombs.demos

import androidx.compose.animation.core.FloatPropKey
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.transitionDefinition
import androidx.compose.animation.core.tween
import androidx.compose.animation.transition
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.Group
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.alexjlockwood.beesandbombs.demos.utils.PathEasing

@Composable
fun CircularProgressIndicator(modifier: Modifier = Modifier) {
    val darkColor = if (isSystemInDarkTheme()) Color.White else Color.Black

    val state = transition(
        definition = CircularProgressIndicatorTransition,
        initState = 0,
        toState = 1,
    )
    Image(
        painter = rememberVectorPainter(
            defaultWidth = 48.dp,
            defaultHeight = 48.dp,
            viewportWidth = 48f,
            viewportHeight = 48f,
        ) { _, _ ->
            Group(
                translationX = 24f,
                translationY = 24f,
                rotation = state[RotationProp],
            ) {
                Path(
                    pathData = remember { addPathNodes("m 0 -18 a 18 18 0 1 1 0 36 a 18 18 0 1 1 0 -36") },
                    stroke = SolidColor(darkColor),
                    strokeLineCap = StrokeCap.Square,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineWidth = 4f,
                    trimPathStart = state[TrimPathStartProp],
                    trimPathEnd = state[TrimPathEndProp],
                    trimPathOffset = state[TrimPathOffsetProp],
                )
            }
        },
        modifier = modifier,
    )
}

private val TrimPathStartProp = FloatPropKey()
private val TrimPathEndProp = FloatPropKey()
private val TrimPathOffsetProp = FloatPropKey()
private val RotationProp = FloatPropKey()

private val TrimPathStartEasing = PathEasing("M 0 0 L 0.5 0 C 0.7 0 0.6 1 1 1")
private val TrimPathEndEasing = PathEasing("M 0 0 C 0.2 0 0.1 1 0.5 0.96 C 0.96 0.96 1 1 1 1")

private val CircularProgressIndicatorTransition = transitionDefinition<Int> {
    state(0) {
        this[TrimPathStartProp] = 0f
        this[TrimPathEndProp] = 0.03f
        this[TrimPathOffsetProp] = 0f
        this[RotationProp] = 0f
    }

    state(1) {
        this[TrimPathStartProp] = 0.75f
        this[TrimPathEndProp] = 0.78f
        this[TrimPathOffsetProp] = 0.25f
        this[RotationProp] = 720f
    }

    transition(fromState = 0, toState = 1) {
        TrimPathStartProp using infiniteRepeatable(
            animation = tween(durationMillis = 1333, easing = TrimPathStartEasing)
        )
        TrimPathEndProp using infiniteRepeatable(
            animation = tween(durationMillis = 1333, easing = TrimPathEndEasing)
        )
        TrimPathOffsetProp using infiniteRepeatable(
            animation = tween(durationMillis = 1333, easing = LinearEasing)
        )
        RotationProp using infiniteRepeatable(
            animation = tween(durationMillis = 4444, easing = LinearEasing)
        )
    }
}