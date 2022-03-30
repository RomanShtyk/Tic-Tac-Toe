package com.example.tiktactoe.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tiktactoe.model.Cell
import com.example.tiktactoe.model.CellState
import com.example.tiktactoe.R
import kotlinx.android.synthetic.main.item_cell.view.*

class CellAdapter(private val onCellClicked: (Int) -> Unit) :
    ListAdapter<Cell, CellAdapter.ViewHolder>(CellDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cell, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(item: Cell) {
            setImageRes(item.state)
            setClickListener(item.state)
            setBackgroundRes(item.isWin)
        }

        private fun setImageRes(state: CellState) {
            val res = when (state) {
                CellState.CROSS -> R.drawable.ic_cross
                CellState.CIRCLE -> R.drawable.ic_circle
                CellState.EMPTY -> 0
            }
            itemView.ivIcon.setImageResource(res)
        }

        private fun setBackgroundRes(isWin: Boolean) {
            val res = if (isWin) R.drawable.shape_cell_win else R.drawable.shape_cell
            itemView.setBackgroundResource(res)
        }

        private fun setClickListener(state: CellState) {
            if (state == CellState.EMPTY) {
                view.setOnClickListener {
                    it.setOnClickListener(null)
                    view.isClickable = false
                    onCellClicked(absoluteAdapterPosition)
                }
            } else {
                view.setOnClickListener(null)
                view.isClickable = false
            }
        }
    }

}

