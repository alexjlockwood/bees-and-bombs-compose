package com.alexjlockwood.beesandbombs

import android.graphics.PointF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.pow
import kotlin.math.sqrt

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

private class CatmullRom(
    private val alpha: Float = 0.5f,
) {
    val path = Path()

    private var x0 = 0f
    private var y0 = 0f
    private var x1 = 0f
    private var y1 = 0f
    private var x2 = 0f
    private var y2 = 0f
    private var l01_a = 0f
    private var l12_a = 0f
    private var l23_a = 0f
    private var l01_2a = 0f
    private var l12_2a = 0f
    private var l23_2a = 0f
    private var numPoints = 0

    fun lineStart() {
        path.reset()
        x0 = 0f
        y0 = 0f
        x1 = 0f
        y1 = 0f
        x2 = 0f
        y2 = 0f
        l01_a = 0f
        l12_a = 0f
        l23_a = 0f
        l01_2a = 0f
        l12_2a = 0f
        l23_2a = 0f
        numPoints = 0
    }

    fun lineEnd() {
        when (numPoints) {
            2 -> path.lineTo(x2, y2)
            3 -> point(x2, y2)
        }
    }

    fun point(x: Float, y: Float) {
        if (numPoints != 0) {
            val x23 = x2 - x
            val y23 = y2 - y
            l23_2a = (x23 * x23 + y23 * y23).pow(alpha)
            l23_a = sqrt(l23_2a)
        }

        if (numPoints == 0) {
            path.moveTo(x, y)
        } else if (numPoints > 1) {
            pointInternal(x, y)
        }

        l01_a = l12_a
        l12_a = l23_a
        l01_2a = l12_2a
        l12_2a = l23_2a
        x0 = x1
        x1 = x2
        x2 = x
        y0 = y1
        y1 = y2
        y2 = y

        numPoints++
    }

    private fun pointInternal(x: Float, y: Float) {
        var x1 = this.x1
        var y1 = this.y1
        var x2 = this.x2
        var y2 = this.y2

        if (this.l01_a > 0) {
            val a = 2 * this.l01_2a + 3 * this.l01_a * this.l12_a + this.l12_2a
            val n = 3 * this.l01_a * (this.l01_a + this.l12_a)
            x1 = (x1 * a - this.x0 * this.l12_2a + this.x2 * this.l01_2a) / n
            y1 = (y1 * a - this.y0 * this.l12_2a + this.y2 * this.l01_2a) / n
        }

        if (this.l23_a > 0) {
            val b = 2 * this.l23_2a + 3 * this.l23_a * this.l12_a + this.l12_2a
            val m = 3 * this.l23_a * (this.l23_a + this.l12_a)
            x2 = (x2 * b + this.x1 * this.l23_2a - x * this.l12_2a) / m
            y2 = (y2 * b + this.y1 * this.l23_2a - y * this.l12_2a) / m
        }

        path.cubicTo(x1, y1, x2, y2, this.x2, this.y2)
    }
}