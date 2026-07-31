package com.alix.tsuki.list.ui.adapter

import com.alix.tsuki.list.domain.ListFilterOption

interface QuickFilterClickListener {

	fun onFilterOptionClick(option: ListFilterOption)
}
