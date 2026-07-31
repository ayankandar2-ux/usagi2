package com.alix.tsuki.widget.shelf.adapter

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.alix.tsuki.R
import com.alix.tsuki.core.ui.list.OnListItemClickListener
import com.alix.tsuki.databinding.ItemCategoryCheckableSingleBinding
import com.alix.tsuki.widget.shelf.model.CategoryItem

fun categorySelectItemAD(
	clickListener: OnListItemClickListener<CategoryItem>
) = adapterDelegateViewBinding<CategoryItem, CategoryItem, ItemCategoryCheckableSingleBinding>(
	{ inflater, parent -> ItemCategoryCheckableSingleBinding.inflate(inflater, parent, false) },
) {

	itemView.setOnClickListener {
		clickListener.onItemClick(item, it)
	}

	bind {
		with(binding.checkedTextView) {
			text = item.name ?: getString(R.string.all_favourites)
			isChecked = item.isSelected
		}
	}
}
