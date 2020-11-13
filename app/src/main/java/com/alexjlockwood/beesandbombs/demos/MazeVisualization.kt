package com.alexjlockwood.beesandbombs.demos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import com.alexjlockwood.beesandbombs.demos.MazeType.PRIMS_ALGORITHM
import com.alexjlockwood.beesandbombs.demos.MazeType.RANDOMIZED_DEPTH_FIRST_TRAVERSAL
import com.alexjlockwood.beesandbombs.demos.MazeType.RANDOMIZED_TRAVERSAL
import com.alexjlockwood.beesandbombs.demos.utils.sinebow
import java.util.*
import kotlin.math.floor
import kotlin.math.round

private const val N = 1 shl 0
private const val S = 1 shl 1
private const val W = 1 shl 2
private const val E = 1 shl 3

// TODO: make dp?
private const val CELL_SIZE = 8f
private const val CELL_SPACING = 8f

private const val COLOR_SPEED = 300f

enum class MazeType {
    RANDOMIZED_TRAVERSAL,
    RANDOMIZED_DEPTH_FIRST_TRAVERSAL,
    PRIMS_ALGORITHM,
}

@Composable
fun MazeVisualization(modifier: Modifier = Modifier) {
    val mazeTypes = MazeType.values()
    var mazeType by mutableStateOf(mazeTypes[0])
    var frameCounter by mutableStateOf(0)
    Box(
        modifier
            .clickable(onClick = {
                // Tap the screen to cycle through the different maze types.
                mazeType = mazeTypes[(mazeTypes.indexOf(mazeType) + 1) % mazeTypes.size]
                frameCounter = 0
            })
            .drawWithCache {
                val mazeWidth = size.mazeWidth()
                val mazeHeight = size.mazeHeight()
                val cells = when (mazeType) {
                    RANDOMIZED_TRAVERSAL -> generateMaze(RandomizedTraversalHeap(), mazeWidth, mazeHeight)
                    RANDOMIZED_DEPTH_FIRST_TRAVERSAL -> generateMaze(RandomizedDepthFirstHeap(), mazeWidth, mazeHeight)
                    PRIMS_ALGORITHM -> generateMaze(PrimsHeap(), mazeWidth, mazeHeight)
                }
                val fills = cells.map { null }.toMutableList<Color?>()
                val frontier = mutableListOf(0)

                onDrawBehind {
                    drawRect(Color.Black)

                    translate(
                        round((size.width - mazeWidth * CELL_SIZE - (mazeWidth + 1) * CELL_SPACING) / 2f),
                        round((size.height - mazeHeight * CELL_SIZE - (mazeHeight + 1) * CELL_SPACING) / 2f),
                    ) {
                        val nextFrontier = mutableListOf<Int>()
                        val currentColor = sinebow(frameCounter / COLOR_SPEED)
                        for (i in frontier) {
                            fills[i] = currentColor
                            if (cells[i] and E != 0 && fills[i + 1] == null) nextFrontier.add(i + 1)
                            if (cells[i] and W != 0 && fills[i - 1] == null) nextFrontier.add(i - 1)
                            if (cells[i] and S != 0 && fills[i + mazeWidth] == null) nextFrontier.add(i + mazeWidth)
                            if (cells[i] and N != 0 && fills[i - mazeWidth] == null) nextFrontier.add(i - mazeWidth)
                        }

                        drawMaze(cells, fills)

                        if (nextFrontier.isNotEmpty()) {
                            frontier.clear()
                            frontier.addAll(nextFrontier)
                            frameCounter++
                        }
                    }
                }
            }
    )
}

private fun Size.mazeWidth() = floor((width - CELL_SPACING) / (CELL_SIZE + CELL_SPACING)).toInt()

private fun Size.mazeHeight() = floor((height - CELL_SPACING) / (CELL_SIZE + CELL_SPACING)).toInt()

private fun DrawScope.drawMaze(cells: List<Int>, fills: List<Color?>) {
    for (i in cells.indices) {
        val color = fills[i] ?: continue
        val x = i % size.mazeWidth()
        val y = i / size.mazeWidth()
        val x0 = x * CELL_SIZE + (x + 1) * CELL_SPACING
        val y0 = y * CELL_SIZE + (y + 1) * CELL_SPACING
        drawRect(
            color = color,
            topLeft = Offset(x0, y0),
            size = Size(CELL_SIZE, CELL_SIZE),
        )
        if (cells[i] and S != 0) {
            drawRect(
                color = color,
                topLeft = Offset(x0, y0 + CELL_SPACING),
                size = Size(CELL_SIZE, CELL_SPACING),
            )
        }
        if (cells[i] and E != 0) {
            drawRect(
                color = color,
                topLeft = Offset(x0 + CELL_SPACING, y0),
                size = Size(CELL_SPACING, CELL_SIZE),
            )
        }
    }
}


private fun generateMaze(heap: Heap, mazeWidth: Int, mazeHeight: Int): List<Int> {
    heap.add(Edge(0, N))
    heap.add(Edge(0, E))
    val cells = (0 until mazeWidth * mazeHeight).map { 0 }.toMutableList()
    while (heap.isNotEmpty()) {
        val (i0, d0) = heap.remove()
        val x0 = i0 % mazeWidth
        val y0 = i0 / mazeWidth
        val i1: Int
        val d1: Int
        val x1: Int
        val y1: Int
        when (d0) {
            N -> {
                i1 = i0 - mazeWidth
                d1 = S
                x1 = x0
                y1 = y0 - 1
            }
            S -> {
                i1 = i0 + mazeWidth
                d1 = N
                x1 = x0
                y1 = y0 + 1
            }
            W -> {
                i1 = i0 - 1
                d1 = E
                x1 = x0 - 1
                y1 = y0
            }
            E -> {
                i1 = i0 + 1
                d1 = W
                x1 = x0 + 1
                y1 = y0
            }
            else -> throw IllegalStateException("Invalid direction: $d0")
        }
        if (0 <= i1 && cells[i1] == 0) {
            cells[i0] = cells[i0] or d0
            cells[i1] = cells[i1] or d1
            val addedEdges = mutableListOf<Edge>()
            if (0 < y1 && cells[i1 - mazeWidth] == 0) addedEdges.add(Edge(i1, N))
            if (y1 < mazeHeight - 1 && cells[i1 + mazeWidth] == 0) addedEdges.add(Edge(i1, S))
            if (0 < x1 && cells[i1 - 1] == 0) addedEdges.add(Edge(i1, W))
            if (x1 < mazeWidth - 1 && cells[i1 + 1] == 0) addedEdges.add(Edge(i1, E))
            heap.addAll(addedEdges)
        }
    }
    return cells
}

private interface Heap {
    fun add(edge: Edge): Boolean
    fun remove(): Edge
    fun isNotEmpty(): Boolean
    fun addAll(edges: MutableList<Edge>)
}

private class PrimsHeap : Heap {
    private val heap = PriorityQueue<Edge>(10) { a, b -> a.priority.compareTo(b.priority) }
    override fun add(edge: Edge): Boolean = heap.add(edge)
    override fun remove(): Edge = heap.remove()
    override fun isNotEmpty(): Boolean = heap.isNotEmpty()
    override fun addAll(edges: MutableList<Edge>) = edges.forEach { heap.add(it) }
}

private class RandomizedDepthFirstHeap : Heap {
    private val heap = mutableListOf<Edge>()
    override fun add(edge: Edge): Boolean = heap.add(edge)
    override fun remove(): Edge = heap.removeLast()
    override fun isNotEmpty(): Boolean = heap.isNotEmpty()
    override fun addAll(edges: MutableList<Edge>) {
        edges.shuffle()
        edges.forEach { heap.add(it) }
    }
}

private class RandomizedTraversalHeap : Heap {
    private val heap = mutableListOf<Edge>()
    override fun add(edge: Edge): Boolean = heap.add(edge)
    override fun remove(): Edge = heap.removeAt((0 until heap.size).random())
    override fun isNotEmpty(): Boolean = heap.isNotEmpty()
    override fun addAll(edges: MutableList<Edge>) = edges.forEach { heap.add(it) }
}

private data class Edge(val index: Int, val direction: Int, val priority: Double = Math.random())