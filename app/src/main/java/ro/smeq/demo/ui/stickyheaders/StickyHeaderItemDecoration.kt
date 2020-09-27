package ro.smeq.demo.ui.stickyheaders

import android.graphics.Canvas
import android.view.*
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

/**
 * Adapted from: https://github.com/saber-solooki/StickyHeader to allow items without a header and
 * onClick on StickyHeader
 */
class StickyHeaderItemDecoration(private val mListener: StickyHeaderInterface, recyclerView: RecyclerView) : ItemDecoration() {
    private var mStickyHeaderHeight = 0
    private var currentHeader: View? = null

    init {
        // to trigger the onClick() of the sticky header
        val detector = GestureDetectorCompat(recyclerView.context, object: GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                val currentHeaderBottom = currentHeader?.bottom ?: 0
                if (e.y < currentHeaderBottom) {
                    currentHeader?.callOnClick()
                }

                // handle single tap
                return super.onSingleTapConfirmed(e)
            }
        })
        recyclerView.addOnItemTouchListener(object: RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                detector.onTouchEvent(e)
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        val topChild = parent.getChildAt(0) ?: return
        val topChildPosition = parent.getChildAdapterPosition(topChild)
        if (topChildPosition == RecyclerView.NO_POSITION) {
            return
        }
        val headerPos = mListener.getHeaderPositionForItem(topChildPosition)
        val currentHeader = getHeaderViewForItem(headerPos, parent)

        if (currentHeader != null) {
            fixLayoutSize(parent, currentHeader)
            val contactPoint = currentHeader.bottom
            val childInContact = getChildInContact(parent, contactPoint)
            if (childInContact != null
                && mListener.isHeader(parent.getChildAdapterPosition(childInContact))
            ) {
                moveHeader(c, currentHeader, childInContact)
                return
            }
            drawHeader(c, currentHeader)
        }

        this.currentHeader = currentHeader
    }

    private fun getHeaderViewForItem(headerPosition: Int, parent: RecyclerView): View? {
        return if (headerPosition >= 0) {
            val layoutResId = mListener.getHeaderLayout(headerPosition)
            val header = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
            mListener.bindHeaderData(header, headerPosition)
            header
        } else {
            null
        }
    }

    private fun drawHeader(c: Canvas, header: View) {
        c.save()
        c.translate(0f, 0f)
        header.draw(c)
        c.restore()
    }

    private fun moveHeader(c: Canvas, currentHeader: View, nextHeader: View) {
        c.save()
        val dy = nextHeader.top - currentHeader.height.toFloat()
        c.translate(0f, dy)
        currentHeader.draw(c)
        c.restore()
    }

    private fun getChildInContact(
        parent: RecyclerView,
        contactPoint: Int
    ): View? {
        var childInContact: View? = null
        for (i in 0 until parent.childCount) {
            var heightTolerance = 0
            val child = parent.getChildAt(i)

            //measure height tolerance with child if child is another header
            val isChildHeader = mListener.isHeader(parent.getChildAdapterPosition(child))
            if (isChildHeader) {
                heightTolerance = mStickyHeaderHeight - child.height
            }

            //add heightTolerance if child top be in display area
            var childBottomPosition: Int
            childBottomPosition = if (child.top > 0) {
                child.bottom + heightTolerance
            } else {
                child.bottom
            }

            if (childBottomPosition > contactPoint) {
                if (child.top <= contactPoint) {
                    // This child overlaps the contactPoint
                    childInContact = child
                    break
                }
            }
        }
        return childInContact
    }

    /**
     * Properly measures and layouts the top sticky header.
     * @param parent ViewGroup: RecyclerView in this case.
     */
    private fun fixLayoutSize(parent: ViewGroup, view: View) {

        // Specs for parent (RecyclerView)
        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightSpec =
            View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)

        // Specs for children (headers)
        val childWidthSpec = ViewGroup.getChildMeasureSpec(
            widthSpec,
            parent.paddingLeft + parent.paddingRight,
            view.layoutParams.width
        )
        val childHeightSpec = ViewGroup.getChildMeasureSpec(
            heightSpec,
            parent.paddingTop + parent.paddingBottom,
            view.layoutParams.height
        )
        view.measure(childWidthSpec, childHeightSpec)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight.also { mStickyHeaderHeight = it })
    }

    interface StickyHeaderInterface {
        /**
         * This method gets called by [StickHeaderItemDecoration] to fetch the position of the header item in the adapter
         * that is used for (represents) item at specified position.
         * @param itemPosition int. Adapter's position of the item for which to do the search of the position of the header item.
         * @return int. Position of the header item in the adapter.
         */
        fun getHeaderPositionForItem(itemPosition: Int): Int

        /**
         * This method gets called by [StickHeaderItemDecoration] to get layout resource id for the header item at specified adapter's position.
         * @param headerPosition int. Position of the header item in the adapter.
         * @return int. Layout resource id.
         */
        fun getHeaderLayout(headerPosition: Int): Int

        /**
         * This method gets called by [StickHeaderItemDecoration] to setup the header View.
         * @param header View. Header to set the data on.
         * @param headerPosition int. Position of the header item in the adapter.
         */
        fun bindHeaderData(header: View?, headerPosition: Int)

        /**
         * This method gets called by [StickHeaderItemDecoration] to verify whether the item represents a header.
         * @param itemPosition int.
         * @return true, if item at the specified adapter's position represents a header.
         */
        fun isHeader(itemPosition: Int): Boolean
    }
}
