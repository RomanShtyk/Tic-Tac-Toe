package com.example.tiktactoe.ui.list

import androidx.recyclerview.widget.DiffUtil
import com.example.tiktactoe.domain.model.Cell

object CellDiff : DiffUtil.ItemCallback<Cell>() {

    override fun areItemsTheSame(oldItem: Cell, newItem: Cell): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Cell, newItem: Cell): Boolean {
        return oldItem == newItem
    }
}
