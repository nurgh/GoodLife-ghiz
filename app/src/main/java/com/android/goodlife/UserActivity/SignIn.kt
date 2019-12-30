package com.android.goodlife.UserActivity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*

import com.android.goodlife.Untilty.Const.Companion.NODE_ID
import com.android.goodlife.Untilty.Const.Companion.NODE_NAME
import com.android.goodlife.Untilty.Const.Companion.NODE_PHOTO
import com.android.goodlife.Data.SettingApi
import com.android.goodlife.Data.Tools
import com.android.goodlife.MainActivityHome

import com.android.goodlife.R
import com.android.goodlife.Untilty.Const
import com.android.goodlife.Untilty.CustomToast

import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignIn : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener,
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
        setContentView(R.layout.activity_sign_in)





            val Masuk = findViewById<Button>(R.id.btn_login)
            Masuk.setOnClickListener {

                Log.d("Sign in", "Aksi Sign In")

                val mail = findViewById<EditText>(R.id.email).text.toString()
                val pass = findViewById<EditText>(R.id.password).text.toString()

                if (mail.isEmpty() || pass.isEmpty() || pass.length < 8) {

                    Toast.makeText(this, "please entry email password", Toast.LENGTH_LONG).show()
                } else if (mail == "doctoradmin@gmail.com" || pass == "doctor") {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(mail, pass)
                        .addOnCompleteListener {
                            if (!it.isSuccessful) {
                                return@addOnCompleteListener

                                val intent = Intent(this, SignIn::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                finish()
                            } else {

                                Toast.makeText(this, "Successfully sign in", Toast.LENGTH_LONG)
                                    .show()
                                val intent = Intent(this, MainActivityHome::class.java)
                                intent.putExtra("email", mail)
                                intent.putExtra("password", pass)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }

                        }
                        .addOnFailureListener {
                            Log.d("SignIn", "Failed login : ${it.message}")
                            Toast.makeText(this, "Email and Password incorrect", Toast.LENGTH_LONG)
                                .show()
                        }

                } else {

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(mail, pass)
                        .addOnCompleteListener {
                            if (!it.isSuccessful) {

                                Log.d("sign in", "Your fail Sign in")
                                val intent = Intent(this, SignIn::class.java)
                                startActivity(intent)
                                finish()
                            } else {

                                Log.d("Sign In", "Your succcessfully Sign In")
                                Toast.makeText(this, "Successfully Sign Up", Toast.LENGTH_LONG)
                                    .show()
                                val intent = Intent(this, TakePhoto::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                finish()
                            }
                        }
                        .addOnFailureListener {

                            Log.d("Sign In", "Failed Sign in User ")
                            Toast.makeText(this, "Inncorrect email/ password", Toast.LENGTH_LONG)
                                .show()

                        }
                }


            }
        val daftar = findViewById<TextView>(R.id.btn_daftar)

        daftar.setOnClickListener {

            val intent = Intent(this, SignUp::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            Log.d("Sign In", "Aksi Sign up")

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
                    startActivity(Intent(this@SignIn, MainActivityHome::class.java))
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
                                ref.child("$usrId/$NODE_NAME").setValue(usrNm)
                                ref.child("$usrId/$NODE_PHOTO").setValue(usrDp)
                                ref.child("$usrId/$NODE_ID").setValue(usrId)
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })

                    startActivity(Intent(this@SignIn, MainActivityHome::class.java))
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

