package com.cemsarikaya.gezidefterim.view.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.cemsarikaya.gezidefterim.R
import com.cemsarikaya.gezidefterim.databinding.ActivityLoginBinding
import com.cemsarikaya.gezidefterim.view.MapsActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private lateinit var binding: ActivityLoginBinding
private lateinit var auth: FirebaseAuth

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
       // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth



        binding.RegisterTextView.setOnClickListener {

           val intent = Intent(this@LoginActivity,RegisterActivity::class.java)
            startActivity(intent)
            slideAnim()



        }

        binding.forgotPassword.setOnClickListener {

            val intent = Intent(this@LoginActivity,ForgotActivity::class.java)
            startActivity(intent)
            slideAnim()

        }



           val currentUser = auth.currentUser
            if(currentUser != null){
                val intent = Intent(this@LoginActivity, MapsActivity::class.java)
                slideAnim()
                startActivity(intent)
                finish()


            }







    }



    fun loginButton(view:View){

        val email = binding.EmailText.text.toString()
        val password = binding.passwordText.text.toString()

        if(email.isNotEmpty() && password.isNotEmpty()){


            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {

                val intent = Intent(this@LoginActivity, MapsActivity::class.java)
                startActivity(intent)
                slideAnim()
                finish()
            }.addOnFailureListener{

               Toast.makeText(this@LoginActivity, it.localizedMessage, Toast.LENGTH_SHORT).show()

            }

        }else{

            Toast.makeText(this, "Enter Email and Password!", Toast.LENGTH_SHORT).show()

        }


    }

    fun slideAnim(){

        overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left)

    }


}