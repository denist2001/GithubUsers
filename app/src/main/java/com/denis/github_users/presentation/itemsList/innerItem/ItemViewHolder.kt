package com.denis.github_users.presentation.itemsList.innerItem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavDirections
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Scale
import coil.transform.RoundedCornersTransformation
import com.denis.github_users.R
import com.denis.github_users.databinding.InnerItemBinding
import com.denis.github_users.presentation.itemsList.ItemsFragmentDirections
import com.denis.github_users.presentation.itemsList.model.PresentationUserData
import com.denis.github_users.utils.clicks
import com.denis.github_users.utils.throttleFirst
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ItemViewHolder(
    itemView: View,
    private val clickFlowCollector: ((direction: NavDirections) -> Unit)?
) : RecyclerView.ViewHolder(itemView) {

    private var lastTime = 0L
    lateinit var binding: InnerItemBinding

    companion object {
        fun create(
            parent: ViewGroup,
            clickFlowCollector: ((direction: NavDirections) -> Unit)?
        ): ItemViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.inner_item, parent, false)
            return ItemViewHolder(view, clickFlowCollector)
        }
    }

    fun bind(item: PresentationUserData?) = with(itemView) {
        binding = InnerItemBinding.bind(this)
        if (item == null) return@with
        binding.pictureIv.load(item.avatar_url) {
            scale(Scale.FIT)
            placeholder(R.drawable.person_150)
            fallback(R.drawable.person_150)
            transformations(RoundedCornersTransformation(10f, 10f, 10f, 10f))
        }
        binding.loginTv.text = itemView.context.getString(R.string.login, item.login)
        binding.idNumberTv.text = itemView.context.getString(R.string.id_number, item.id)

        clicks().throttleFirst(1000L).onEach {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastTime >= 1000) {
                lastTime = currentTime
                val direction = ItemsFragmentDirections.actionItemsFragmentToDetailsFragment(
                    login = item.login,
                )
                clickFlowCollector?.let { collector -> collector(direction) }
            }
        }.launchIn(MainScope())
    }
}