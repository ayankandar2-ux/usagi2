package com.alix.tsuki.filter.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import com.alix.tsuki.core.model.MangaSourceSerializer
import tsuki.model.MangaListFilter
import tsuki.model.MangaSource

@Serializable
@JsonIgnoreUnknownKeys
data class PersistableFilter(
    @SerialName("name")
    val name: String,
    @Serializable(with = MangaSourceSerializer::class)
    @SerialName("source")
    val source: MangaSource,
    @Serializable(with = MangaListFilterSerializer::class)
    @SerialName("filter")
    val filter: MangaListFilter,
) {

    val id: Int
        get() = name.hashCode()

    companion object {

        const val MAX_TITLE_LENGTH = 18
    }
}
