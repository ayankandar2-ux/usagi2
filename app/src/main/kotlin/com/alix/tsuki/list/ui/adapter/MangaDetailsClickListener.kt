package com.alix.tsuki.list.ui.adapter

import android.view.View
import com.alix.tsuki.core.ui.list.OnListItemClickListener
import com.alix.tsuki.list.ui.model.MangaListModel
import tsuki.model.Manga
import tsuki.model.MangaTag

interface MangaDetailsClickListener : OnListItemClickListener<MangaListModel> {

	fun onReadClick(manga: Manga, view: View)

	fun onTagClick(manga: Manga, tag: MangaTag, view: View)
}
