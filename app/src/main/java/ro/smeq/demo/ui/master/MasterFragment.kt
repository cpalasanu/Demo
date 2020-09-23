package ro.smeq.demo.ui.master

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_master.*
import ro.smeq.demo.R
import ro.smeq.demo.ui.MainActivity

class MasterFragment : Fragment() {
    private val adapter = Adapter()

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
//        recycler_view.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

        adapter.clickListener = {
            if (activity is MainActivity) {
                (activity as MainActivity).onListItemClick(it)
            }
        }
        adapter.submitList(listOf(
            ListItem(1,"Test", "test@yahoo.com"),
            ListItem(1,"This", "test@gmail.com"),
            ListItem(1,"List", "test@hotmal.com"),
            ListItem(1,"Test", "test@yahoo.com"),
            ListItem(1,"This", "test@gmail.com"),
            ListItem(1,"List", "test@hotmal.com"),
            ListItem(1,"Test", "test@yahoo.com"),
            ListItem(1,"This", "test@gmail.com"),
            ListItem(1,"List", "test@hotmal.com"),
        ))
    }

    class Adapter: RecyclerView.Adapter<VH>() {
        private var items: List<ListItem>? = null
        var clickListener: ((ListItem) -> Unit)? = null

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
            }
        }

        override fun getItemCount() = items?.size ?: 0
    }

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tv_title)
        val tvEmail: TextView = view.findViewById(R.id.tv_email)
    }
}
