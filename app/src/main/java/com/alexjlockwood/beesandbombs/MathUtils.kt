package com.alexjlockwood.beesandbombs

import kotlin.math.pow
import kotlin.math.sqrt

const val PI = Math.PI.toFloat()
const val TWO_PI = 2 * PI
const val HALF_PI = PI / 2

fun constrain(amount: Float, low: Float, high: Float): Float {
    return amount.coerceIn(low, high)
}

fun toDegrees(radians: Float): Float {
    return Math.toDegrees(radians.toDouble()).toFloat()
}

fun dist(x1: Float, y1: Float, x2: Float, y2: Float): Float {
    return sqrt(((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)))
}

fun ease(p: Float): Float {
    return 3 * p * p - 2 * p * p * p
}

fun ease(p: Float, g: Float): Float {
    return if (p < 0.5f) {
        0.5f * pow(2 * p, g)
    } else {
        1 - 0.5f * pow(2 * (1 - p), g)
    }
}

fun lerp(a: Float, b: Float, t: Float): Float {
    return a + (b - a) * t
}

fun map(value: Float, start1: Float, stop1: Float, start2: Float, stop2: Float): Float {
    return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1))
}

fun pow(n: Float, e: Float): Float {
    return n.pow(e)
}

fun toRadians(degrees: Float): Float {
    return Math.toRadians(degrees.toDouble()).toFloat()
}

fun sq(num: Float): Float {
    return num * num
}
