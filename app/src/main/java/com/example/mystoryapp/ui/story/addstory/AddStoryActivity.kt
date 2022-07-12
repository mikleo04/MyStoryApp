package com.example.mystoryapp.ui.story.addstory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryapp.data.User
import com.example.mystoryapp.databinding.ActivityAddStoryBinding
import com.example.mystoryapp.preferences.UserPreference
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var addStoryModel: AddStoryViewModel
    private var photoFile: File? = null
    private lateinit var userPreference: UserPreference
    private lateinit var user: User
    private var isExecutingUpload = false

    companion object{
        const val CAMERA_X_RESULT_CODE = 1
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        addStoryModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
            AddStoryViewModel::class.java)

        userPreference = UserPreference(this)
        user = userPreference.getUser()

        addStoryModel.setUserData(user)

        binding.addStoryBtnGallery.setOnClickListener{
            startGallery()
        }

        binding.addStoryBtnUpload.setOnClickListener{
            binding.addStoryTvErrMsg.text = ""
            if (photoFile == null){
                Toast.makeText(this, "Photo is required", Toast.LENGTH_SHORT).show()
            }else{
                if (!isExecutingUpload){
                    val notes = binding.addStoryEtNotes.text.toString()
                    addStoryModel.addNewStory(notes, 0.0, 0.0, photoFile!!)
                }
            }

        }
        addStoryModel.isLoading.observe(this){
            if (it) {
                binding.addStoryUploadProgressBar.visibility = View.VISIBLE
                isExecutingUpload = true
            } else {
                binding.addStoryUploadProgressBar.visibility = View.GONE
                isExecutingUpload = false
            }
        }
        addStoryModel.addNewStoryResponse.observe(this){
            if(it.error == false && it.message == "Story created successfully"){
                finish()
            }else{
                binding.addStoryTvErrMsg.text = it.message.toString()
            }
        }
        prepareCameraX()
    }
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT_CODE) {
            photoFile = it.data?.getSerializableExtra("picture") as File
        }

        if (it.resultCode == CAMERA_X_RESULT_CODE) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            val result = rotateBitmap(
                BitmapFactory.decodeFile(myFile.path),
                isBackCamera
            )

            binding.addStoryIvPreview.setImageBitmap(result)
        }

    }
    private fun prepareCameraX() {
        binding.addStoryBtnCamera.setOnClickListener {
            if (!allPermissionsGranted()) {
                ActivityCompat.requestPermissions(
                    this,
                    REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS
                )
            }else{
                val intent = Intent(this, CameraxActivity::class.java)
                launcherIntentCameraX.launch(intent)
            }

        }
    }


    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult( ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            photoFile = createTemporaryFile(selectedImg)

            binding.addStoryIvPreview.setImageURI(selectedImg)
        }
    }
    private fun rotateBitmap(bitmap: Bitmap, isBackCamera: Boolean = false): Bitmap {
        val matrix = Matrix()
        return if (isBackCamera) {
            matrix.postRotate(90f)
            Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )
        } else {
            matrix.postRotate(-90f)
            matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
            Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )
        }
    }

    private fun createTemporaryFile(uri: Uri): File {
        val directory: File? = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val tempFile = File.createTempFile("tempFile", ".jpg", directory)

        val buffer = ByteArray(1024)
        var len: Int

        val inputStream = contentResolver.openInputStream(uri) as InputStream
        val outputStream: OutputStream = FileOutputStream(tempFile)

        while (inputStream.read(buffer).also { len = it } > 0 ){
            outputStream.write(buffer, 0, len)
        }
        outputStream.close()
        inputStream.close()
        return tempFile
    }
}