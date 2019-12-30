package com.android.goodlife.Adapter

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView


import com.android.goodlife.Model.Doctor
import com.android.goodlife.R
import com.bumptech.glide.Glide



class DoctorListAdapter(val doctorlist: List<Doctor>, val context: Context,  private val mListener: OnItemClickListener?) : RecyclerView.Adapter<DoctorListAdapter.Holder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.row_doctor_list, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return doctorlist.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.nama.text = doctorlist[position].name
        holder.mail.text = doctorlist[position].email
        Glide.with(context).load(doctorlist[position].photos).into(holder.image)





    }

     inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         val nama = itemView.findViewById(R.id.name) as TextView
         val mail = itemView.findViewById(R.id.email_doctor) as TextView
         val image = itemView.findViewById(R.id.images) as ImageView
         val lay = itemView.findViewById(R.id.lyt_parent1)as LinearLayout



     }

    interface OnItemClickListener {
        fun onItemClick(view: View, viewModel: Doctor)
    }

    fun onClick(v: View?) {
        if (onItemClickListener != null) {
            Handler().postDelayed({
                if (v != null) {
                    onItemClickListener!!.onItemClick(v, v.tag as Doctor)
                }
            }, 200)
        }
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }




}