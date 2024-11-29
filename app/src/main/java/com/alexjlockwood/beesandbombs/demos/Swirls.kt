package com.alexjlockwood.beesandbombs.demos

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alexjlockwood.beesandbombs.demos.utils.PI
import com.alexjlockwood.beesandbombs.demos.utils.Point
import com.alexjlockwood.beesandbombs.demos.utils.TWO_PI
import com.alexjlockwood.beesandbombs.demos.utils.map
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Swirls(modifier: Modifier = Modifier) {
    val animatedProgress = remember { Animatable(0f) }
    val t = animatedProgress.value

    LaunchedEffect(Unit) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 6000, easing = LinearEasing),
            ),
        )
    }

    Canvas(
        modifier = modifier
            .aspectRatio(1f)
            .clipToBounds(),
    ) {
        val (width, height) = size
        val N = (size.minDimension / 30f).toInt()
        val n = 30
        val swirlWidth = size.minDimension / 3f
        val swirlHeight = size.minDimension / 4f

        translate(width / 2, height / 2) {
            for (a in 0 until n) {
                var startPoint: Point? = null
                for (i in 0 until N) {
                    val qq = i / (N - 1f)
                    val th = PI * qq + TWO_PI * t + TWO_PI * a / n
                    val x = swirlWidth * (cos(th) + cos(3 * th) / 10f)
                    val y = swirlHeight * (sin(2 * th) / 3f + map(a.toFloat(), 0f, n - 1f, -1f, 1f))
                    val endPoint = Point(x, y)
                    if (startPoint != null) {
                        drawLine(
                            color = getColor(((t + 0.5f * qq + 0.5f * a / n) + 120) % 1),
                            start = Offset(startPoint.x, startPoint.y),
                            end = Offset(endPoint.x, endPoint.y),
                            strokeWidth = 1.dp.toPx(),
                        )
                    }
                    startPoint = endPoint
                }
            }
        }
    }
}

private fun getColor(q: Float): Color {
    return when {
        q < 0.25f -> lerp(Color1, Color2, map(q, 0f, 0.25f, 0f, 1f))
        q < 0.45f -> lerp(Color2, Color3, map(q, 0.25f, 0.45f, 0f, 1f))
        q < 0.75f -> lerp(Color3, Color4, map(q, 0.45f, 0.75f, 0f, 1f))
        else -> lerp(Color4, Color1, map(q, 0.75f, 1f, 0f, 1f))
    }
}

private val Color1 = Color(0xffd70441)
private val Color2 = Color(0xfff4e904)
private val Color3 = Color(0xff009978)
private val Color4 = Color(0xff5e3688)

@Preview
@Composable
private fun SwirlsPreview() {
    Swirls()
}
