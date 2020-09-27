package ro.smeq.demo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import ro.smeq.demo.R
import ro.smeq.demo.ui.detail.DetailFragment
import ro.smeq.demo.ui.master.ListItem
import ro.smeq.demo.ui.master.MasterFragment
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private var twoPane = false
    private var postId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (fragment_container != null) {
            // single pane mod
            if (savedInstanceState != null) return

            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, MasterFragment())
                .commit()
        } else {
            twoPane = true
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_POST_ID)) {
            postId = savedInstanceState.getLong(KEY_POST_ID)
            showDetails(postId!!)
        }
    }

    fun onListItemClick(listItem: ListItem) {
        postId = listItem.id
        showDetails(listItem.id)
    }

    fun syncPostId(postId: Long) {
        this.postId = postId
    }

    private fun showDetails(postId: Long) {
        Timber.i("showDetails($postId)")
        if (twoPane) {
            val detailFragment = supportFragmentManager.findFragmentById(R.id.detail_fragment) as DetailFragment
            detailFragment.updatePost(postId)
        } else {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                .replace(R.id.fragment_container, DetailFragment().apply {
                    arguments = Bundle().apply {
                        putLong(DetailFragment.KEY_POST_ID, postId)
                    }
                })
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Timber.i("onSaveInstanceState postId=$postId")
        postId?.let { outState.putLong(KEY_POST_ID, it) }
    }

    companion object {
        const val KEY_POST_ID = "post_id"
    }
}
