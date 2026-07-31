package com.alix.tsuki.stats.ui

import android.content.res.ColorStateList
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.alix.tsuki.R
import com.alix.tsuki.core.ui.list.OnListItemClickListener
import com.alix.tsuki.core.util.UsagiColors
import com.alix.tsuki.databinding.ItemStatsBinding
import tsuki.model.Manga
import com.alix.tsuki.stats.domain.StatsRecord

fun statsAD(
	listener: OnListItemClickListener<Manga>,
) = adapterDelegateViewBinding<StatsRecord, StatsRecord, ItemStatsBinding>(
	{ layoutInflater, parent -> ItemStatsBinding.inflate(layoutInflater, parent, false) },
) {

	binding.root.setOnClickListener { v ->
		listener.onItemClick(item.manga ?: return@setOnClickListener, v)
	}

	bind {
		binding.textViewTitle.text = item.manga?.title ?: getString(R.string.other_manga)
		binding.textViewSummary.text = item.time.format(context.resources)
		binding.imageViewBadge.imageTintList = ColorStateList.valueOf(UsagiColors.ofManga(context, item.manga))
		binding.root.isClickable = item.manga != null
	}
}
