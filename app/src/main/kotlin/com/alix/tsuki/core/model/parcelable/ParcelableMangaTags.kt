package com.alix.tsuki.core.model.parcelable

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler
import com.alix.tsuki.core.model.MangaSource
import tsuki.model.MangaTag

object MangaTagParceler : Parceler<MangaTag> {
	override fun create(parcel: Parcel) = MangaTag(
		title = requireNotNull(parcel.readString()),
		key = requireNotNull(parcel.readString()),
		source = MangaSource(parcel.readString()),
	)

	override fun MangaTag.write(parcel: Parcel, flags: Int) {
		parcel.writeString(title)
		parcel.writeString(key)
		parcel.writeString(source.name)
	}
}

@Parcelize
@TypeParceler<MangaTag, MangaTagParceler>
data class ParcelableMangaTags(val tags: Set<MangaTag>) : Parcelable
