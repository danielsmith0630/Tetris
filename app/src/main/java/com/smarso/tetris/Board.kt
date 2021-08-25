package com.smarso.tetris

import com.smarso.tetris.figures.BitMatrix
import com.smarso.tetris.figures.Figure
import com.smarso.tetris.figures.Point

class Board(private val view: GameView) {

    var area: BitMatrix = BitMatrix(AREA_HEIGHT, AREA_WIDTH) { _, _ -> false }

    lateinit var currentFigure: Figure

    fun drawFigure() = with(currentFigure) {
        if (ghost == null) {
            for (i in 0 until AREA_HEIGHT - position.y) {
                val movement = Point(0, i)
                if (!canMove(movement, currentFigure)) {
                    ghost = clone()
                    ghost!!.position = Point(position.x, position.y + i - 1)
                    break
                }
            }
        }

        ghost?.points?.forEach {
            view.drawBlockAt(it.x, it.y, currentFigure.color, PaintStyle.STOKE)
        }

        points.forEach {
            view.drawBlockAt(it.x, it.y, currentFigure.color, PaintStyle.FILL)
        }
        view.invalidate()
    }

    private fun clearFigure(figure: Figure) {
        figure
                .points
                .forEach {
                    view.clearBlockAt(it.x, it.y)
                }
        figure
                .ghost
                ?.points
                ?.forEach {
                    view.clearBlockAt(it.x, it.y)
                }
    }

    /**
     * @return true if success
     */
    fun moveFigure(movement: Point, figure: Figure = currentFigure) : Boolean {
        canMove(movement, figure) || return false
        clearFigure(figure)

        // invalidate ghost
        if (movement.x != 0)
            figure.ghost = null

        figure.position += movement
        drawFigure()
        return true
    }

    private fun canMove(movement: Point, figure: Figure): Boolean = figure
            .points
            .all {
                val nextPoint = it + movement
                !nextPoint.outOfArea() && !area[nextPoint]
            }

    private fun Point.outOfArea(): Boolean = x >= AREA_WIDTH || x < 0 || y >= AREA_HEIGHT || y < 0

    fun rotateFigure() {
        val newFigure = currentFigure.clone().apply {
            rotate()

            // edge cases
            while (!points.all { it.x >= 0 }) {
                position += Direction.RIGHT.movement
            }
            while (!points.all { it.x < AREA_WIDTH }) {
                position += Direction.LEFT.movement
            }
            while (!points.all { it.y < AREA_HEIGHT }) {
                position += Direction.UP.movement
            }

            // try to fix unexpected collisions
            if (!points.all { !area[it] }) {
                if (canMove(Direction.RIGHT.movement, this))
                    position += Direction.RIGHT.movement
                else if (canMove(Direction.LEFT.movement, this))
                    position += Direction.LEFT.movement
            }
        }

        if (newFigure.points.all { !area[it] }) {
            clearFigure(currentFigure)
            currentFigure = newFigure
            currentFigure.ghost = null
            drawFigure()
        }
    }

    fun fixFigure() {
        for (point in currentFigure.points) {
            area[point] = true
        }
    }

    fun getFilledLinesIndices() : List<Int> = area.array
            .mapIndexed { i, row -> i to row }
            .filter { it.second.all { it } }
            .map { it.first }

    fun wipeLines(lines: List<Int>) {
        val cropped = area.array.filter { !it.all { it } }.toMutableList()
        repeat(lines.size) {
            cropped.add(0, Array(AREA_WIDTH) {false})
        }
        area = BitMatrix(cropped.toTypedArray())
    }

    fun startingPosition(figure: Figure) = Point((AREA_WIDTH - figure.matrix.width) / 2, 0)
}