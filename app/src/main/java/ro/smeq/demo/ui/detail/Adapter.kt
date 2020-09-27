package ro.smeq.demo.ui.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ro.smeq.demo.R
import ro.smeq.demo.ui.stickyheaders.StickyHeaderItemDecoration

class Adapter : RecyclerView.Adapter<VH>(), StickyHeaderItemDecoration.StickyHeaderInterface {
    private var items: MutableList<ListItem>? = null
    var clickListener: ((ListItem) -> Unit)? = null

    fun submitList(newList: MutableList<ListItem>?) {
        items = newList
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items!![position]) {
            is HeaderListItem -> R.layout.item_detail_header
            is AlbumListItem -> R.layout.item_detail_album
            is PhotoListItem -> R.layout.item_detail_photo
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.item_detail_header -> VH.HeaderVH(
                inflater.inflate(
                    R.layout.item_detail_header,
                    parent,
                    false
                )
            )
            R.layout.item_detail_album -> VH.AlbumVH(
                inflater.inflate(
                    R.layout.item_detail_album,
                    parent,
                    false
                )
            )
            R.layout.item_detail_photo -> VH.PhotoVH(
                inflater.inflate(
                    R.layout.item_detail_photo,
                    parent,
                    false
                )
            )
            else -> throw IllegalStateException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        when (holder) {
            is VH.HeaderVH -> {
                val headerListItem = items!![position] as HeaderListItem
                holder.bindData(headerListItem)
            }
            is VH.AlbumVH -> {
                val albumListItem = items!![position] as AlbumListItem
                holder.bindData(albumListItem) {
                    toggleAlbumExpand(
                        albumListItem,
                        position,
                        holder.ivDropDown,
                        false
                    )
                }
            }
            is VH.PhotoVH -> {
                val photoListItem = items!![position] as PhotoListItem
                holder.bindData(photoListItem)
            }
        }
    }

    private fun toggleAlbumExpand(
        albumListItem: AlbumListItem,
        position: Int,
        ivDropDown: ImageView,
        isStickyHeader: Boolean     // if isStickyHeader we include the album header to update the dropdown icon
    ) {
        if (albumListItem.isExpanded) {
            albumListItem.isExpanded = false
            items?.removeAll(albumListItem.photos)
            notifyItemRangeRemoved(if (isStickyHeader) position else position + 1, albumListItem.photos.size)
            ivDropDown.setImageResource(R.drawable.ic_drop_down)
        } else {
            albumListItem.isExpanded = true
            items?.addAll(position + 1, albumListItem.photos)
            notifyItemRangeInserted(if (isStickyHeader) position else position + 1, albumListItem.photos.size)
            ivDropDown.setImageResource(R.drawable.ic_drop_up)
        }
    }

    override fun getItemCount() = items?.size ?: 0

    override fun getItemId(position: Int): Long {
        return items?.get(position)?.hashCode()?.toLong() ?: 0
    }

    override fun getHeaderPositionForItem(itemPosition: Int): Int {
        if (itemPosition < 0 || itemPosition >= items!!.size) {
            throw IllegalStateException("Item out of range")
        }

        for (position in itemPosition downTo 0) {
            if (isHeader(position)) return position
        }

        return -1
    }

    override fun getHeaderLayout(headerPosition: Int): Int {
        return R.layout.item_detail_album
    }

    override fun bindHeaderData(header: View?, headerPosition: Int) {
        if (header != null && headerPosition >= 0) {
            val albumListItem = items!![headerPosition] as AlbumListItem
            val holder = VH.AlbumVH(header)
            holder.bindData(albumListItem) {
                toggleAlbumExpand(
                    albumListItem,
                    headerPosition,
                    holder.ivDropDown,
                    true
                )
            }
        }
    }

    override fun isHeader(itemPosition: Int): Boolean {
        return items!![itemPosition] is AlbumListItem
    }
}

sealed class VH(view: View) : RecyclerView.ViewHolder(view) {
    class HeaderVH(view: View) : VH(view) {
        val tvTitle: TextView = view.findViewById(R.id.tv_title)
        val tvBody: TextView = view.findViewById(R.id.tv_body)

        fun bindData(headerListItem: HeaderListItem) {
            tvTitle.text = headerListItem.title
            tvBody.text = headerListItem.body
        }
    }

    class AlbumVH(view: View) : VH(view) {
        private val tvTitle: TextView = view.findViewById(R.id.tv_album_title)
        val ivDropDown: ImageView = view.findViewById(R.id.iv_dropdown)

        fun bindData(albumListItem: AlbumListItem, onClickListener: View.OnClickListener?) {
            tvTitle.text = albumListItem.title
            itemView.setOnClickListener(onClickListener)

            if (albumListItem.isExpanded) {
                ivDropDown.setImageResource(R.drawable.ic_drop_up)
            } else {
                ivDropDown.setImageResource(R.drawable.ic_drop_down)
            }
        }
    }

    class PhotoVH(view: View) : VH(view) {
        val textView: TextView = view.findViewById(R.id.text_view)
        val imageView: ImageView = view.findViewById(R.id.image_view)

        fun bindData(photoListItem: PhotoListItem) {
            textView.text = photoListItem.title
            Picasso.get()
                .load(photoListItem.imgUrl)
                .placeholder(R.drawable.img_placeholder)
                .into(imageView)
        }
    }
}
