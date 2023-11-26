package com.naufal.storyapp.view.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.naufal.storyapp.data.response.story.ListStoryItem
import com.naufal.storyapp.data.retrofit.ApiService

class StoryPaging(private val apiService: ApiService):PagingSource<Int, ListStoryItem>() {
    private companion object {
        const val INITIAL_PAGE = 1
    }
    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE
            val responseData = apiService.getStories(position, params.loadSize)

            LoadResult.Page(
                data = responseData.listStory,
                prevKey = if (position == INITIAL_PAGE) null else position - 1,
                nextKey = if (responseData.listStory.isEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}