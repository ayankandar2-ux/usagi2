package com.alix.tsuki.explore.ui.adapter

import android.view.View
import com.alix.tsuki.list.ui.adapter.ListHeaderClickListener
import com.alix.tsuki.list.ui.adapter.ListStateHolderListener

interface ExploreListEventListener : ListStateHolderListener, View.OnClickListener, ListHeaderClickListener
