package com.mealmuse.util

import androidx.recyclerview.widget.DiffUtil

class RecipesDiffUtil(
    private val oldList: List<com.mealmuse.models.Result>,
    private val newList: List<com.mealmuse.models.Result>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
       //Then we are going to use a three equal signs to actually see if those two are identical.
        return oldList[oldItemPosition] === newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}