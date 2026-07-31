package com.alix.tsuki.reader.ui

import com.alix.tsuki.reader.ui.pager.ReaderPage

data class ReaderContent(
	val pages: List<ReaderPage>,
	val state: ReaderState?
)