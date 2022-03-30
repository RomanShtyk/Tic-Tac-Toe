package com.example.tiktactoe.ui

import androidx.lifecycle.ViewModel
import com.example.tiktactoe.model.Cell
import com.example.tiktactoe.model.CellState
import com.example.tiktactoe.utils.TicTacToeSolver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class GameViewModel : ViewModel(), CoroutineScope {

    private var job = SupervisorJob()

    override val coroutineContext: CoroutineContext = Dispatchers.Default + job

    private fun getInitialState(): List<Cell> =
        List(GameActivity.GAME_SIZE * GameActivity.GAME_SIZE) {
            Cell(it, false, CellState.EMPTY)
        }

    private val _cellsList: MutableStateFlow<List<Cell>> = MutableStateFlow(getInitialState())
    val cellsList: StateFlow<List<Cell>> = _cellsList

    private val _isGameFinished: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isGameFinished: StateFlow<Boolean> = _isGameFinished

    fun onReset() {
        launch {
            _cellsList.emit(getInitialState())
            _isGameFinished.emit(false)
        }
    }

    fun onCellClicked(position: Int) {
        launch {
            var currentList = _cellsList.value.toMutableList()

            val currentMoveState = TicTacToeSolver.getNextMoveCellState(currentList)

            currentList[position] = currentList[position].copy(state = currentMoveState)

            currentList = TicTacToeSolver.solveGame(currentList, position, currentMoveState)

            if (TicTacToeSolver.checkIsGameFinished(currentList)) {
                _isGameFinished.emit(true)
            }

            _cellsList.emit(currentList)
        }
    }

}
