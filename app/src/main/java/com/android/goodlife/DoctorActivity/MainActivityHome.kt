package com.android.goodlife.DoctorActivity

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment

import com.android.goodlife.Data.Tools
import com.android.goodlife.Fragment.*
import com.android.goodlife.R
import com.android.goodlife.SelectFriendActivity
import com.android.goodlife.Service.NotificationService

import com.android.goodlife.Untilty.CustomToast
import com.android.goodlife.UserActivity.SignIn
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivityHome : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener {

    private var toolbar: Toolbar? = null
    internal lateinit var mJobScheduler: JobScheduler
    lateinit var doctor: DoctorFragment



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        loadFragment(TimelineFeedFragmentCust())
        bn_main.setOnNavigationItemSelectedListener(this)
//        untuk menggunkana shadow

        val actionBar = getSupportActionBar()
        actionBar?.elevation = 0f

        toolbar = findViewById(R.id.toolbar) as Toolbar
        prepareActionBar(toolbar)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // for system bar in marshmellow
            Tools.systemBarMarshmellow(this)
            //Create the scheduler
            mJobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val builder = JobInfo.Builder(1, ComponentName(packageName, NotificationService::class.java.getName()))
            builder.setPeriodic(900000)
            //If it needs to continue even after boot, persisted needs to be true
            //builder.setPersisted(true);
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            mJobScheduler.schedule(builder.build())

            if (mJobScheduler != null && mJobScheduler.schedule(builder.build()) != JobScheduler.RESULT_SUCCESS) {
                Log.d("Sign In", "Unable to schedule the service!");
            }
        }
    }


    private fun prepareActionBar(toolbar: Toolbar?) {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(false)
        actionBar.setHomeButtonEnabled(false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.action_logout -> {
                val logoutIntent = Intent(this, SignIn::class.java)
                logoutIntent.putExtra("mode", "logout")
                startActivity(logoutIntent)
                finish()
                return true
            }

            R.id.action_select_friends -> {
                val intent = Intent(this, SelectFriendActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }

    private var exitTime: Long = 0

    fun doExitApp() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            CustomToast(this).showInfo(getString(R.string.press_again_exit_app))
            exitTime = System.currentTimeMillis()
        } else {
            finish()
        }
    }

    override fun onBackPressed() {
        doExitApp()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var fragment : Fragment? = null
        when(item.itemId){

            R.id.Timelinefeed -> fragment = TimelineFeedFragment()
            R.id.Chat -> fragment = ChatsFragment()
            R.id.Schedule -> fragment = ScheduleDoctorFragment()
            R.id.Profile -> fragment = ProfileFragment()
        }
        return loadFragment(fragment)
    }

    private fun loadFragment(fragment: Fragment?): Boolean {
            if (fragment != null){

                supportFragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).commit()
                return true
            }
        return false
    }


}
