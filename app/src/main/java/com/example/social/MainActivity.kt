package com.example.social




import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.social.daos.PostDao
import com.example.social.model.Post
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.Query


class MainActivity : AppCompatActivity(), IPostAdapter {
    private lateinit var adapter : PostAdapter
    private lateinit var postdao : PostDao
    private lateinit var recyclerview : RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fab : FloatingActionButton = findViewById(R.id.fab)

        postdao= PostDao()
        recyclerview=findViewById(R.id.recyclerView)
        fab.setOnClickListener {
            val intent= Intent(this,CreatePostActivity::class.java)
            startActivity(intent)
        }

        setUpRecyclerView()

    }

    private fun setUpRecyclerView() {

        val postCollection = postdao.postCollection

        val query = postCollection.orderBy("createdAt", Query.Direction.DESCENDING)
        val recyclerViewOption = FirestoreRecyclerOptions.Builder<Post>().setQuery(query,Post::class.java).build()
        adapter = PostAdapter(recyclerViewOption,this)
        recyclerview.adapter=adapter

        recyclerview.layoutManager=LinearLayoutManager(this)

    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onLikeClicked(postId: String) {
        postdao.postLike(postId)
    }
}