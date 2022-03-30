package com.example.tiktactoe.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.tiktactoe.ui.list.CellAdapter
import com.example.tiktactoe.R
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.coroutines.flow.collect

class GameActivity : AppCompatActivity() {

    private val viewModel: GameViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(this)[GameViewModel::class.java]
    }

    private val adapter: CellAdapter by lazy(LazyThreadSafetyMode.NONE) {
        CellAdapter(viewModel::onCellClicked)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        initView()
        subscribeToViewModel()
    }

    private fun initView() {
        rvCells.layoutManager = object : GridLayoutManager(this, GAME_SIZE) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        rvCells.adapter = adapter

        vTouchCatcher.setOnClickListener {
            viewModel.onReset()
        }
    }

    private fun subscribeToViewModel() {
        lifecycleScope.launchWhenCreated {
            viewModel.cellsList.collect(adapter::submitList)
        }
        lifecycleScope.launchWhenCreated {
            viewModel.isGameFinished.collect(::onGameFinished)
        }
    }

    private fun onGameFinished(isFinished: Boolean) {
        vTouchCatcher.visibility = if (isFinished) View.VISIBLE else View.GONE
    }

    companion object {

        const val GAME_SIZE = 3
    }
}
