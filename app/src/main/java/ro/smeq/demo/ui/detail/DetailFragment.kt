package ro.smeq.demo.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_view.*
import ro.smeq.demo.R
import java.lang.IllegalStateException

class DetailFragment : Fragment() {
    private val adapter = Adapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.recycler_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recycler_view.layoutManager = GridLayoutManager(context, 2).apply {
            orientation = GridLayoutManager.VERTICAL
            spanSizeLookup = object: GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (adapter.getItemViewType(position) == R.layout.item_detail_photo) 1
                    else 2
                }
            }
        }
        recycler_view.adapter = adapter

        adapter.submitList(
            listOf(
                HeaderListItem(0, "Header title", "Header body with a lot of text"),
                AlbumListItem(1, "Album title 1"),
                PhotoListItem(2, "Photo 1", "https://storage0.dms.mpinteractiv.ro/media/1/1/1687/19551751/1/firea-interviu-prf.jpg?width=560"),
                PhotoListItem(3, "Photo 2", "https://storage0.dms.mpinteractiv.ro/media/1/1/1687/19551751/1/firea-interviu-prf.jpg?width=560"),
                PhotoListItem(4, "Photo 3", "https://storage0.dms.mpinteractiv.ro/media/1/1/1687/19551751/1/firea-interviu-prf.jpg?width=560"),
                PhotoListItem(5, "Photo 4", "https://storage0.dms.mpinteractiv.ro/media/1/1/1687/19551751/1/firea-interviu-prf.jpg?width=560"),
                PhotoListItem(6, "Photo 5", "https://storage0.dms.mpinteractiv.ro/media/1/1/1687/19551751/1/firea-interviu-prf.jpg?width=560"),
                PhotoListItem(7, "Photo 6", "https://storage0.dms.mpinteractiv.ro/media/1/1/1687/19551751/1/firea-interviu-prf.jpg?width=560"),
            ))
    }

    fun updatePost(postId: Long) {
        // todo
    }

    class Adapter: RecyclerView.Adapter<VH>() {
        private var items: List<ListItem>? = null
        var clickListener: ((ListItem) -> Unit)? = null

        fun submitList(newList: List<ListItem>?) {
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
                R.layout.item_detail_header -> VH.HeaderVH(inflater.inflate(R.layout.item_detail_header, parent, false))
                R.layout.item_detail_album -> VH.AlbumVH(inflater.inflate(R.layout.item_detail_album, parent, false))
                R.layout.item_detail_photo -> VH.PhotoVH(inflater.inflate(R.layout.item_detail_photo, parent, false))
                else -> throw IllegalStateException("Unknown view type")
            }
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            when(holder) {
                is VH.HeaderVH -> {
                    val headerListItem = items!![position] as HeaderListItem
                    holder.tvTitle.text = headerListItem.title
                    holder.tvBody.text = headerListItem.body
                }
                is VH.AlbumVH -> {
                    val albumListItem = items!![position] as AlbumListItem
                    holder.tvTitle.text = albumListItem.title
                }
                is VH.PhotoVH -> {
                    val photoListItem = items!![position] as PhotoListItem
                    holder.textView.text = photoListItem.title
                    Picasso.get().load(photoListItem.imgUrl).into(holder.imageView)
                }
            }
        }

        override fun getItemCount() = items?.size ?: 0
    }

    sealed class VH(view: View) : RecyclerView.ViewHolder(view) {
        class HeaderVH(view: View): VH(view) {
            val tvTitle: TextView = view.findViewById(R.id.tv_title)
            val tvBody: TextView = view.findViewById(R.id.tv_body)
        }

        class AlbumVH(view: View): VH(view) {
            val tvTitle: TextView = view.findViewById(R.id.tv_album_title)
        }

        class PhotoVH(view: View): VH(view) {
            val textView: TextView = view.findViewById(R.id.text_view)
            val imageView: ImageView = view.findViewById(R.id.image_view)
        }
    }
}
