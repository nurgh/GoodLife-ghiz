package com.android.goodlife

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.android.goodlife.Fragment.ProfileFragmentCus
import com.android.goodlife.Fragment.ScheduleDoctorFragmentCust
import com.android.goodlife.Fragment.TimelineFeedFragmentCust
import com.android.goodlife.SecondFragment.ChatsFragmentCust
import com.android.goodlife.UserActivity.SignIn
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivityHomeCustomers : AppCompatActivity(),  BottomNavigationView.OnNavigationItemSelectedListener {

    private var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_home_customers)

        val actionBar = getSupportActionBar()
        actionBar?.elevation = 0f

        toolbar = findViewById(R.id.toolbar) as Toolbar
        prepareActionBar(toolbar)


    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var fragment: Fragment? = null
        when (item.itemId) {

            R.id.Timelinefeed -> fragment = TimelineFeedFragmentCust()
            R.id.Chat -> fragment = ChatsFragmentCust()
            R.id.Schedule -> fragment = ScheduleDoctorFragmentCust()
            R.id.Profile -> fragment = ProfileFragmentCus()
        }
        return loadFragment(fragment)
    }

    private fun loadFragment(fragment: Fragment?): Boolean {
        if (fragment != null) {

            supportFragmentManager.beginTransaction().replace(R.id.frame_layout, fragment)
                .commit()
            return true
        }
        return false
    }

    private fun prepareActionBar(toolbar: Toolbar?) {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(false)
        actionBar.setHomeButtonEnabled(false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu_cust, menu)
        return super.onCreateOptionsMenu(menu)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.action_logout_cust -> {
                val logoutIntent = Intent(this, SignIn::class.java)
                logoutIntent.putExtra("mode", "logout")
                startActivity(logoutIntent)
                finish()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }

    }



}






