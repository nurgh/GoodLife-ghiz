package com.android.goodlife.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.goodlife.Adapter.ChatListAdapter
import com.android.goodlife.ChatDetailsActivity
import com.android.goodlife.Data.SettingApi
import com.android.goodlife.MainActivityHome
import com.android.goodlife.Model.ChatMessage
import com.android.goodlife.ParseFirebaseData
import com.android.goodlife.R
import com.android.goodlife.Untilty.Const
import com.android.goodlife.Wighet.DividerItemDecoration
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*


class ChatsFragment : Fragment() {
    lateinit var recyclerView: RecyclerView

    private var mLayoutManager: LinearLayoutManager? = null
    var mAdapter: ChatListAdapter? = null
    private var progressBar: ProgressBar? = null
    lateinit var fab: FloatingActionButton


    internal lateinit var valueEventListener: ValueEventListener
    internal lateinit var ref: DatabaseReference

    internal lateinit var view: View

    internal lateinit var pfbd: ParseFirebaseData
    internal lateinit var set: SettingApi

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        view = inflater.inflate(R.layout.fragment_chat, container, false)
        pfbd = ParseFirebaseData(context!!)
        set = SettingApi(context!!)

        // activate fragment menu
        setHasOptionsMenu(true)

        recyclerView = view.findViewById(R.id.recyclerView) as RecyclerView
        progressBar = view.findViewById(R.id.progressBar) as ProgressBar

        // use a linear layout manager
        mLayoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(DividerItemDecoration(activity!!, DividerItemDecoration.VERTICAL_LIST))

        



        valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(Const.LOG_TAG, "Data changed from fragment")
                if (dataSnapshot.value != null)
                // TODO: 25-05-2017 if number of items is 0 then show something else
                mAdapter = ChatListAdapter(context!!, pfbd.getAllLastMessages(dataSnapshot))
                recyclerView.adapter = mAdapter

                mAdapter?.setOnItemClickListener(object : ChatListAdapter.OnItemClickListener{
                    override fun onItemClick(view: View, obj: ChatMessage, position: Int) {
                        if (obj.receiver.id.equals(set.readSetting(Const.PREF_MY_ID)))
                            ChatDetailsActivity.navigate(
                                activity as MainActivityHome,
                                view.findViewById(R.id.lyt_parent),
                                obj.sender
                            )
                        else if (obj.sender.id.equals(set.readSetting(Const.PREF_MY_ID)))
                            ChatDetailsActivity.navigate(
                                activity as MainActivityHome,
                                view.findViewById(R.id.lyt_parent),
                                obj.receiver
                            )
                    }
                })

                bindView()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }

        ref = FirebaseDatabase.getInstance().getReference(Const.MESSAGE_CHILD)
        ref.addValueEventListener(valueEventListener)

        return view
    }

    fun bindView() {
        try {
            mAdapter!!.notifyDataSetChanged()
            progressBar!!.visibility = View.GONE
        } catch (e: Exception) {
        }

    }



    override fun onDestroy() {
        //Remove the listener, otherwise it will continue listening in the background
        //We have service to run in the background
        ref.removeEventListener(valueEventListener)
        super.onDestroy()
    }


}