package com.alix.tsuki.tracker.ui.debug

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import dagger.hilt.android.AndroidEntryPoint
import com.alix.tsuki.core.nav.router
import com.alix.tsuki.core.ui.BaseActivity
import com.alix.tsuki.core.ui.BaseListAdapter
import com.alix.tsuki.core.ui.list.OnListItemClickListener
import com.alix.tsuki.core.util.ext.consumeAllSystemBarsInsets
import com.alix.tsuki.core.util.ext.observe
import com.alix.tsuki.core.util.ext.systemBarsInsets
import com.alix.tsuki.databinding.ActivityTrackerDebugBinding
import com.alix.tsuki.list.ui.adapter.ListItemType
import com.alix.tsuki.list.ui.adapter.TypedListSpacingDecoration

@AndroidEntryPoint
class TrackerDebugActivity : BaseActivity<ActivityTrackerDebugBinding>(), OnListItemClickListener<TrackDebugItem> {

	private val viewModel by viewModels<TrackerDebugViewModel>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(ActivityTrackerDebugBinding.inflate(layoutInflater))
		setDisplayHomeAsUp(isEnabled = true, showUpAsClose = false)
		val tracksAdapter = BaseListAdapter<TrackDebugItem>()
			.addDelegate(ListItemType.FEED, trackDebugAD(this))
		with(viewBinding.recyclerView) {
			setHasFixedSize(true)
			adapter = tracksAdapter
			addItemDecoration(TypedListSpacingDecoration(context, false))
		}
		viewModel.content.observe(this, tracksAdapter)
	}

	override fun onApplyWindowInsets(v: View, insets: WindowInsetsCompat): WindowInsetsCompat {
		val barsInsets = insets.systemBarsInsets
		viewBinding.recyclerView.updatePadding(
			left = barsInsets.left,
			right = barsInsets.right,
			bottom = barsInsets.bottom,
		)
		viewBinding.appbar.updatePadding(
			left = barsInsets.left,
			right = barsInsets.right,
			top = barsInsets.top,
		)
		return insets.consumeAllSystemBarsInsets()
	}

	override fun onItemClick(item: TrackDebugItem, view: View) {
		router.openDetails(item.manga)
	}
}
