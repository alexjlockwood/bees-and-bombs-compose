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

//private const val width = 1328f
private const val width = 1440f
//private const val height = 2621f
private const val height = 2733f
private const val cellSize = 8f
private const val cellSpacing = 8f
private val cellWidth = floor((width - cellSpacing) / (cellSize + cellSpacing)).toInt()
private val cellHeight = floor((height - cellSpacing) / (cellSize + cellSpacing)).toInt()

@Composable
fun MazeGeneration(modifier: Modifier = Modifier) {
    val numCells = cellWidth * cellHeight
    val cells = remember { generate(cellWidth, cellHeight) }
    val counter = remember { mutableStateOf(0f) }
    val fills = remember { arrayOfNulls<Color>(numCells).toMutableList() }
    val frontier = remember {
        val index = (cellWidth shr 1) + (cellHeight shr 1) * cellWidth
        mutableListOf(index)
    }

    Canvas(modifier = modifier) {
        //drawRect(Color.Black)

        translate(
            round((width - cellWidth * cellSize - (cellWidth + 1) * cellSpacing) / 2),
            round((height - cellHeight * cellSize - (cellHeight + 1) * cellSpacing) / 2),
        ) {
            val (width, height)  =  size
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

private fun generate(cellWidth: Int, cellHeight: Int): List<Int> {
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
