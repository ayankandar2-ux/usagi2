package com.alix.tsuki.reader.ui

import com.alix.tsuki.bookmarks.domain.Bookmark
import tsuki.model.MangaChapter
import com.alix.tsuki.reader.ui.pager.ReaderPage

interface ReaderNavigationCallback {

	fun onPageSelected(page: ReaderPage): Boolean

	fun onChapterSelected(chapter: MangaChapter): Boolean

	fun onBookmarkSelected(bookmark: Bookmark): Boolean = onPageSelected(
		ReaderPage(bookmark.toMangaPage(), bookmark.page, bookmark.chapterId),
	)
}
