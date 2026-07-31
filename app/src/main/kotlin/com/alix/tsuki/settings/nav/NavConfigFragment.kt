package com.alix.tsuki.settings.nav

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import com.alix.tsuki.R
import com.alix.tsuki.core.prefs.NavItem
import com.alix.tsuki.core.ui.BaseFragment
import com.alix.tsuki.core.ui.BaseListAdapter
import com.alix.tsuki.core.ui.dialog.buildAlertDialog
import com.alix.tsuki.core.ui.dialog.setRecyclerViewList
import com.alix.tsuki.core.ui.list.OnListItemClickListener
import com.alix.tsuki.core.ui.util.RecyclerViewOwner
import com.alix.tsuki.core.util.ext.consumeAllSystemBarsInsets
import com.alix.tsuki.core.util.ext.container
import com.alix.tsuki.core.util.ext.end
import com.alix.tsuki.core.util.ext.observe
import com.alix.tsuki.core.util.ext.start
import com.alix.tsuki.core.util.ext.systemBarsInsets
import com.alix.tsuki.databinding.FragmentSettingsSourcesBinding
import com.alix.tsuki.list.ui.adapter.ListItemType
import com.alix.tsuki.list.ui.model.ListModel
import com.alix.tsuki.settings.nav.adapter.navAddAD
import com.alix.tsuki.settings.nav.adapter.navAvailableAD
import com.alix.tsuki.settings.nav.adapter.navConfigAD
import com.alix.tsuki.settings.SettingsActivity

@AndroidEntryPoint
class NavConfigFragment : BaseFragment<FragmentSettingsSourcesBinding>(), RecyclerViewOwner,
	OnListItemClickListener<NavItem>, View.OnClickListener {

	private var reorderHelper: ItemTouchHelper? = null
	private val viewModel by viewModels<NavConfigViewModel>()

	override val recyclerView: RecyclerView?
		get() = viewBinding?.recyclerView

	override fun onCreateViewBinding(
		inflater: LayoutInflater,
		container: ViewGroup?,
	): FragmentSettingsSourcesBinding {
		return FragmentSettingsSourcesBinding.inflate(inflater, container, false)
	}

	override fun onViewBindingCreated(
		binding: FragmentSettingsSourcesBinding,
		savedInstanceState: Bundle?,
	) {
		super.onViewBindingCreated(binding, savedInstanceState)
		binding.fabImport.visibility = View.GONE
		val navConfigAdapter = BaseListAdapter<ListModel>()
			.addDelegate(ListItemType.NAV_ITEM, navConfigAD(this))
			.addDelegate(ListItemType.FOOTER_LOADING, navAddAD(this))
		with(binding.recyclerView) {
			setHasFixedSize(true)
			adapter = navConfigAdapter
			reorderHelper = ItemTouchHelper(ReorderCallback()).also {
				it.attachToRecyclerView(this)
			}
		}
		viewModel.content.observe(viewLifecycleOwner, navConfigAdapter)
	}

	override fun onApplyWindowInsets(v: View, insets: WindowInsetsCompat): WindowInsetsCompat {
		val barsInsets = insets.systemBarsInsets
		val isTablet = !resources.getBoolean(R.bool.is_tablet)
		val isMaster = container?.id == R.id.container_master
		v.setPaddingRelative(
			if (isTablet && !isMaster) 0 else barsInsets.start(v),
			0,
			if (isTablet && isMaster) 0 else barsInsets.end(v),
			barsInsets.bottom,
		)
		return insets.consumeAllSystemBarsInsets()
	}

	override fun onResume() {
		super.onResume()
		(activity as? SettingsActivity)?.setSectionTitle(getString(R.string.main_screen_sections))
	}

	override fun onDestroyView() {
		reorderHelper = null
		super.onDestroyView()
	}

	override fun onClick(v: View) {
		var dialog: DialogInterface? = null
		val listener = OnListItemClickListener<NavItem> { item, _ ->
			viewModel.addItem(item)
			dialog?.dismiss()
		}
		dialog = buildAlertDialog(v.context) {
			setTitle(R.string.add)
			setCancelable(true)
			setRecyclerViewList(viewModel.availableItems, navAvailableAD(listener))
			setNegativeButton(android.R.string.cancel, null)
		}.apply { show() }
	}

	override fun onItemClick(item: NavItem, view: View) {
		viewModel.removeItem(item)
	}

	override fun onItemLongClick(item: NavItem, view: View): Boolean {
		val holder = viewBinding?.recyclerView?.findContainingViewHolder(view) ?: return false
		reorderHelper?.startDrag(holder)
		return true
	}

	private inner class ReorderCallback : ItemTouchHelper.SimpleCallback(
		ItemTouchHelper.DOWN or ItemTouchHelper.UP,
		0,
	) {

		override fun onMove(
			recyclerView: RecyclerView,
			viewHolder: RecyclerView.ViewHolder,
			target: RecyclerView.ViewHolder,
		): Boolean = target.itemViewType == ListItemType.NAV_ITEM.ordinal

		override fun onMoved(
			recyclerView: RecyclerView,
			viewHolder: RecyclerView.ViewHolder,
			fromPos: Int,
			target: RecyclerView.ViewHolder,
			toPos: Int,
			x: Int,
			y: Int,
		) {
			super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)
			viewModel.reorder(fromPos, toPos)
		}

		override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) = Unit

		override fun isLongPressDragEnabled() = false
	}
}
