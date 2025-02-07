package ro.smeq.demo.ui.detail

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_detail.*
import ro.smeq.demo.MyApp
import ro.smeq.demo.R
import ro.smeq.demo.ui.MainActivity
import ro.smeq.demo.ui.stickyheaders.StickyHeaderItemDecoration
import timber.log.Timber
import javax.inject.Inject


class DetailFragment : Fragment() {
    private val disposable = CompositeDisposable()
    private val adapter = Adapter().apply { setHasStableIds(true) }

    @Inject
    lateinit var presenter: DetailPresenter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as MyApp).applicationComponent.inject(this)

        // fix for postId lost when changing screen orientation on phone
        if (context is MainActivity) {
            arguments?.getLong(KEY_POST_ID)?.let {
                context.syncPostId(it)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val gridColumns = calculateGridColumns()
        recycler_view.layoutManager = GridLayoutManager(context, gridColumns).apply {
            orientation = GridLayoutManager.VERTICAL
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (adapter.getItemViewType(position) == R.layout.item_detail_photo) 1
                    else gridColumns
                }
            }
        }
        recycler_view.adapter = adapter
        recycler_view.addItemDecoration(StickyHeaderItemDecoration(adapter, recycler_view))

        arguments?.getLong(KEY_POST_ID)?.let {
            updatePost(it)
        }
    }

    private fun calculateGridColumns(): Int {
        val metrics = DisplayMetrics()
        (context as MainActivity).windowManager.defaultDisplay.getMetrics(metrics)
        val widthDp = metrics.widthPixels / metrics.density

        return if (widthDp > MIN_WIDTH_FOR_3_COLUMNS) 3
        else 2
    }

    fun updatePost(postId: Long) {
        disposable.add(
            presenter.getPost(postId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        tv_title.text = "Post $postId"
                        adapter.submitList(it)
                        recycler_view.scrollToPosition(0)
                    },
                    Timber::e
                )
        )
    }

    override fun onStop() {
        disposable.clear()
        super.onStop()
    }

    companion object {
        const val KEY_POST_ID = "post_id"
        const val MIN_WIDTH_FOR_3_COLUMNS = 800
    }
}
