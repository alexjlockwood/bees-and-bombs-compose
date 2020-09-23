package com.alexjlockwood.beesandbombs.demos.utils

import android.graphics.Path
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.graphics.vector.PathNode
import androidx.compose.ui.graphics.vector.addPathNodes

/**
 * Converts an SVG path data string to a native [Path] object.
 */
fun String.toAndroidPath(): Path {
    val path = Path()
    val pathNodes = addPathNodes(this)
    pathNodes.forEach {
        when (it) {
            is PathNode.MoveTo -> path.moveTo(it.x, it.y)
            is PathNode.LineTo -> path.lineTo(it.x, it.y)
            is PathNode.CurveTo -> path.cubicTo(it.x1, it.y1, it.x2, it.y2, it.x3, it.y3)
            is PathNode.Close -> path.close()
            // TODO: support all possible SVG path data commands
            else -> throw UnsupportedOperationException("Unsupported SVG path data command")
        }
    }
    return path
}
