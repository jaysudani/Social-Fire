package com.example.social

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.social.daos.UserDao
import com.example.social.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SignInActivity : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var signInButton: com.google.android.gms.common.SignInButton
    private lateinit var progressBar: ProgressBar
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)


        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1025922383011-jpn82pef7vv885a5oi88r0hd4d3vm5t4.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth=Firebase.auth

        //finding id
        signInButton = findViewById(R.id.signInButton)
        progressBar = findViewById(R.id.progressBar)



        //try
        val getResult =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                    handleSignInResult(task)
                }
            }


        //onclick
        signInButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            //startActivityForResult(signInIntent, RC_SIGN_IN)
            getResult.launch(signInIntent)
            Log.d("yes ","1")

        }

//        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//            super.onActivityResult(requestCode, resultCode, data)
//            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
//            if (requestCode == RC_SIGN_IN) {
//                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//                handleSignInResult(task)
//            }
//        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser =auth.currentUser
        updateUI(currentUser)
    }
    private fun handleSignInResult(task: Task<GoogleSignInAccount>?) {
        try {
            // Google Sign In was successful, authenticate with Firebase
            val account = task?.getResult(ApiException::class.java)!!
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            // Google Sign In failed, update UI appropriately
            Log.w(TAG, "Google sign in failed", e)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken,null)
        signInButton.visibility= View.GONE
        progressBar.visibility=View.VISIBLE
        GlobalScope.launch(Dispatchers.IO) {
            val auth = auth.signInWithCredential(credential).await()
            val firebaseUser =auth.user
            withContext(Dispatchers.Main){
                Log.d("hello","update")
                updateUI(firebaseUser)
            }
        }
    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
        if(firebaseUser!=null){

            val user = User(firebaseUser.uid,firebaseUser.displayName,firebaseUser.photoUrl.toString())
            val userDao= UserDao()
            userDao.addUser(user)
            val mainActivityIntent =Intent(this,MainActivity::class.java)
            startActivity(mainActivityIntent)
            finish()
        }else{
            signInButton.visibility= View.VISIBLE
            progressBar.visibility=View.GONE
        }
    }


}