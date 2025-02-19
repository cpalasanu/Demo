package ro.smeq.demo.ui.master

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_master.*
import ro.smeq.demo.MyApp
import ro.smeq.demo.R
import ro.smeq.demo.ui.MainActivity
import timber.log.Timber
import javax.inject.Inject

class MasterFragment : Fragment() {
    private val disposable = CompositeDisposable()
    private val adapter = Adapter().apply { setHasStableIds(true) }

    @Inject
    lateinit var presenter: MasterPresenter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as MyApp).applicationComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_master, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recycler_view.layoutManager = LinearLayoutManager(context)
        recycler_view.adapter = adapter

        adapter.clickListener = {
            if (activity is MainActivity) {
                (activity as MainActivity).onListItemClick(it)
            }
        }
        adapter.deleteCallback = {
            disposable.add(
                presenter.deletePost(it.id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { Timber.d("Post ${it.id} deleted") },
                        Timber::e
                    )
            )
        }

        swipe_to_refresh.setOnRefreshListener {
            disposable.add(
                presenter.sync()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { swipe_to_refresh.isRefreshing = false },
                        { throwable ->
                            swipe_to_refresh.isRefreshing = false
                            Timber.e(throwable)
                        })
            )
        }

        search_view.setOnSearchClickListener {
            tv_title.visibility = View.GONE
        }

        search_view.setOnCloseListener {
            tv_title.visibility = View.VISIBLE
            search(null)

            return@setOnCloseListener false
        }

        search_view.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                search(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // nothing, only search on submit
                return true
            }
        })
    }

    private fun search(searchStr: String?) {
        disposable.add(
            presenter.searchInPosts(searchStr)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Timber.i("Posts: $it")
                    adapter.submitList(it)
                }, Timber::e)
        )
    }

    override fun onStart() {
        super.onStart()
        disposable.add(
            presenter.getPosts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Timber.i("Posts: $it")
                    adapter.submitList(it)
                }, Timber::e)
        )
    }

    override fun onStop() {
        disposable.clear()

        // clear SearchView
        search_view.setQuery("", false)
        search_view.isIconified = true

        super.onStop()
    }

    class Adapter : RecyclerView.Adapter<VH>() {
        private var items: List<ListItem>? = null
        var clickListener: ((ListItem) -> Unit)? = null
        var deleteCallback: ((ListItem) -> Unit)? = null

        fun submitList(newList: List<ListItem>?) {
            items = newList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(
                LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
            )
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            items?.get(position)?.let { listItem ->
                holder.tvTitle.text = listItem.title
                holder.tvEmail.text = listItem.email
                holder.itemView.setOnClickListener {
                    clickListener?.invoke(listItem)
                }
                holder.ivDelete.setOnClickListener {
                    deleteCallback?.invoke(listItem)
                }
            }
        }

        override fun getItemCount() = items?.size ?: 0

        override fun getItemId(position: Int): Long {
            return items?.get(position)?.id ?: 0
        }
    }

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tv_title)
        val tvEmail: TextView = view.findViewById(R.id.tv_email)
        val ivDelete: ImageView = view.findViewById(R.id.iv_delete)
    }
}
