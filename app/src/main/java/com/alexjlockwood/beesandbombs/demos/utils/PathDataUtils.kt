package com.alexjlockwood.beesandbombs.demos.utils

import android.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.graphics.vector.PathNode
import androidx.compose.ui.graphics.vector.PathParser

/**
 * Converts an SVG path data string to a native [Path] object.
 */
fun String.asAndroidPath(): Path {
    return PathParser().parsePathString(this).toPath().asAndroidPath()
}

/**
 * Linearly interpolates two lists of path nodes to simulate path morphing.
 */
fun lerp(fromPathNodes: List<PathNode>, toPathNodes: List<PathNode>, t: Float): List<PathNode> {
    return fromPathNodes.mapIndexed { i, from ->
        val to = toPathNodes[i]
        if (from is PathNode.MoveTo && to is PathNode.MoveTo) {
            PathNode.MoveTo(
                lerp(from.x, to.x, t),
                lerp(from.y, to.y, t),
            )
        } else if (from is PathNode.CurveTo && to is PathNode.CurveTo) {
            PathNode.CurveTo(
                lerp(from.x1, to.x1, t),
                lerp(from.y1, to.y1, t),
                lerp(from.x2, to.x2, t),
                lerp(from.y2, to.y2, t),
                lerp(from.x3, to.x3, t),
                lerp(from.y3, to.y3, t),
            )
        } else {
            // TODO: support all possible SVG path data types
            throw IllegalStateException("Unsupported SVG PathNode command")
        }
    }
}
