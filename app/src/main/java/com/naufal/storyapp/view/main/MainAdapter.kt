package com.naufal.storyapp.view.main

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.naufal.storyapp.data.response.story.ListStoryItem
import com.naufal.storyapp.databinding.StorylayoutBinding
import com.naufal.storyapp.view.detailStory.DetailActivity
import com.squareup.picasso.Picasso

class MainAdapter: PagingDataAdapter<ListStoryItem, MainAdapter.MainViewHolder>(ResponseCallback) {
    companion object {
        private val ResponseCallback = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
    inner class MainViewHolder (private val binding: StorylayoutBinding): RecyclerView.ViewHolder(binding.root) {
            fun bind(story: ListStoryItem) {
                binding.tvName.text = story.name
                binding.tvDescription.text = story.description
                Picasso.get().load(story.photoUrl).into(binding.ivStory)
                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, DetailActivity::class.java)
                    intent.putExtra("detail", story)
                    val optionsCompat: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                     itemView.context as Activity,
                        Pair(binding.ivStory, "profile"),
                        Pair(binding.tvName, "name"),
                        Pair(binding.tvDescription, "description")
                    )
                    itemView.context.startActivity(intent, optionsCompat.toBundle())
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val binding = StorylayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val currentStory = getItem(position)
        if (currentStory != null) {
            holder.bind(currentStory)
        }
    }


}