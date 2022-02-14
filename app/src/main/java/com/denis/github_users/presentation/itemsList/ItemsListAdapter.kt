package com.denis.github_users.presentation.itemsList

import android.view.ViewGroup
import androidx.navigation.NavDirections
import androidx.paging.PagingDataAdapter
import com.denis.github_users.presentation.itemsList.innerItem.ItemViewHolder
import com.denis.github_users.presentation.itemsList.model.PresentationUserData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemsListAdapter @Inject constructor() :
    PagingDataAdapter<PresentationUserData, ItemViewHolder>(DiffCallback()) {

    private var clickFlowCollector: ((direction: NavDirections) -> Unit)? = null

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder.create(parent, clickFlowCollector)
    }

    fun setClickCollector(clickCollector: ((direction: NavDirections) -> Unit)?) {
        clickFlowCollector = clickCollector
    }
}