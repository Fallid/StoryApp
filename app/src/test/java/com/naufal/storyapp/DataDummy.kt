package com.naufal.storyapp

import com.naufal.storyapp.data.response.story.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (indexData in 0..50) {
            val stories = ListStoryItem(
                "https://story-api.dicoding.dev/images/stories/photos-$indexData-dummy-pic.png",
                "2023-11-26T09:04:46.$indexData",
                "Nama $indexData",
                "Deskripsi $indexData",
                "-7.9192424$indexData".toDouble(),
                indexData.toString(),
                "112.59735$indexData".toDouble()
            )
            items.add(stories)
        }
        return items
    }
}