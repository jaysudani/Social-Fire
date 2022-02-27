package com.example.social

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.social.daos.PostDao

class CreatePostActivity : AppCompatActivity() {
    private lateinit var postDao: PostDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        val postinput : EditText=findViewById(R.id.postInput)
        val postbutton : Button =findViewById(R.id.postButton)
        postDao = PostDao()
        postbutton.setOnClickListener {
           val inpput= postinput.text.toString().trim()
            if(inpput.isNotEmpty()){

                postDao.addPost(inpput)
                Toast.makeText(this,"Post creation done", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    override fun onBackPressed() {

        val intent= Intent(this,MainActivity::class.java)
        startActivity(intent)

    }
}
