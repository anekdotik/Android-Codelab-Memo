package com.sap.codelab.presentation.home

import androidx.recyclerview.widget.RecyclerView
import com.sap.codelab.databinding.RecyclerviewMemoBinding
import com.sap.codelab.domain.model.Memo

/**
 * View holder for Memos.
 */
class MemoViewHolder(
    private val binding: RecyclerviewMemoBinding,
    private val onMemoClick: (Long) -> Unit,
    private val onCheckedChange: (Memo, Boolean) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(memo: Memo) = with(binding) {
        memoTitle.text = memo.title
        memoText.text = memo.description

        root.setOnClickListener {
            onMemoClick(memo.id)
        }

        checkBox.setOnCheckedChangeListener(null)
        checkBox.isChecked = memo.isDone
        checkBox.isEnabled = !memo.isDone
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            onCheckedChange(memo, isChecked)
        }
    }
}