package com.alexjlockwood.beesandbombs.demos.utils

import kotlin.math.pow
import kotlin.math.sqrt

const val PI = Math.PI.toFloat()
const val TWO_PI = 2 * PI
const val HALF_PI = PI / 2

/**
 * Calculates the cartesian distance between two points.
 */
fun dist(x1: Float, y1: Float, x2: Float, y2: Float): Float {
    return sqrt(((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)))
}

/**
 * Calculates a number between two numbers at a specific increment.
 */
fun lerp(a: Float, b: Float, t: Float): Float {
    return a + (b - a) * t
}

/**
 * Re-maps a number from one range to another.
 */
fun map(value: Float, start1: Float, stop1: Float, start2: Float, stop2: Float): Float {
    return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1))
}

/**
 * Converts the angle measured in radians to an approximately equivalent angle measured in degrees.
 */
fun Float.toDegrees(): Float {
    return Math.toDegrees(toDouble()).toFloat()
}

fun ease(p: Float): Float {
    return 3 * p * p - 2 * p * p * p
}

fun ease(p: Float, g: Float): Float {
    return if (p < 0.5f) {
        0.5f * (2 * p).pow(g)
    } else {
        1 - 0.5f * (2 * (1 - p)).pow(g)
    }
}
