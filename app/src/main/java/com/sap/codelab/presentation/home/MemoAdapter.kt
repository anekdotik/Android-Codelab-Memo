package com.sap.codelab.presentation.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sap.codelab.databinding.RecyclerviewMemoBinding
import com.sap.codelab.domain.model.Memo
import com.sap.codelab.presentation.home.MemoViewHolder
import kotlin.math.max

/**
 * Adapter containing a set of memos.
 */
class MemoAdapter(
    private val onMemoClick: (Long) -> Unit,
    private val onCheckedChange: (Memo, Boolean) -> Unit
) : ListAdapter<Memo, MemoViewHolder>(MemoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val binding = RecyclerviewMemoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MemoViewHolder(binding, onMemoClick, onCheckedChange)
    }

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class MemoDiffCallback : DiffUtil.ItemCallback<Memo>() {
    override fun areItemsTheSame(oldItem: Memo, newItem: Memo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Memo, newItem: Memo): Boolean {
        return oldItem == newItem
    }
}