package com.naufal.storyapp.view.add

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
    private lateinit var imageView: ImageView
    private lateinit var binding: ActivityAddBinding
    private lateinit var location: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentImageUri: Uri? = null
    private val requestLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }

                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }

                else -> {
                }
            }
        }

    companion object {
        private val CAMERA_PERMISSION = arrayOf(Manifest.permission.CAMERA)
    }

    private fun requiredPermission(): Boolean {
        return CAMERA_PERMISSION.all {
            ContextCompat.checkSelfPermission(
                applicationContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val token = intent.getStringExtra("token")
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imageView = binding.ivAddImage
        layoutView()
        setAnimation()
        if (!requiredPermission()) {
            ActivityCompat.requestPermissions(this, CAMERA_PERMISSION, 200)
        }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener { startCamera() }
        binding.btnUpload.setOnClickListener {
            if (token != null) {
                uploadAction("Bearer $token")
            }
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getMyLastLocation()
    }

    private fun uploadAction(token: String) {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.edAddDescription.text.toString()
            isLoading(true)
            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val latitudeUploader = location.latitude.toFloat()
            val longtitudeUploader = location.longitude.toFloat()
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )
            lifecycleScope.launch {
                try {
                    val apiService = ApiConfig.getApiService()
                    val successResponse = apiService.newStory(
                        token,
                        requestBody,
                        multipartBody,
                        latitudeUploader,
                        longtitudeUploader
                    )
                    successResponse.enqueue(object : Callback<AddStoryResponse> {
                        override fun onResponse(
                            call: Call<AddStoryResponse>,
                            response: Response<AddStoryResponse>
                        ) {
                            showToast(response.message())
                            isLoading(false)
                            val intent = Intent(this@AddActivity, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
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

    private fun startCamera() {
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

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { locs: Location? ->
                if (locs != null) {
                    location = locs
                } else {
                    Toast.makeText(
                        this@AddActivity,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestLocationPermission.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun isLoading(loading: Boolean) {
        if (loading) {
            binding.pbAddStory.visibility = View.VISIBLE
        } else {
            binding.pbAddStory.visibility = View.INVISIBLE
        }
    }

    private fun layoutView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.show()
    }

    private fun setAnimation() {
        val buttonCamera =
            ObjectAnimator.ofFloat(binding.btnCamera, View.ALPHA, 1f).setDuration(1000)
        val buttonGallery =
            ObjectAnimator.ofFloat(binding.btnGallery, View.ALPHA, 1f).setDuration(1000)
        val editTextDesc =
            ObjectAnimator.ofFloat(binding.edAddDescription, View.ALPHA, 1f).setDuration(1000)
        val buttonUpload =
            ObjectAnimator.ofFloat(binding.btnUpload, View.ALPHA, 1f).setDuration(1000)


        AnimatorSet().apply {
            playSequentially(
                buttonCamera,
                buttonGallery,
                editTextDesc,
                buttonUpload
            )
            startDelay = 1000
        }.start()
    }
}