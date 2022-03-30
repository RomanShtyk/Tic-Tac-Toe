package com.example.tiktactoe.utils

import com.example.tiktactoe.model.Cell
import com.example.tiktactoe.model.CellState
import com.example.tiktactoe.ui.GameActivity

object TicTacToeSolver {

    fun solveGame(list: List<Cell>, position: Int, newState: CellState): MutableList<Cell> {

        var currentList = list.toMutableList()

        checkColumn(currentList.toMutableList(), position, newState)?.let {
            currentList = it
        }
        checkRow(currentList.toMutableList(), position, newState)?.let {
            currentList = it
        }
        checkDiagonalLtoR(currentList.toMutableList(), newState)?.let {
            currentList = it
        }
        checkDiagonalRtoL(currentList.toMutableList(), newState)?.let {
            currentList = it
        }

        return currentList
    }

    fun checkIsGameFinished(list: List<Cell>): Boolean {
        if (list.find { it.isWin } != null) return true
        if (list.find { it.state == CellState.EMPTY } == null) return true
        return false
    }

    fun getNextMoveCellState(list: List<Cell>): CellState {
        return if (list.filter { it.state != CellState.EMPTY }.size % 2 == 0) CellState.CROSS else CellState.CIRCLE
    }

    private fun checkRow(
        list: MutableList<Cell>,
        updatedPosition: Int,
        state: CellState
    ): MutableList<Cell>? {
        repeat(GameActivity.GAME_SIZE) {
            val index = (updatedPosition / GameActivity.GAME_SIZE) * GameActivity.GAME_SIZE + it
            if (list[index].state != state) return null
            list[index] = list[index].copy(isWin = true)
        }
        return list
    }

    private fun checkColumn(
        list: MutableList<Cell>,
        updatedPosition: Int,
        state: CellState
    ): MutableList<Cell>? {
        repeat(GameActivity.GAME_SIZE) {
            val index = (updatedPosition % GameActivity.GAME_SIZE) + (GameActivity.GAME_SIZE * it)
            if (list[index].state != state) return null
            list[index] = list[index].copy(isWin = true)
        }
        return list
    }

    private fun checkDiagonalLtoR(list: MutableList<Cell>, state: CellState): MutableList<Cell>? {
        repeat(GameActivity.GAME_SIZE) {
            val index = it * (GameActivity.GAME_SIZE) + it
            if (list[index].state != state) return null
            list[index] = list[index].copy(isWin = true)
        }
        return list
    }

    private fun checkDiagonalRtoL(list: MutableList<Cell>, state: CellState): MutableList<Cell>? {
        repeat(GameActivity.GAME_SIZE) {
            val index = (it + 1) * (GameActivity.GAME_SIZE - 1)
            if (list[index].state != state) return null
            list[index] = list[index].copy(isWin = true)
        }
        return list
    }

}
