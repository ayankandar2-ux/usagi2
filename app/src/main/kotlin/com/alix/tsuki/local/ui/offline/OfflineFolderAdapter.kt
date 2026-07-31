package com.alix.tsuki.local.ui.offline

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alix.tsuki.databinding.ItemOfflineFolderBinding
import tsuki.model.Manga

class OfflineFolderAdapter(
	private val onItemClick: (Manga) -> Unit,
	private val onItemLongClick: (Manga) -> Unit,
) : ListAdapter<Manga, OfflineFolderAdapter.ViewHolder>(DiffCallback) {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val binding = ItemOfflineFolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return ViewHolder(binding)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		holder.bind(getItem(position))
	}

	inner class ViewHolder(private val binding: ItemOfflineFolderBinding) : RecyclerView.ViewHolder(binding.root) {

		init {
			binding.root.setOnClickListener {
				val position = bindingAdapterPosition
				if (position != RecyclerView.NO_POSITION) {
					onItemClick(getItem(position))
				}
			}
			binding.root.setOnLongClickListener {
				val position = bindingAdapterPosition
				if (position != RecyclerView.NO_POSITION) {
					onItemLongClick(getItem(position))
					true
				} else {
					false
				}
			}
		}

		fun bind(manga: Manga) {
			binding.textViewTitle.text = manga.title
			binding.imageViewCover.setImageAsync(manga.coverUrl, manga)
		}
	}

	private object DiffCallback : DiffUtil.ItemCallback<Manga>() {
		override fun areItemsTheSame(oldItem: Manga, newItem: Manga) = oldItem.id == newItem.id
		override fun areContentsTheSame(oldItem: Manga, newItem: Manga) = oldItem == newItem
	}
}
