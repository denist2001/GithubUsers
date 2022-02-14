package com.denis.github_users.presentation.itemsList

import androidx.recyclerview.widget.DiffUtil
import com.denis.github_users.presentation.itemsList.model.PresentationUserData

class DiffCallback : DiffUtil.ItemCallback<PresentationUserData>() {
    override fun areItemsTheSame(
        oldItem: PresentationUserData,
        newItem: PresentationUserData
    ): Boolean {
        return oldItem.id == newItem.id
                && oldItem.login.equals(
            newItem.login,
            false,
        )
                && oldItem.avatar_url.equals(
            newItem.avatar_url,
            false
        )
    }

    override fun areContentsTheSame(
        oldItem: PresentationUserData,
        newItem: PresentationUserData
    ): Boolean {
        return oldItem == newItem
    }
}