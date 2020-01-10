package com.android.goodlife.UserActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.android.goodlife.Model.Users

import com.android.goodlife.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUp : AppCompatActivity() {

    var TAG = "Sign Up"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        textsignIn.setOnClickListener {

            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
            finish()
        }


        val signUp = findViewById<FloatingActionButton>(R.id.btn_signup)


        signUp.setOnClickListener {

            val username = findViewById<EditText>(R.id.add_usernamesignup).text.toString()
            val  email = findViewById<EditText>(R.id.add_emailsignup).text.toString()
            val password = findViewById<EditText>(R.id.add_passwordsignup).text.toString()

           if (email.isEmpty() || username.isEmpty()){

                Toast.makeText(this, "Please Insert your email", Toast.LENGTH_LONG).show()
               return@setOnClickListener
           }else if(password.length < 8){

               Toast.makeText(this, "Please Insert your password min 8 character", Toast.LENGTH_LONG).show()
                return@setOnClickListener
           }else{

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {

                        if (!it.isSuccessful){

                            Log.d(TAG, "Your Registrasion fail")
                            return@addOnCompleteListener
                            Toast.makeText(this, "Please Check back Your Bio", Toast.LENGTH_LONG).show()

                        }else{

                            Log.d(TAG, "Its Successfully")
                            saveUsertoDatabaseFirebase(username,email,password)

                            Toast.makeText(this, "Anda Berhasil membuat Akun", Toast.LENGTH_LONG).show()

                            val intent = Intent(this, SignIn::class.java)
                            startActivity(intent)
                            finish()

                        }
                    }

           }


        }

    }

    private fun saveUsertoDatabaseFirebase(username: String, email: String, password: String) {


         val uid = FirebaseAuth.getInstance().uid.toString()

        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val users = Users(uid,username, email, "", password)

        ref.setValue(users)
            .addOnCompleteListener {

                Log.d(TAG, "Succescfully add Data on database Firebase")
                Toast.makeText(this, "Saved Successfully", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {

                Log.d(TAG, "Failed add data in Database firebase")
                Toast.makeText(this, "Saved its Fail", Toast.LENGTH_LONG).show()
            }

    }
}
