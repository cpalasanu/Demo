package ro.smeq.demo.ui.detail

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.recycler_view.*
import ro.smeq.demo.MyApp
import ro.smeq.demo.R
import ro.smeq.demo.repository.Repository
import timber.log.Timber
import java.lang.IllegalStateException
import javax.inject.Inject

class DetailFragment : Fragment() {
    private val disposable = CompositeDisposable()
    private val adapter = Adapter().apply { setHasStableIds(true) }

    @Inject
    lateinit var repository: Repository

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as MyApp).applicationComponent.inject(this)
    }

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

        arguments?.getLong(KEY_POST_ID)?.let {
            updatePost(it)
        }
    }

    fun updatePost(postId: Long) {
        disposable.add(
            repository.post(postId)
                .map { postWithAlbums ->
                    val adapterList = ArrayList<ListItem>()
                    adapterList.add(HeaderListItem(postWithAlbums.post.id, postWithAlbums.post.title, postWithAlbums.post.body))
                    postWithAlbums.albumsWithPhotos.forEach { albumWithPhotos ->
                        adapterList.add(AlbumListItem(albumWithPhotos.album.id, albumWithPhotos.album.title))
                        albumWithPhotos.photos.forEach { photo ->
                            adapterList.add(PhotoListItem(photo.id, photo.title, photo.url))
                        }
                    }

                    return@map adapterList
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { adapter.submitList(it) },
                    Timber::e
                )
        )
    }

    override fun onStop() {
        disposable.clear()
        super.onStop()
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

        override fun getItemId(position: Int): Long {
            return items?.get(position)?.hashCode()?.toLong() ?: 0
        }
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

    companion object {
        const val KEY_POST_ID = "post_id"
    }
}
