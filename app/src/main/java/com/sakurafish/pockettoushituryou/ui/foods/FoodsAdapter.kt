package com.sakurafish.pockettoushituryou.ui.foods

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.WorkerThread
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.sakurafish.pockettoushituryou.R
import com.sakurafish.pockettoushituryou.databinding.ItemFoodBinding
import com.sakurafish.pockettoushituryou.di.module.IoDispatcher
import com.sakurafish.pockettoushituryou.di.module.MainDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FoodsAdapter(
    @IoDispatcher
    private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher
    private val mainDispatcher: CoroutineDispatcher,
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val items = ArrayList<FoodItemViewModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_food,
                parent,
                false
            ), lifecycleOwner
        )
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val viewModel = items[position]
        val holder = viewHolder as ViewHolder
        if (viewModel.isExpanded) {
            holder.binding.expandArrow.isSelected = true
            holder.binding.expandableLayout.expand(true)
        } else {
            holder.binding.expandArrow.isSelected = false
            holder.binding.expandableLayout.collapse(true)
        }

        viewModel.setExpandListener(View.OnClickListener {
            // collapse or expand card
            if (viewModel.isExpanded) {
                holder.binding.expandArrow.isSelected = false
                holder.binding.expandableLayout.collapse(true)
                viewModel.isExpanded = false
            } else {
                holder.binding.expandArrow.isSelected = true
                holder.binding.expandableLayout.expand(true)
                viewModel.isExpanded = true
            }
        })

        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun refreshFavoriteStatus() {
        GlobalScope.launch {
            updateFavoritesView()
        }
    }

    @WorkerThread
    suspend fun updateFavoritesView() {
        withContext(ioDispatcher) {
            items.forEach(FoodItemViewModel::refreshFavoriteStatus)
            withContext(mainDispatcher) {
                notifyDataSetChanged()
            }
        }
    }

    inner class ViewHolder(
        val binding: ItemFoodBinding,
        private val lifecycleOwner: LifecycleOwner
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FoodItemViewModel) {
            binding.viewModel = item
            binding.lifecycleOwner = lifecycleOwner
        }
    }
}