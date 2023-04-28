package com.alexjlockwood.beesandbombs.demos

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp
import com.alexjlockwood.beesandbombs.demos.utils.Point
import com.alexjlockwood.beesandbombs.demos.utils.ease
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun SlidingCubes(modifier: Modifier = Modifier) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 3500, easing = LinearEasing),
            ),
        )
    }

    val helper = remember {
        IsometricProjectionHelper().apply {
            scale(120f, 120f, 120f)
        }
    }

    val fillColor = if (isSystemInDarkTheme()) Color.Black else Color.White
    val strokeColor = if (isSystemInDarkTheme()) Color.White else Color.Black

    Canvas(modifier.clipToBounds()) {
        val (width, height) = size

        helper.save()
        translate(width / 2f, height / 2f) {
            for (x in NumCubes downTo -NumCubes) {
                for (y in NumCubes downTo -NumCubes) {
                    val d = distanceManhattan(x, y)
                    if (d > 10) {
                        continue
                    }

                    val t = ease((animatedProgress.value * 3.5f - distanceCartesian(x, y) / 4).coerceIn(0f, 1f))
                    drawCube(
                        helper = helper,
                        fillColor = fillColor,
                        strokeColor = strokeColor,
                        angle = (if (d % 2 == 0) -1 else 1) * (PI / 4 - t * PI / 2),
                        x = x * 2f,
                        y = y * 2f,
                        z = 2 * t,
                    )
                }
            }
        }
        helper.restore()

        drawRect(color = strokeColor, style = Stroke(BorderStrokeWidth.toPx()))
    }
}

private fun DrawScope.drawCube(
    helper: IsometricProjectionHelper,
    fillColor: Color,
    strokeColor: Color,
    angle: Float,
    x: Float,
    y: Float,
    z: Float,
) {
    var adjustedAngle = angle % (PI / 2)
    if (adjustedAngle < 0) {
        adjustedAngle += PI / 2
    }

    helper.save()
    helper.translate(x, y, z)
    helper.rotateZ(adjustedAngle - PI / 4)

    val path = Path()
    path.moveTo(helper.transform(0.5f, -0.5f, 0.5f))
    path.lineTo(helper.transform(0.5f, 0.5f, 0.5f))
    path.lineTo(helper.transform(-0.5f, 0.5f, 0.5f))
    path.lineTo(helper.transform(-0.5f, 0.5f, -0.5f))
    path.lineTo(helper.transform(-0.5f, -0.5f, -0.5f))
    path.lineTo(helper.transform(0.5f, -0.5f, -0.5f))
    path.close()

    drawPath(
        path = path,
        color = fillColor,
    )

    drawPath(
        path = path,
        color = strokeColor,
        style = Stroke(width = CubeStrokeWidth.toPx()),
    )

    path.reset()
    path.moveTo(helper.transform(-0.5f, -0.5f, 0.5f))
    path.lineTo(helper.transform(0.5f, -0.5f, 0.5f))
    path.moveTo(helper.transform(-0.5f, -0.5f, 0.5f))
    path.lineTo(helper.transform(-0.5f, 0.5f, 0.5f))
    path.moveTo(helper.transform(-0.5f, -0.5f, 0.5f))
    path.lineTo(helper.transform(-0.5f, -0.5f, -0.5f))

    drawPath(
        path = path,
        color = strokeColor,
        style = Stroke(width = CubeStrokeWidth.toPx() / 2),
    )

    helper.restore()
}

private fun Path.moveTo(p: Point) = moveTo(p.x, p.y)

private fun Path.lineTo(p: Point) = lineTo(p.x, p.y)

private fun distanceCartesian(x: Int, y: Int) = sqrt((x * x + y * y).toFloat())

private fun distanceManhattan(x: Int, y: Int) = abs(x) + abs(y)

private const val NumCubes = 14
private val CubeStrokeWidth = 2.5.dp
private val BorderStrokeWidth = 12.dp

/**
 * Helper class that handles the transformation and projection of 3D points
 * to a 2D plane.
 */
private class IsometricProjectionHelper {

    private var matrix = Matrix()
    private val matrices: MutableList<Matrix> = mutableListOf()

    /**
     * Saves the current transformation matrix to the stack.
     */
    fun save() {
        matrices.add(matrix.copy())
    }

    /**
     * Pops the current transformation matrix from the stack.
     */
    fun restore() {
        matrix = matrices.removeLast()
    }

    /**
     * Scales the current transformation matrix in 3D space.
     *
     * | a b c d |   | kx  0  0 0 |   | a * kx b * ky c * kz d |
     * | e f g h | * |  0 ky  0 0 | = | e * kx f * ky g * kz h |
     * | i j k l |   |  0  0 kz 0 |   | i * kx j * ky k * kz l |
     * | 0 0 0 1 |   |  0  0  0 1 |   |      0      0      0 1 |
     */
    fun scale(kx: Float, ky: Float, kz: Float) {
        matrix.a *= kx
        matrix.b *= ky
        matrix.c *= kz
        matrix.e *= kx
        matrix.f *= ky
        matrix.g *= kz
        matrix.i *= kx
        matrix.j *= ky
        matrix.k *= kz
    }

    /**
     * Rotates the current transformation matrix along the Z axis in 3D space.
     *
     * | a b c d |   | cos -sin 0 0 |   | a * cos + b * sin a * -sin + b * cos c d |
     * | e f g h | * | sin  cos 0 0 | = | e * cos + f * sin e * -sin + f * cos g h |
     * | i j k l |   |   0    0 1 0 |   | i * cos + j * sin i * -sin + j * cos k l |
     * | 0 0 0 1 |   |   0    0 0 1 |   |                 0                  0 0 1 |
     */
    fun rotateZ(angle: Float) {
        val cos = cos(angle)
        val sin = sin(angle)
        val a = matrix.a
        val b = matrix.b
        val e = matrix.e
        val f = matrix.f
        val i = matrix.i
        val j = matrix.j
        matrix.a = a * cos + b * sin
        matrix.b = a * -sin + b * cos
        matrix.e = e * cos + f * sin
        matrix.f = e * -sin + f * cos
        matrix.i = i * cos + j * sin
        matrix.j = i * -sin + j * cos
    }

    /**
     * Translates the current transformation matrix in 3D space.
     *
     * | a b c d |   | 1 0 0 tx |   | a b c a * tx + b * ty + c * tz + d |
     * | e f g h | * | 0 1 0 ty | = | e f g e * tx + f * ty + g * tz + h |
     * | i j k l |   | 0 0 1 tz |   | i j k i * tx + j * ty + k * tz + l |
     * | 0 0 0 1 |   | 0 0 0  1 |   | 0 0 0                            1 |
     */
    fun translate(tx: Float, ty: Float, tz: Float) {
        matrix.d += matrix.a * tx + matrix.b * ty + matrix.c * tz
        matrix.h += matrix.e * tx + matrix.f * ty + matrix.g * tz
        matrix.l += matrix.i * tx + matrix.j * ty + matrix.k * tz
    }

    /**
     * Returns a transformed 2D point using the current transformation matrix.
     */
    fun transform(x: Float, y: Float, z: Float): Point {
        return project(
            x = x * matrix.a + y * matrix.b + z * matrix.c + matrix.d,
            y = x * matrix.e + y * matrix.f + z * matrix.g + matrix.h,
            z = x * matrix.i + y * matrix.j + z * matrix.k + matrix.l,
        )
    }

    private fun project(x: Float, y: Float, z: Float): Point {
        return Point(
            x = x * cos(PI / 6) + y * cos(PI - PI / 6),
            y = x * -sin(PI / 6) + y * -sin(PI - PI / 6) - z,
        )
    }
}

/**
 * Representation of the following 4x4 matrix:
 *
 * | a b c d |
 * | e f g h |
 * | i j k l |
 * | 0 0 0 1 |
 */
private data class Matrix(
    var a: Float = 1f,
    var b: Float = 0f,
    var c: Float = 0f,
    var d: Float = 0f,
    var e: Float = 0f,
    var f: Float = 1f,
    var g: Float = 0f,
    var h: Float = 0f,
    var i: Float = 0f,
    var j: Float = 0f,
    var k: Float = 1f,
    var l: Float = 0f,
)

private const val PI = Math.PI.toFloat()
