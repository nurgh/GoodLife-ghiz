package com.android.goodlife

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.android.goodlife.Data.SettingApi
import com.android.goodlife.Data.Tools
import com.android.goodlife.DoctorActivity.MainActivityHome
import com.android.goodlife.Untilty.Const
import com.android.goodlife.Untilty.CustomToast
import com.android.goodlife.UserActivity.SignIn
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_layout_one.*

class LayoutOne : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener,
    View.OnClickListener{

    private val RC_SIGN_IN = 100
    private var signInButton: SignInButton? = null
    private var loginProgress: ProgressBar? = null

    private var mGoogleApiClient: GoogleApiClient? = null
    private var mFirebaseAuth: FirebaseAuth? = null
    internal lateinit var ref: DatabaseReference
    internal lateinit var set: com.android.goodlife.Data.SettingApi

    internal lateinit var customToast: CustomToast
    val USERS_CHILD = "users"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layout_one)


        btn_masukemail.setOnClickListener {

            val intent = Intent(this@LayoutOne, SignIn::class.java)
            startActivity(intent)
            finish()

        }

        signInButton = findViewById<SignInButton>(R.id.btn_login_google)as SignInButton
        loginProgress = findViewById<ProgressBar>(R.id.login_progress) as ProgressBar

        // Set click listeners
        signInButton!!.setOnClickListener(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance()
        set = SettingApi(this)

        if (intent.getStringExtra("mode") != null) {
            if (intent.getStringExtra("mode") == "logout") {
                mGoogleApiClient!!.connect()
                mGoogleApiClient!!.registerConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
                    override fun onConnected(bundle: Bundle?) {
                        mFirebaseAuth!!.signOut()
                        var signOut: Any = Auth.GoogleSignInApi.signOut(mGoogleApiClient)
                        set.deleteAllSettings()
                    }

                    override fun onConnectionSuspended(i: Int) {

                    }
                })
            }
        }
        if (!mGoogleApiClient!!.isConnecting()) {
            if (!set.readSetting(Const.PREF_MY_ID).equals("na")) {
                signInButton!!.setVisibility(View.GONE)
                val handler = Handler()
                handler.postDelayed({
                    startActivity(Intent(this@LayoutOne, MainActivityHomeCustomers::class.java))
                    finish()
                }, 3000)
            }
        }
        // for system bar in marshmellow
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Tools.systemBarMarshmellow(this)
        }
    }

    private fun signIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        customToast.showError("Google Play Services error.")
    }

    override fun onClick(p0: View?) {
        when (p0!!.getId()) {
            R.id.btn_login_google -> signIn()
            else -> return
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mFirebaseAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful) {
                    customToast.showError(getString(R.string.error_authetication_failed))
                } else {
                    ref = FirebaseDatabase.getInstance().getReference(USERS_CHILD)
                    ref.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val usrNm = acct.displayName
                            val usrId = acct.id
                            val usrDp = acct.photoUrl!!.toString()

                            set.addUpdateSettings(Const.PREF_MY_ID, usrId!!)
                            set.addUpdateSettings(Const.PREF_MY_NAME, usrNm!!)
                            set.addUpdateSettings(Const.PREF_MY_DP, usrDp)

                            if (!snapshot.hasChild(usrId)) {
                                ref.child("$usrId/${Const.NODE_NAME}").setValue(usrNm)
                                ref.child("$usrId/${Const.NODE_PHOTO}").setValue(usrDp)
                                ref.child("$usrId/${Const.NODE_ID}").setValue(usrId)
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })

                    startActivity(Intent(this@LayoutOne, MainActivityHomeCustomers::class.java))
                    finish()
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                signInButton!!.setVisibility(View.GONE)
                loginProgress!!.setVisibility(View.VISIBLE)
                // Google Sign In was successful, authenticate with Firebase
                val account = result.signInAccount
                firebaseAuthWithGoogle(account!!)
            } else {

                Toast.makeText(this, "error failed", Toast.LENGTH_LONG).show()
//                customToast.showError(getString(R.string.error_login_failed))
            }
        }
    }



}
