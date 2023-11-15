package com.naufal.storyapp.view.add

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.naufal.storyapp.R
import com.naufal.storyapp.data.response.story.AddStoryResponse
import com.naufal.storyapp.data.retrofit.ApiConfig
import com.naufal.storyapp.databinding.ActivityAddBinding
import com.naufal.storyapp.view.add.Camera.Companion.CAMERAX_RESULT
import com.naufal.storyapp.view.main.MainActivity
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class AddActivity : AppCompatActivity() {
    private lateinit var imageView:ImageView
    private lateinit var binding: ActivityAddBinding
    private var currentImageUri: Uri? = null

    companion object{
        private val CAMERA_PERMISSION = arrayOf(Manifest.permission.CAMERA)
    }

    private fun requiredPermission() : Boolean{
        return CAMERA_PERMISSION.all {
            ContextCompat.checkSelfPermission(applicationContext, it) == PackageManager.PERMISSION_GRANTED
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val token = intent.getStringExtra("token")
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imageView = binding.ivAddImage
        if (!requiredPermission()){
            ActivityCompat.requestPermissions(this, CAMERA_PERMISSION, 200)
        }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener{startCameraX()}
        binding.btnUpload.setOnClickListener {
            if (token != null) {
                uploadImage("Bearer $token")
            }
        }
    }

    private fun uploadImage(token:String) {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.edAddDescription.text.toString()
            isLoading(true)
            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )
            lifecycleScope.launch {
                try {
                    val apiService = ApiConfig.getApiService()
                    val successResponse = apiService.newStory(token, requestBody, multipartBody)
                    successResponse.enqueue(object : Callback<AddStoryResponse>{
                        override fun onResponse(
                            call: Call<AddStoryResponse>,
                            response: Response<AddStoryResponse>
                        ) {
                            showToast(response.message())
                            isLoading(false)
                            val intent = Intent(this@AddActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }

                        override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                            showToast(t.message.toString())
                            isLoading(false)
                        }
                    })
                    isLoading(false)
                } catch (e: HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, AddStoryResponse::class.java)
                    errorResponse.message?.let { showToast(it) }
                    isLoading(false)
                }
            }
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }
    private fun startCameraX(){
        val intent = Intent(this, Camera::class.java)
        launcherIntentCameraX.launch(intent)
    }
    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERAX_RESULT) {
            currentImageUri = it.data?.getStringExtra(Camera.EXTRA_IMAGE)?.toUri()
            showImage()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivAddImage.setImageURI(it)
        }
    }
    private fun isLoading (loading:Boolean){
        if (loading){
            binding.pbAddStory.visibility = View.VISIBLE
        }else{
            binding.pbAddStory.visibility = View.INVISIBLE
        }
    }
}