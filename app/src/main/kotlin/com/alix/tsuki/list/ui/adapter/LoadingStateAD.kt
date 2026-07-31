package com.alix.tsuki.list.ui.adapter

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegate
import com.alix.tsuki.R
import com.alix.tsuki.list.ui.model.ListModel
import com.alix.tsuki.list.ui.model.LoadingState

fun loadingStateAD() = adapterDelegate<LoadingState, ListModel>(R.layout.item_loading_state) {
}