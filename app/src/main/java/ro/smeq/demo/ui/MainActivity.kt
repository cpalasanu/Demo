package ro.smeq.demo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import ro.smeq.demo.R
import ro.smeq.demo.ui.detail.DetailFragment
import ro.smeq.demo.ui.master.ListItem
import ro.smeq.demo.ui.master.MasterFragment

class MainActivity : AppCompatActivity() {
    private var twoPane = false

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
    }

    fun onListItemClick(listItem: ListItem) {
        if (twoPane) {
            val detailFragment = supportFragmentManager.findFragmentById(R.id.detail_fragment) as DetailFragment
            detailFragment.updatePost(listItem.id)
        } else {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                .replace(R.id.fragment_container, DetailFragment().apply {
                    arguments = Bundle().apply {
                        putLong(DetailFragment.KEY_POST_ID, listItem.id)
                    }
                })
                .addToBackStack(null)
                .commit()
        }
    }
}
