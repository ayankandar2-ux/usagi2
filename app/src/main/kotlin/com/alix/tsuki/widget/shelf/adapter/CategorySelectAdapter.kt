package com.alix.tsuki.widget.shelf.adapter

import com.alix.tsuki.core.ui.BaseListAdapter
import com.alix.tsuki.core.ui.list.OnListItemClickListener
import com.alix.tsuki.widget.shelf.model.CategoryItem

class CategorySelectAdapter(
	clickListener: OnListItemClickListener<CategoryItem>
) : BaseListAdapter<CategoryItem>() {

	init {
		delegatesManager.addDelegate(categorySelectItemAD(clickListener))
	}
}
