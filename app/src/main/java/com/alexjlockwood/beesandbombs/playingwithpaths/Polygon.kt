package com.alexjlockwood.beesandbombs.playingwithpaths

import android.graphics.Path
import android.graphics.PointF
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.graphics.vector.PathNode
import kotlin.math.cos
import kotlin.math.sin

/**
 * A helper class that contains information about each polygon's drawing commands and a
 * [getPointAlongPath] method that supports animating motion along the polygon path.
 */
class Polygon(val color: Color, sides: Int, radius: Float, laps: Int) {
    /** The list of path nodes to use to draw the polygon path. */
    val pathNodes: List<PathNode>

    /** A precomputed lookup table that will be used to animate motion along the polygon path. */
    private val pointsAlongPath: List<PointAlongPath>

    init {
        val polygonPoints = createPolygonPoints(sides, radius)
        pathNodes = createPolygonPathNodes(polygonPoints)
        val polygonDotPath = createPolygonDotPath(polygonPoints, laps)
        pointsAlongPath = createPointsAlongPath(polygonDotPath)
    }

    /**
     * Returns the [PointF] along the polygon path given a fraction in the interval [0,1].
     * This is used to translate the black dot's location along each polygon path throughout
     * the animation.
     */
    fun getPointAlongPath(fraction: Float): PointF {
        if (fraction <= 0f) return pointsAlongPath.first().point
        if (fraction >= 1f) return pointsAlongPath.last().point

        // Binary search for the correct path point.
        var low = 0
        var high = pointsAlongPath.size - 1
        while (low <= high) {
            val mid = (low + high) / 2
            val midFraction = pointsAlongPath[mid].fraction
            when {
                fraction < midFraction -> high = mid - 1
                fraction > midFraction -> low = mid + 1
                else -> return pointsAlongPath[mid].point
            }
        }

        // Now high is below the fraction and low is above the fraction.
        val start = pointsAlongPath[high]
        val end = pointsAlongPath[low]
        val intervalFraction = (fraction - start.fraction) / (end.fraction - start.fraction)
        return lerp(start.point, end.point, intervalFraction)
    }
}

/**
 * Creates a list of points describing the coordinates of a polygon with the given
 * number of [sides] and [radius].
 */
private fun createPolygonPoints(sides: Int, radius: Float): List<PointF> {
    val startAngle = (3 * Math.PI / 2).toFloat()
    val angleIncrement = (2 * Math.PI / sides).toFloat()
    return (0..sides).map {
        val theta = startAngle + angleIncrement * it
        PointF(
            ViewportWidth / 2 + (radius * cos(theta)),
            ViewportHeight / 2 + (radius * sin(theta)),
        )
    }
}

/** Creates a list of [PathNode] drawing commands for the given list of [points]. */
private fun createPolygonPathNodes(points: List<PointF>): List<PathNode> {
    return points.mapIndexed { index, it ->
        when (index) {
            0 -> PathNode.MoveTo(it.x, it.y)
            else -> PathNode.LineTo(it.x, it.y)
        }
    }
}

/**
 * Container class that holds the location of a [point] at the given
 * [fraction] along a stroked path.
 */
private data class PointAlongPath(val fraction: Float, val point: PointF)

/**
 * Creates a [Path] given a polygon's [points] and the number of [laps] its
 * corresponding dot should travel during the animation.
 */
private fun createPolygonDotPath(points: List<PointF>, laps: Int): Path {
    return Path().apply {
        for (i in 0 until laps) {
            points.forEachIndexed { index, it ->
                when (index) {
                    0 -> moveTo(it.x, it.y)
                    else -> lineTo(it.x, it.y)
                }
            }
        }
    }
}

/** Creates a lookup table that can be used to animate motion along a path. */
private fun createPointsAlongPath(path: Path): List<PointAlongPath> {
    // Note: see j.mp/path-approximate-compat to backport this call for pre-O devices
    val approximatedPath = approximate(path, 0.5f)
    val pointsAlongPath = mutableListOf<PointAlongPath>()
    for (i in approximatedPath.indices step 3) {
        val fraction = approximatedPath[i]
        val point = PointF(approximatedPath[i + 1], approximatedPath[i + 2])
        pointsAlongPath.add(PointAlongPath(fraction, point))
    }
    return pointsAlongPath
}

private fun lerp(start: PointF, end: PointF, fraction: Float): PointF {
    return PointF(
        com.alexjlockwood.beesandbombs.lerp(start.x, end.x, fraction),
        com.alexjlockwood.beesandbombs.lerp(start.y, end.y, fraction)
    )
}
