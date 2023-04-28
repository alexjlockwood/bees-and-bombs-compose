package com.alexjlockwood.beesandbombs.demos

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.alexjlockwood.beesandbombs.demos.utils.Point
import com.alexjlockwood.beesandbombs.demos.utils.animationTimeMillis
import com.alexjlockwood.beesandbombs.demos.utils.sinebow
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

private const val N = 60

@Composable
fun RainbowWorm(modifier: Modifier = Modifier) {
    val millis by animationTimeMillis()
    val path = remember { Path() }

    Canvas(modifier = modifier) {
        drawRect(Color.White)

        val (width, height) = size
        val dt = millis * 1e-3f
        val padding = 48.dp.toPx()
        val x = (0 until N).map { padding + (width - 2 * padding) * it / N.toFloat() }
        val y = (0 until N).map { sin(it / 10f + dt) * height / 3f + height / 2f }
        val z = (0 until N).map { 64.dp.toPx() * sin(it / 10f + dt).pow(2) + 24.dp.toPx() }

        var p0: Point? = null
        var p1: Point? = Point(x[0], y[0])
        var p2: Point? = Point(x[1], y[1])
        var p3: Point? = Point(x[2], y[2])

        for (i in (3..N)) {
            path.reset()
            path.lineJoin(p0, p1!!, p2!!, p3, z[i - 1])
            drawPath(
                path = path,
                color = sinebow(i / N.toFloat()),
            )
            drawPath(
                path = path,
                color = Color.Black,
                style = Stroke(1.dp.toPx()),
            )
            p0 = p1
            p1 = p2
            p2 = p3
            p3 = if (i < N) Point(x[i], y[i]) else null
        }
    }
}

// Computes the stroke outline for segment p1 to p2.
private fun Path.lineJoin(p0: Point?, p1: Point, p2: Point, p3: Point?, width: Float) {
    val u12 = calcPerp(p1, p2)
    val r = width / 2
    var a = Point(p1.x + u12.x * r, p1.y + u12.y * r)
    var b = Point(p2.x + u12.x * r, p2.y + u12.y * r)
    var c = Point(p2.x - u12.x * r, p2.y - u12.y * r)
    var d = Point(p1.x - u12.x * r, p1.y - u12.y * r)

    // Clip line ad and line dc using the average of u01 and u12.
    if (p0 != null) {
        val u01 = calcPerp(p0, p1)
        val e = Point(p1.x + u01.x + u12.x, p1.y + u01.y + u12.y)
        a = calcLineIntersect(a, b, p1, e)
        d = calcLineIntersect(d, c, p1, e)
    }

    // Clip line ab and line dc using the average of u12 and u23.
    if (p3 != null) {
        val u23 = calcPerp(p2, p3)
        val e = Point(p2.x + u23.x + u12.x, p2.y + u23.y + u12.y)
        b = calcLineIntersect(a, b, p2, e)
        c = calcLineIntersect(d, c, p2, e)
    }

    moveTo(a.x, a.y)
    lineTo(b.x, b.y)
    lineTo(c.x, c.y)
    lineTo(d.x, d.y)
    close()
}

// Computes the intersection of two lines.
private fun calcLineIntersect(p1: Point, p2: Point, p3: Point, p4: Point): Point {
    val (x1, y1) = p1
    val (x2, y2) = p2
    val (x3, y3) = p3
    val (x4, y4) = p4
    val x13 = x1 - x3
    val x21 = x2 - x1
    val x43 = x4 - x3
    val y13 = y1 - y3
    val y21 = y2 - y1
    val y43 = y4 - y3
    val ua = (x43 * y13 - y43 * x13) / (y43 * x21 - x43 * y21)
    return Point(x1 + ua * x21, y1 + ua * y21)
}

// Computes the unit vector perpendicular to a line.
private fun calcPerp(p0: Point, p1: Point): Point {
    val (x0, y0) = p0
    val (x1, y1) = p1
    val y10 = y1 - y0
    val x10 = x1 - x0
    val len = sqrt(y10 * y10 + x10 * x10)
    return Point(-y10 / len, x10 / len)
}
