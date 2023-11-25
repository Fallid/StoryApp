package com.naufal.storyapp.view.main

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.naufal.storyapp.R
import com.naufal.storyapp.data.repository.StoryRepository
import com.naufal.storyapp.data.response.story.AllStoryResponse
import com.naufal.storyapp.data.response.story.ListStoryItem
import com.naufal.storyapp.data.retrofit.ApiConfig
import com.naufal.storyapp.databinding.ActivityMainBinding
import com.naufal.storyapp.view.add.AddActivity
import com.naufal.storyapp.view.maps.MapsActivity
import com.naufal.storyapp.view.modelFactory.ViewModelFactory
import com.naufal.storyapp.view.welcome.WelcomeActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(){
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainAdapter: MainAdapter
    private lateinit var allStoryResponse: ArrayList<ListStoryItem>
    private val apiConfig = ApiConfig.getApiService()
    private val storyRepository =  StoryRepository(apiConfig)
    private var tokens = ""
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    companion object{
        const val LOCATION_PERMISSION = "location_stories"
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
                tokens = story.token
            print(tokens)
                viewModel.viewModelScope.launch {
                    try {
                        isLoading(true)
                        storyRepository.getStories(token= tokens, onSuccess = {list -> showStories(list)}, onError = {})
                        binding.fbAdd.setOnClickListener {
                            val intent = Intent(this@MainActivity, AddActivity::class.java)
                            intent.putExtra("token", tokens)
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
    private fun showStories(storyList: List<ListStoryItem>) {
        binding.apply {
            mainAdapter = MainAdapter()
            binding.rvStoryItem.adapter = mainAdapter
            if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
                rvStoryItem.layoutManager = GridLayoutManager(this@MainActivity, 3)
            }else{
                rvStoryItem.layoutManager = LinearLayoutManager(this@MainActivity)
            }
            isLoading(false)
            allStoryResponse = ArrayList(storyList)
            allStoryResponse.addAll(storyList)
            mainAdapter.submitList(storyList)

        }
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
                R.id.languageButton -> {
                    val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                    startActivity(intent)
                    true
                }
                R.id.mapsButton -> {
                    val intent = Intent(this, MapsActivity::class.java)
                    intent.putExtra(LOCATION_PERMISSION, allStoryResponse)
                    startActivity(intent)
                    true
                }

            else -> false
        }
        }
    }
}