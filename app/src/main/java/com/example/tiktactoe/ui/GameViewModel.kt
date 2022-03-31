package com.example.tiktactoe.ui

import androidx.lifecycle.ViewModel
import com.example.tiktactoe.domain.model.Cell
import com.example.tiktactoe.domain.model.CellState
import com.example.tiktactoe.domain.TicTacToeInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameInteractor: TicTacToeInteractor,
) : ViewModel(), CoroutineScope {

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

            val currentMoveState = gameInteractor.getNextMoveCellState(currentList)

            currentList[position] = currentList[position].copy(state = currentMoveState)

            currentList = gameInteractor.solveGame(currentList, position, currentMoveState)

            if (gameInteractor.checkIsGameFinished(currentList)) {
                _isGameFinished.emit(true)
            }

            _cellsList.emit(currentList)
        }
    }

}
