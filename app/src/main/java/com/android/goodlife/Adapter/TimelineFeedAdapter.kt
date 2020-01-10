package com.android.goodlife.Adapter

import android.content.Context
import android.view.LayoutInflater

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView


import com.android.goodlife.Model.Timeline
import com.android.goodlife.R
import com.bumptech.glide.Glide


class TimelineFeedAdapter(val mTimline: List<Timeline>, val context: Context) : RecyclerView.Adapter<TimelineFeedAdapter.Holder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.row_timeline_doctor, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return mTimline.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.supporting_text.text = mTimline[position].status
        holder.title_text.text = mTimline[position].namaDokter
        Glide.with(context).load(mTimline[position].imagesProfile).into(holder.avatar_image)
        Glide.with(context).load(mTimline[position].imagesStatus).into(holder.media_image)


    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val supporting_text = itemView.findViewById(R.id.supporting_text) as TextView
        val title_text = itemView.findViewById(R.id.title_text) as TextView
        val avatar_image = itemView.findViewById(R.id.avatar_image) as ImageView
        val media_image = itemView.findViewById(R.id.media_image) as ImageView


    }
}
