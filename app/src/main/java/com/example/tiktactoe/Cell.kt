package com.example.tiktactoe

data class Cell(val id: Int, val isWin: Boolean, val state: CellState)

enum class CellState {
    CIRCLE,
    CROSS,
    EMPTY
}
