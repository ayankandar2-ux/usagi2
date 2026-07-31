package com.alix.tsuki.tracker.data

import com.alix.tsuki.core.db.entity.toManga
import com.alix.tsuki.core.db.entity.toMangaTags
import com.alix.tsuki.tracker.domain.model.TrackingLogItem
import java.time.Instant

fun TrackLogWithManga.toTrackingLogItem(): TrackingLogItem {
	val chaptersList = trackLog.chapters.split('\n').filterNot { x -> x.isEmpty() }
	return TrackingLogItem(
		id = trackLog.id,
		chapters = chaptersList,
		manga = manga.toManga(tags.toMangaTags(), null),
		createdAt = Instant.ofEpochMilli(trackLog.createdAt),
		isNew = trackLog.isUnread,
	)
}
