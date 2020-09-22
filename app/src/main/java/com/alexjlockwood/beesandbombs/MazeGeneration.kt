package com.alexjlockwood.beesandbombs

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import java.util.*
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sin

private const val N = 1 shl 0
private const val S = 1 shl 1
private const val W = 1 shl 2
private const val E = 1 shl 3

private const val width = 1328f
private const val height = 1328f

//private const val width = 1328f
//private const val width = 1440f
//private const val height = 2621f
//private const val height = 2733f
private const val cellSize = 8f
private const val cellSpacing = 8f
private val cellWidth = floor((width - cellSpacing) / (cellSize + cellSpacing)).toInt()
private val cellHeight = floor((height - cellSpacing) / (cellSize + cellSpacing)).toInt()

@Composable
fun MazeGeneration(modifier: Modifier = Modifier) {
    val numCells = cellWidth * cellHeight
    val cells = remember { generateRandomizedTraversal(cellWidth, cellHeight) }
    val counter = remember { mutableStateOf(0f) }
    val fills = remember { arrayOfNulls<Color>(numCells).toMutableList() }
    val frontier = remember { mutableListOf((cellWidth shr 1) + (cellHeight shr 1) * cellWidth) }

    Canvas(modifier = modifier) {
        drawRect(Color.Black)

        translate(
            round((width - cellWidth * cellSize - (cellWidth + 1) * cellSpacing) / 2),
            round((height - cellHeight * cellSize - (cellHeight + 1) * cellSpacing) / 2),
        ) {
            val n = frontier.size
            val frontier1 = mutableListOf<Int>()
            val color = sinebow(counter.value / 300f)
            for (i in 0 until n) {
                val i0 = frontier[i]
                fills[i0] = color
                if (cells[i0] and E != 0) {
                    val i1 = i0 + 1
                    if (fills[i1] == null) {
                        frontier1.add(i1)
                    }
                }
                if (cells[i0] and W != 0) {
                    val i1 = i0 - 1
                    if (fills[i1] == null) {
                        frontier1.add(i1)
                    }
                }
                if (cells[i0] and S != 0) {
                    val i1 = i0 + cellWidth
                    if (fills[i1] == null) {
                        frontier1.add(i1)
                    }
                }
                if (cells[i0] and N != 0) {
                    val i1 = i0 - cellWidth
                    if (fills[i1] == null) {
                        frontier1.add(i1)
                    }
                }
            }
            frontier.clear()
            frontier.addAll(frontier1)

            fillCells(cells, fills)

            if (frontier.isNotEmpty()) {
                counter.value++
            }
        }
    }
}

private fun generatePrims(cellWidth: Int, cellHeight: Int): List<Int> {
    val heap = PriorityQueue<Edge>(10) { a, b -> a.priority.compareTo(b.priority) }
    val cells = (0 until cellWidth * cellHeight).map { 0 }.toMutableList()
    heap.add(Edge(0, N))
    heap.add(Edge(0, E))
    while (heap.isNotEmpty()) {
        val edge = heap.remove()
        val i0 = edge.index
        val d0 = edge.direction
        val x0 = i0 % cellWidth
        val y0 = i0 / cellWidth
        val i1: Int
        val d1: Int
        val x1: Int
        val y1: Int
        if (d0 == N) {
            i1 = i0 - cellWidth
            d1 = S
            x1 = x0
            y1 = y0 - 1
        } else if (d0 == S) {
            i1 = i0 + cellWidth
            d1 = N
            x1 = x0
            y1 = y0 + 1
        } else if (d0 == W) {
            i1 = i0 - 1
            d1 = E
            x1 = x0 - 1
            y1 = y0
        } else {
            i1 = i0 + 1
            d1 = W
            x1 = x0 + 1
            y1 = y0
        }
        if (i1 >= 0 && cells[i1] == 0) {
            cells[i0] = cells[i0] or d0
            cells[i1] = cells[i1] or d1
            if (y1 > 0 && cells[i1 - cellWidth] == 0) {
                heap.add(Edge(i1, N))
            }
            if (y1 < cellHeight - 1 && cells[i1 + cellWidth] == 0) {
                heap.add(Edge(i1, S))
            }
            if (x1 > 0 && cells[i1 - 1] == 0) {
                heap.add(Edge(i1, W))
            }
            if (x1 < cellWidth - 1 && cells[i1 + 1] == 0) {
                heap.add(Edge(i1, E))
            }
        }
    }
    return cells
}

private fun generateRandomizedDepthFirst(cellWidth: Int, cellHeight: Int): List<Int> {
    val heap = mutableListOf<Edge>()
    val cells = (0 until cellWidth * cellHeight).map { 0 }.toMutableList()
    heap.add(Edge(0, N))
    heap.add(Edge(0, E))
    while (heap.isNotEmpty()) {
        val edge = heap.removeLast()
        val i0 = edge.index
        val d0 = edge.direction
        val x0 = i0 % cellWidth
        val y0 = i0 / cellWidth
        val i1: Int
        val d1: Int
        val x1: Int
        val y1: Int
        if (d0 == N) {
            i1 = i0 - cellWidth
            d1 = S
            x1 = x0
            y1 = y0 - 1
        } else if (d0 == S) {
            i1 = i0 + cellWidth
            d1 = N
            x1 = x0
            y1 = y0 + 1
        } else if (d0 == W) {
            i1 = i0 - 1
            d1 = E
            x1 = x0 - 1
            y1 = y0
        } else {
            i1 = i0 + 1
            d1 = W
            x1 = x0 + 1
            y1 = y0
        }
        if (i1 >= 0 && cells[i1] == 0) {
            cells[i0] = cells[i0] or d0
            cells[i1] = cells[i1] or d1
            var m = 0
            if (y1 > 0 && cells[i1 - cellWidth] == 0) {
                heap.add(Edge(i1, N))
                m++
            }
            if (y1 < cellHeight - 1 && cells[i1 + cellWidth] == 0) {
                heap.add(Edge(i1, S))
                m++
            }
            if (x1 > 0 && cells[i1 - 1] == 0) {
                heap.add(Edge(i1, W))
                m++
            }
            if (x1 < cellWidth - 1 && cells[i1 + 1] == 0) {
                heap.add(Edge(i1, E))
                m++
            }
            shuffle(heap, heap.size - m, heap.size)
        }
    }
    return cells
}

private fun <T> shuffle(array: MutableList<T>, i0: Int, i1: Int): MutableList<T> {
    var m = i1 - i0
    while (m != 0) {
        val i = (Math.random() * m).toInt()
        m--
        val t = array[m + i0]
        array[m + i0] = array[i + i0]
        array[i + i0] = t
    }
    return array
}

private fun generateRandomizedTraversal(cellWidth: Int, cellHeight: Int): List<Int> {
    val heap = mutableListOf<Edge>()
    val cells = (0 until cellWidth * cellHeight).map { 0 }.toMutableList()
    heap.add(Edge(0, N))
    heap.add(Edge(0, E))
    while (heap.isNotEmpty()) {
        val edge = popRandom(heap)
        val i0 = edge.index
        val d0 = edge.direction
        val x0 = i0 % cellWidth
        val y0 = i0 / cellWidth
        val i1: Int
        val d1: Int
        val x1: Int
        val y1: Int
        if (d0 == N) {
            i1 = i0 - cellWidth
            d1 = S
            x1 = x0
            y1 = y0 - 1
        } else if (d0 == S) {
            i1 = i0 + cellWidth
            d1 = N
            x1 = x0
            y1 = y0 + 1
        } else if (d0 == W) {
            i1 = i0 - 1
            d1 = E
            x1 = x0 - 1
            y1 = y0
        } else {
            i1 = i0 + 1
            d1 = W
            x1 = x0 + 1
            y1 = y0
        }
        if (i1 >= 0 && cells[i1] == 0) {
            cells[i0] = cells[i0] or d0
            cells[i1] = cells[i1] or d1
            if (y1 > 0 && cells[i1 - cellWidth] == 0) {
                heap.add(Edge(i1, N))
            }
            if (y1 < cellHeight - 1 && cells[i1 + cellWidth] == 0) {
                heap.add(Edge(i1, S))
            }
            if (x1 > 0 && cells[i1 - 1] == 0) {
                heap.add(Edge(i1, W))
            }
            if (x1 < cellWidth - 1 && cells[i1 + 1] == 0) {
                heap.add(Edge(i1, E))
            }
        }
    }
    return cells
}

private fun <T> popRandom(array: MutableList<T>): T {
    val n = array.size
    val i = (Math.random() * n).toInt()
    val t = array[i]
    array[i] = array[n - 1]
    array[n - 1] = t
    return array.removeLast()
}

private fun generateWilsons(cellWidth: Int, cellHeight: Int): List<Int> {
    val cells: MutableList<Int?> = (0 until cellWidth * cellHeight).map { null }.toMutableList()
    val remaining = (0 until cellWidth * cellHeight).map { it }.toMutableList()
    val previous = (0 until cellWidth * cellHeight).map { -1 }.toMutableList()

    // Add the starting cell.
    val start = remaining.removeLast()
    cells[start] = 0

    fun eraseWalk(i0: Int, i2: Int) {
        var i0 = i0
        do {
            val i1 = previous[i0]
            previous[i0] = -1
            i0 = i1
        } while (i1 != i2)
    }

    fun loopErasedRandomWalk(): Boolean {
        var i0: Int
        do {
            if (remaining.isEmpty()) return true
            i0 = remaining.removeLast()
            val cellsi0 =  cells[i0]
        } while (cellsi0 != null && cellsi0 >= 0)

        previous[i0] = i0
        while (true) {
            var x0 = i0 % cellWidth
            var y0 = i0 / cellWidth

            // picking a legal random direction at each step.
            var i1 = (Math.random() * 4).toInt()
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

            // If this new cell was visited previously during this walk,
            // erase the loop, rewinding the path to its earlier state.
            if (previous[i1] >= 0) eraseWalk(i0, i1)

            // Otherwise, just add it to the walk.
            else previous[i1] = i0

            // If this cell is part of the maze, we’re done walking.
            val cellsi1 = cells[i1]
            if (cellsi1 != null && cellsi1>= 0) {

                // Add the random walk to the maze by backtracking to the starting cell.
                // Also erase this walk’s history to not interfere with subsequent walks.
                while (previous[i1] != i1) {
                    i0 = previous[i1]
                    if (i1 == i0 + 1) {
                        cells[i0] = cells[i0]?.or(E)?: E
                        cells[i1] = cells[i1]?.or(W)?: W
                    } else if (i1 == i0 - 1) {
                        cells[i0] = cells[i0]?.or(W)?: W
                        cells[i1] = cells[i1]?.or(E)?: E
                    } else if (i1 == i0 + cellWidth) {
                        cells[i0] = cells[i0]?.or(S)?: S
                        cells[i1] = cells[i1]?.or(N) ?: N
                    } else {
                        cells[i0] = cells[i0]?.or(N) ?: N
                        cells[i1] = cells[i1]?.or(S)?: S
                    }
                    previous[i1] = -1
                    i1 = i0
                }

                previous[i1] = -1
                return false
            }

            i0 = i1
        }
    }

    while (!loopErasedRandomWalk());

    val filteredCells =  cells.filterNotNull()
    if (filteredCells.size != cells.size) {
        throw RuntimeException("This shouldn't happen")
    }
    return filteredCells
}

private fun DrawScope.fillCells(cells: List<Int>, fills: List<Color?>) {
    var i = 0
    for (y in 0 until cellHeight) {
        for (x in 0 until cellWidth) {
            val color = fills[i]
            if (color != null) {
                fillCell(i, color)
                if (cells[i] and S != 0) {
                    fillSouth(i, color)
                }
                if (cells[i] and E != 0) {
                    fillEast(i, color)
                }
            }
            i++
        }
    }
}

private fun DrawScope.fillCell(i: Int, color: Color) {
    val x = i % cellWidth
    val y = i / cellWidth
    drawRect(
        color = color,
        topLeft = Offset(x * cellSize + (x + 1) * cellSpacing, y * cellSize + (y + 1) * cellSpacing),
        size = Size(cellSize, cellSize),
    )
}

private fun DrawScope.fillEast(i: Int, color: Color) {
    val x = i % cellWidth
    val y = i / cellWidth
    drawRect(
        color = color,
        topLeft = Offset((x + 1) * (cellSize + cellSpacing), y * cellSize + (y + 1) * cellSpacing),
        size = Size(cellSpacing, cellSize),
    )
}

private fun DrawScope.fillSouth(i: Int, color: Color) {
    val x = i % cellWidth
    val y = i / cellWidth
    drawRect(
        color = color,
        topLeft = Offset(x * cellSize + (x + 1) * cellSpacing, (y + 1) * (cellSize + cellSpacing)),
        size = Size(cellSize, cellSpacing),
    )
}

private fun sinebow(t: Float): Color {
    return Color(
        red = sin(PI * (t + 0f / 3f)).pow(2),
        green = sin(PI * (t + 1f / 3f)).pow(2),
        blue = sin(PI * (t + 2f / 3f)).pow(2),
    )
}

private data class Edge(val index: Int, val direction: Int, val priority: Double = Math.random())
