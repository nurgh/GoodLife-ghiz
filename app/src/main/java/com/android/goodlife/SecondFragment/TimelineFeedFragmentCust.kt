package com.android.goodlife.Fragment



import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.goodlife.Adapter.TimelineFeedAdapter
import com.android.goodlife.Model.Timeline
import com.android.goodlife.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class TimelineFeedFragmentCust : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var txtnotimeline: TextView
    private lateinit var adapterT: TimelineFeedAdapter
    private lateinit var swipe: SwipeRefreshLayout
    lateinit var mTimeline : MutableList<Timeline>


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       val v = inflater.inflate(R.layout.activity_timeline_feed_fragment, container, false)

        return v

        recyclerView = v.findViewById(R.id.search_timeline_recycler_view)
        txtnotimeline = v.findViewById(R.id.txtnotimeline)

        swipe = v.findViewById(R.id.swipe)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        mTimeline = mutableListOf()
        getTimeLine()
    }

    private fun getTimeLine() {
        swipe.isRefreshing = true
        val ref = FirebaseDatabase.getInstance().getReference("Timeline")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    swipe.isRefreshing = false
                    txtnotimeline.visibility = View.GONE
                    mTimeline.clear()
                    for (data: DataSnapshot in p0.children) {
                        val timeline: Timeline = data.getValue(Timeline::class.java) as Timeline
                        mTimeline.add(timeline)
                    }
                    adapterT = context?.let { TimelineFeedAdapter(mTimeline, it) }!!
                    recyclerView.adapter = adapterT
                    adapterT.notifyDataSetChanged()
                } else {
                    swipe.isRefreshing = false
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })
    }
}
