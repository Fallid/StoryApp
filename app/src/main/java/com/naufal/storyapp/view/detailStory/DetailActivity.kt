package com.naufal.storyapp.view.detailStory

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.naufal.storyapp.R
import com.naufal.storyapp.data.response.story.ListStoryItem
import com.naufal.storyapp.databinding.ActivityDetailBinding
import com.squareup.picasso.Picasso

@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var story: ListStoryItem
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        story = intent.getParcelableExtra<ListStoryItem>("detail") as ListStoryItem
        val detailToolbar = findViewById<MaterialToolbar>(R.id.mt_detail)

        setSupportActionBar(detailToolbar)
        supportActionBar?.title = "${story.name}'s Story"
        binding.apply {
            Picasso.get().load(story.photoUrl).into(ivStoryimage)
            tvDetailname.text = story.name
            tvDetailDesc.text = story.description

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }
}