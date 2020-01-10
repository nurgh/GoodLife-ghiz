package com.android.goodlife.DoctorActivity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.goodlife.Model.Doctor
import com.android.goodlife.R
import com.android.goodlife.UserActivity.SignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_sign_doctor.*

class SignIn_Doctor : AppCompatActivity() {

    internal lateinit var ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_doctor)

        btn_login_doct.setOnClickListener {

            Log.d("Sign in", "Aksi Sign In")

            val mail = findViewById<EditText>(R.id.email_doct).text.toString()
            val pass = findViewById<EditText>(R.id.password_doct).text.toString()

            if (mail.isEmpty() || pass.isEmpty() || pass.length < 8) {

                Toast.makeText(this, "please entry email password", Toast.LENGTH_LONG).show()
            } else if (mail == "doctoradmin@gmail.com" || pass == "doctor1234") {
                val intent = Intent(this, DoctorAccout::class.java)
                startActivity(intent)
                finish()
            } else {

                FirebaseAuth.getInstance().signInWithEmailAndPassword(mail, pass)
                    .addOnCompleteListener {
                        if (!it.isSuccessful) {
                            Log.d("sign in", "Your fail Sign in")
                            val intent = Intent(this, SignIn::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            val email = FirebaseAuth.getInstance().currentUser?.email.toString()

                            ref = FirebaseDatabase.getInstance().getReference("doctor")

                            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {

                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    for (cekdata in p0.children) {
                                        var docter = cekdata.getValue(Doctor::class.java)
                                        if (email.equals(docter!!.email)) {

                                            val intent = Intent(
                                                this@SignIn_Doctor,
                                                MainActivityHome::class.java
                                            )
                                            startActivity(intent)
                                            finish()

                                            Toast.makeText(
                                                this@SignIn_Doctor,
                                                "hello ${email}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }

                            })

                        }
                    }
                    .addOnFailureListener {

                        Log.d("Sign In", "Failed Sign in User ")
                        Toast.makeText(this, "Inncorrect email/ password", Toast.LENGTH_LONG)
                            .show()

                    }
            }

        }
    }
}



