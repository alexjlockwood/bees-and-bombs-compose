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
import com.alexjlockwood.beesandbombs.demos.MazeType.*
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
    WILSONS_ALGORITHM,
}

@Composable
fun MazeVisualization(modifier: Modifier = Modifier) {
    val mazeTypes = MazeType.values()
    var mazeType by mutableStateOf(mazeTypes[0])
    var frameCounter by mutableStateOf(0)
    Box(
        modifier
            .clickable(onClick = {
                mazeType = mazeTypes[(mazeTypes.indexOf(mazeType) + 1) % mazeTypes.size]
                frameCounter = 0
            })
            .drawWithCache {
                val cellWidth = size.cellWidth()
                val cellHeight = size.cellHeight()
                val cells = when (mazeType) {
                    RANDOMIZED_TRAVERSAL -> generateMaze(RandomizedTraversalHeap(), cellWidth, cellHeight)
                    RANDOMIZED_DEPTH_FIRST_TRAVERSAL -> generateMaze(RandomizedDepthFirstHeap(), cellWidth, cellHeight)
                    PRIMS_ALGORITHM -> generateMaze(PrimsHeap(), cellWidth, cellHeight)
                    WILSONS_ALGORITHM -> generateWilsonsMaze(cellWidth, cellHeight)
                }
                val fills = arrayOfNulls<Color>(cellWidth * cellHeight).toMutableList()
//                val frontier = mutableListOf((cellWidth shr 1) + (cellHeight shr 1) * cellWidth)
                val frontier = mutableListOf(0)//(cellWidth shr 1) + (cellHeight shr 1) * cellWidth)

                onDraw {
                    drawRect(Color.Black)

                    translate(
                        round((size.width - cellWidth * CELL_SIZE - (cellWidth + 1) * CELL_SPACING) / 2f),
                        round((size.height - cellHeight * CELL_SIZE - (cellHeight + 1) * CELL_SPACING) / 2f),
                    ) {
                        val nextFrontier = mutableListOf<Int>()
                        val currentColor = sinebow(frameCounter / COLOR_SPEED)
                        for (i in frontier) {
                            fills[i] = currentColor
                            if (cells[i] and E != 0 && fills[i + 1] == null) nextFrontier.add(i + 1)
                            if (cells[i] and W != 0 && fills[i - 1] == null) nextFrontier.add(i - 1)
                            if (cells[i] and S != 0 && fills[i + cellWidth] == null) nextFrontier.add(i + cellWidth)
                            if (cells[i] and N != 0 && fills[i - cellWidth] == null) nextFrontier.add(i - cellWidth)
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

private fun generateMaze(heap: Heap, cellWidth: Int, cellHeight: Int): List<Int> {
    heap.add(Edge(0, N))
    heap.add(Edge(0, E))
    val cells = (0 until cellWidth * cellHeight).map { 0 }.toMutableList()
    while (heap.isNotEmpty()) {
        val (i0, d0) = heap.remove()
        val x0 = i0 % cellWidth
        val y0 = i0 / cellWidth
        val i1: Int
        val d1: Int
        val x1: Int
        val y1: Int
        when (d0) {
            N -> {
                i1 = i0 - cellWidth
                d1 = S
                x1 = x0
                y1 = y0 - 1
            }
            S -> {
                i1 = i0 + cellWidth
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
            if (0 < y1 && cells[i1 - cellWidth] == 0) addedEdges.add(Edge(i1, N))
            if (y1 < cellHeight - 1 && cells[i1 + cellWidth] == 0) addedEdges.add(Edge(i1, S))
            if (0 < x1 && cells[i1 - 1] == 0) addedEdges.add(Edge(i1, W))
            if (x1 < cellWidth - 1 && cells[i1 + 1] == 0) addedEdges.add(Edge(i1, E))
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

private fun generateWilsonsMaze(cellWidth: Int, cellHeight: Int): List<Int> {
    val cellRange = (0 until cellWidth * cellHeight)
    // Each cell's edge bits.
    val cells = cellRange.map { null }.toMutableList<Int?>()
    // Tracks the current random walk path.
    val prevIndexMap = cellRange.map { -1 }.toMutableList()
    // The remaining cell indices to visit.
    val remainingIndices = cellRange.map { it }.toMutableList()

    // Add the starting cell.
    cells[remainingIndices.removeLast()] = 0

    fun eraseCurrentWalk(prevIndex: Int, currIndex: Int) {
        var i = prevIndex
        do {
            val j = prevIndexMap[i]
            prevIndexMap[i] = -1
            i = j
        } while (i != currIndex)
    }

    fun loopErasedRandomWalk(): Boolean {
        var i0: Int
        do {
            if (remainingIndices.isEmpty()) return true
            i0 = remainingIndices.removeLast()
        } while (cells[i0].geq(0))

        prevIndexMap[i0] = i0
        while (true) {
            var x0 = i0 % cellWidth
            var y0 = i0 / cellWidth

            // Pick a legal random direction at each step.
            var i1 = (0 until 4).random()
            if (i1 == 0) {
                if (y0 <= 0) continue
                --y0
                i1 = i0 - cellWidth
            } else if (i1 == 1) {
                if (y0 >= cellHeight - 1) continue
                ++y0
                i1 = i0 + cellWidth
            } else if (i1 == 2) {
                if (x0 <= 0) continue
                --x0
                i1 = i0 - 1
            } else {
                if (x0 >= cellWidth - 1) continue
                ++x0
                i1 = i0 + 1
            }

            if (prevIndexMap[i1] >= 0) {
                // If this new cell was visited previously during this walk,
                // erase the loop, rewinding the path to its earlier state.
                eraseCurrentWalk(i0, i1)
            } else {
                // Otherwise, just add it to the walk.
                prevIndexMap[i1] = i0
            }

            // If this cell is part of the maze, we're done walking.
            if (cells[i1].geq(0)) {
                // Add the random walk to the maze by backtracking to the starting cell.
                // Also erase this walk's history to not interfere with subsequent walks.
                while (prevIndexMap[i1] != i1) {
                    i0 = prevIndexMap[i1]
                    when (i1) {
                        i0 + 1 -> {
                            cells[i0] = cells[i0]?.or(E) ?: E
                            cells[i1] = cells[i1]?.or(W) ?: W
                        }
                        i0 - 1 -> {
                            cells[i0] = cells[i0]?.or(W) ?: W
                            cells[i1] = cells[i1]?.or(E) ?: E
                        }
                        i0 + cellWidth -> {
                            cells[i0] = cells[i0]?.or(S) ?: S
                            cells[i1] = cells[i1]?.or(N) ?: N
                        }
                        else -> {
                            cells[i0] = cells[i0]?.or(N) ?: N
                            cells[i1] = cells[i1]?.or(S) ?: S
                        }
                    }
                    prevIndexMap[i1] = -1
                    i1 = i0
                }

                prevIndexMap[i1] = -1
                return false
            }

            i0 = i1
        }
    }


    while (!loopErasedRandomWalk()) {
        // Loop until there are no remaining cells, adding a loop-erased
        // random walk to the maze each iteration.
    }

    return cells.map { it!! }
}

private fun Int?.geq(n: Int) = this != null && this >= n

private fun DrawScope.cellWidth() = size.cellWidth()

private fun Size.cellWidth() = floor((width - CELL_SPACING) / (CELL_SIZE + CELL_SPACING)).toInt()

private fun Size.cellHeight() = floor((height - CELL_SPACING) / (CELL_SIZE + CELL_SPACING)).toInt()

private fun DrawScope.drawMaze(cells: List<Int>, fills: List<Color?>) {
    for (i in cells.indices) {
        val color = fills[i] ?: continue
        val x = i % cellWidth()
        val y = i / cellWidth()
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

private data class Edge(val index: Int, val direction: Int, val priority: Double = Math.random())