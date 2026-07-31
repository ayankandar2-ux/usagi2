package com.alix.tsuki.list.ui.adapter

import android.view.View
import com.alix.tsuki.list.ui.model.ListHeader

interface ListHeaderClickListener {

	fun onListHeaderClick(item: ListHeader, view: View)
}
