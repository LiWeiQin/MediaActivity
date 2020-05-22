package com.zkxt.mediausing.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arcns.core.APP
import com.arcns.core.media.selector.EMedia
import com.arcns.core.util.openAppByPath
import com.arcns.core.util.openAppByUri
import com.zkxt.mediausing.R
import com.zkxt.mediausing.databinding.LayoutRecyclewItemMediaEmptyBinding
import com.zkxt.mediausing.databinding.LayoutRecyclewItemMediaSelectorBinding
import com.zkxt.mediausing.viewmodel.ViewModelActivityMain

class MediaSelectorAdapter(
    val viewModel: ViewModelActivityMain,
    var isShowAddItem: Boolean = true
) :
    ListAdapter<EMedia, RecyclerView.ViewHolder>(
        diffCallback
    ) {

    override fun getItemCount(): Int {
        return super.getItemCount() + if (isShowAddItem) 1 else 0
    }

    fun hasMediaDataAddItem(hasMediaDataAddItem: Boolean) {
        val hadMediaDataAddItem = isShowAddItem
        this.isShowAddItem = hasMediaDataAddItem
        if (hadMediaDataAddItem != hasMediaDataAddItem) {
            if (hadMediaDataAddItem) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isShowAddItem && position == itemCount - 1)
            R.layout.layout_recyclew_item_media_empty
        else R.layout.layout_recyclew_item_media_selector
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.layout_recyclew_item_media_selector -> MediaSelectorViewHolder(
                LayoutRecyclewItemMediaSelectorBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ).apply {
                    viewModel = this@MediaSelectorAdapter.viewModel
                })
            R.layout.layout_recyclew_item_media_empty -> MediaSelectorAddViewHolder(
                LayoutRecyclewItemMediaEmptyBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ).apply {
                    viewModel = this@MediaSelectorAdapter.viewModel
                })
            else -> throw  Exception("error viewHolder")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MediaSelectorViewHolder -> holder.bindTo(getItem(position))
            is MediaSelectorAddViewHolder -> holder.bindTo()
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<EMedia>() {
            override fun areItemsTheSame(oldItem: EMedia, newItem: EMedia): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: EMedia, newItem: EMedia): Boolean =
                oldItem == newItem
        }
    }
}


class MediaSelectorViewHolder(var binding: LayoutRecyclewItemMediaSelectorBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bindTo(item: EMedia) {
        binding.item = item
    }
}

class MediaSelectorAddViewHolder(var binding: LayoutRecyclewItemMediaEmptyBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bindTo() {
    }
}

@BindingAdapter(
    value = ["bindMediaSelectorViewModel", "bindMediaDataSelector", "bindMediaDataShowAddItem"],
    requireAll = false
)
fun bindMediaData(
    recyclerView: RecyclerView,
    viewModel: ViewModelActivityMain,
    data: List<EMedia>?,
    isShowAddItem: Boolean?
) {
    if (recyclerView.adapter == null) {
        recyclerView.adapter = MediaSelectorAdapter(viewModel, isShowAddItem ?: false)
        // 实现拖动排序
        ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                if (viewHolder.adapterPosition == (recyclerView.adapter as MediaSelectorAdapter).itemCount - 1
                    && (recyclerView.adapter as MediaSelectorAdapter).isShowAddItem
                ) {
                    return makeMovementFlags(
                        // 拖动方向
                        0,
                        // 滑动方向
                        0
                    )
                }
                return makeMovementFlags(
                    // 拖动方向
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.DOWN or ItemTouchHelper.UP,
                    // 滑动方向
                    0
                )
            }

            override fun isLongPressDragEnabled(): Boolean = true

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                if (target.adapterPosition == (recyclerView.adapter as MediaSelectorAdapter).itemCount - 1
                    && (recyclerView.adapter as MediaSelectorAdapter).isShowAddItem
                ) return false
                viewModel.onSortingSelectedMedias(
                    fromPosition = viewHolder.adapterPosition,
                    toPosition = target.adapterPosition
                )
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            }
        }).attachToRecyclerView(
            recyclerView
        )
    }
    (recyclerView.adapter as MediaSelectorAdapter).hasMediaDataAddItem(isShowAddItem ?: false)
    (recyclerView.adapter as MediaSelectorAdapter).submitList(data ?: arrayListOf<EMedia>())
}


/**
 * 设置点击控件打开相应媒体文件对应的app
 */
@BindingAdapter(
    value = [
        "bindMediaClickOpenApp"
    ],
    requireAll = true
)
fun bindMediaClickOpenApp(
    view: View,
    currentMedia: EMedia
) {
    view.setOnClickListener {
        when (currentMedia.value) {
            is Uri -> view.context.openAppByUri(
                currentMedia.value as Uri,
                currentMedia.mimeTypeIfNullGetOfSuffix
            )
            is String -> view.context.openAppByPath(
                currentMedia.value as String,
                currentMedia.mimeTypeIfNullGetOfSuffix,
                APP.fileProviderAuthority ?: return@setOnClickListener
            )
        }
    }
}

@BindingAdapter(
    value = [
        "bindImageViewLongClick"
    ],
    requireAll = true
)
fun bindImageViewLongClick(
    view: ImageView,
    listener: () -> Unit
) = view.setOnLongClickListener {
    listener.invoke()
    return@setOnLongClickListener true
}











