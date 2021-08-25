package com.smarso.tetris

import com.smarso.tetris.figures.Point

enum class Direction(val movement: Point) {
    UP(Point(0, -1)),
    DOWN(Point(0, 1)),
    LEFT(Point(-1, 0)),
    RIGHT(Point(1, 0))
}