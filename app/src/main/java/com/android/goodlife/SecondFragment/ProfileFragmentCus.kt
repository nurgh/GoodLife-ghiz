package com.android.goodlife.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.goodlife.R

class ProfileFragmentCus : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
      val   v = inflater.inflate(R.layout.activity_profile_users, container, false)
        return v
    }
}