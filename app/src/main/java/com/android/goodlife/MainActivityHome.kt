package com.android.goodlife

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
import androidx.fragment.app.FragmentTransaction

import com.android.goodlife.Data.Tools
import com.android.goodlife.Fragment.ChatsFragment
import com.android.goodlife.Fragment.Doctor
import com.android.goodlife.Fragment.ScheduleDoctor
import com.android.goodlife.Fragment.TimelineFeed
import com.android.goodlife.Service.NotificationService

import com.android.goodlife.Untilty.CustomToast
import com.android.goodlife.UserActivity.SignIn
import kotlinx.android.synthetic.main.activity_main.*


class MainActivityHome : AppCompatActivity() {

    private var toolbar: Toolbar? = null
    internal lateinit var mJobScheduler: JobScheduler
    lateinit var chatsFragment: ChatsFragment
    lateinit var timelineFeed: TimelineFeed
    lateinit var scheduleDoctor: ScheduleDoctor
    lateinit var doctor: Doctor



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bn_main.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.Chat -> {
                    chatsFragment = ChatsFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, chatsFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }


                R.id.Timelinefeed -> {
                    timelineFeed = TimelineFeed()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, timelineFeed)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }

                R.id.Schedule -> {
                    scheduleDoctor = ScheduleDoctor()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, scheduleDoctor)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }

                R.id.Profile -> {
                    doctor = Doctor()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, doctor)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }

            }
            true
        }

//        untuk menggunkana shadow

        val actionBar = getSupportActionBar()
        actionBar?.elevation = 0f



        toolbar = findViewById(R.id.toolbar) as Toolbar
        prepareActionBar(toolbar)
        initComponent()

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

    private fun initComponent() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val ctf = ChatsFragment()
        //icf.setRetainInstance(true);
        fragmentTransaction.add(R.id.main_container, ctf, "Chat History")
        fragmentTransaction.commit()

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
                finish()
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


}
