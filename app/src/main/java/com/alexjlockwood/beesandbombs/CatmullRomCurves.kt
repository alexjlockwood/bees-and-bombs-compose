package com.alexjlockwood.beesandbombs

import android.graphics.PointF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.alexjlockwood.beesandbombs.utils.CatmullRom

@Composable
fun CatmullRomCurves(modifier: Modifier = Modifier) {
    val lines = remember {
        (0 until 10).map {
            (0 until 10).map {
                PointF(
                    (Math.random() * 0.8 + 0.1).toFloat(),
                    (Math.random() * 0.8 + 0.1).toFloat(),
                )
            }
        }
    }

    val darkColor = if (isSystemInDarkTheme()) Color.White else Color.Black
    val curve = remember { CatmullRom() }

    Canvas(modifier = modifier) {
        val (width, height) = size

        lines.forEach {
            curve.lineStart()
            it.forEach { p ->
                val x = p.x * width
                val y = p.y * height
                curve.point(x, y)
            }
            curve.lineEnd()

            drawPath(
                path = curve.path,
                color = darkColor,
                style = Stroke(1.dp.toPx()),
            )
        }

        drawRect(
            color = darkColor,
            style = Stroke(4.dp.toPx()),
        )
    }
}

