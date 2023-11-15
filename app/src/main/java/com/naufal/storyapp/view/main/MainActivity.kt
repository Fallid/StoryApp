package com.naufal.storyapp.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.naufal.storyapp.R
import com.naufal.storyapp.data.repository.StoryRepository
import com.naufal.storyapp.data.response.story.ListStoryItem
import com.naufal.storyapp.data.retrofit.ApiConfig
import com.naufal.storyapp.databinding.ActivityMainBinding
import com.naufal.storyapp.view.add.AddActivity
import com.naufal.storyapp.view.modelFactory.ViewModelFactory
import com.naufal.storyapp.view.welcome.WelcomeActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(){
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainAdapter: MainAdapter
    private val apiConfig = ApiConfig.getApiService()
    private val storyRepository =  StoryRepository(apiConfig)
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }
        mainAdapter = MainAdapter()
        binding.rvStoryItem.adapter = mainAdapter
        binding.rvStoryItem.layoutManager = LinearLayoutManager(this)
        setupView()
        logoutAction()
        viewModel.getSession().observe(this){
            story -> if (story.isLogin){
                val token = story.token
                viewModel.viewModelScope.launch {
                    try {
                        isLoading(true)
                        storyRepository.getStories(token= token, onSuccess = {list -> updateStoryList(list)}, onError = {})
                        binding.fbAdd.setOnClickListener {
                            val intent = Intent(this@MainActivity, AddActivity::class.java)
                            intent.putExtra("token", token)
                            startActivity(intent)
                        }
                    }catch (err : Exception){
                        Log.e("MainActivity List Story", err.message.toString())
                    }
                }
            }
        }
    }

    private fun isLoading (loading:Boolean){
        if (loading){
            binding.pbMain.visibility = View.VISIBLE
        }else{
            binding.pbMain.visibility = View.INVISIBLE
        }
    }
    private fun updateStoryList(storyList: List<ListStoryItem>) {
        isLoading(false)
        mainAdapter.submitList(storyList)
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun logoutAction() {
        binding.mtLogout.setOnMenuItemClickListener {
            menuItem -> when(menuItem.itemId){
                R.id.logoutButton -> {
                    viewModel.logout()
                    true
                }

            else -> false
        }
        }
    }
}