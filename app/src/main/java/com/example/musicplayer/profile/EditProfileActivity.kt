package com.example.musicplayer.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.musicplayer.databinding.ActivityEditProfileBinding
import com.example.musicplayer.user.User
import com.example.musicplayer.user.UserDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class EditProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditProfileBinding
    private lateinit var userDB: UserDatabase
    private var profileImageUri: Uri? = null
    private var coverImageUri: Uri? = null
    private var profileImgPath: String? = null
    private var coverImgPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // userDB 초기화
        userDB = UserDatabase.getInstance(this)!!

        // Intent로부터 값 수신
        val blogName = intent.getStringExtra("BLOG_NAME") ?: ""
        val userName = intent.getStringExtra("USER_NAME") ?: ""
        val userIntroduction = intent.getStringExtra("USER_INTRODUCTION") ?: ""

        // EditText에 값 설정
        binding.blogNameEdit.setText(blogName)
        binding.userNameEdit.setText(userName)
        binding.userIntroductionEdit.setText(userIntroduction)

        initClickListener()
        initProfileRequestImage()
        initCoverRequestImage()
    }

    private fun initClickListener(){
        // 뒤로가기 버튼
        binding.profileEditCancelBtn.setOnClickListener {
            finish()
        }

        // 적용 버튼
        binding.profileEditApplyBtn.setOnClickListener {
            val coverImgPath = coverImageUri?.let { saveImageToInternalStorage(it) }
            val profileImgPath = profileImageUri?.let { saveImageToInternalStorage(it) }
            val blogName = binding.blogNameEdit.text.toString()
            val userName = binding.userNameEdit.text.toString()
            val userIntroduction = binding.userIntroductionEdit.text.toString()

            // 변경된 값들을 Intent에 설정
            val resultIntent = Intent().apply {
                putExtra("COVER_IMG_PATH", coverImgPath)
                putExtra("PROFILE_IMG_PATH", profileImgPath)
                putExtra("BLOG_NAME", blogName)
                putExtra("USER_NAME", userName)
                putExtra("USER_INTRODUCTION", userIntroduction)
            }

            // 결과 Intent 설정
            setResult(Activity.RESULT_OK, resultIntent)

            val newUser = User(
                coverImgPath = coverImgPath,
                profileImgPath = profileImgPath,
                blogName = blogName,
                userName = userName,
                introduction = userIntroduction
            )
            userDB.userDao().insert(newUser)

            finish()
        }
    }

    // 프로필 사진 갤러리 런처
    private fun initProfileRequestImage() {
        val requestGalleryLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            try {
                val option = BitmapFactory.Options()
                val uri = it.data!!.data!!
                profileImageUri = uri // 이미지 URI 저장

                //이미지 로딩
                val inputStream = contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream, null, option)
                inputStream!!.close()

                if (bitmap != null) {
                    binding.profilePictureEditIv.setImageBitmap(bitmap)
                    profileImgPath = it.data!!.data!!.toString()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding.profilePictureEditBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            requestGalleryLauncher.launch(intent)
        }
    }


    // 커버 이미지 갤러리 런처
    private fun initCoverRequestImage() {
        val requestGalleryLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            try {
                val option = BitmapFactory.Options()
                val uri = it.data!!.data!!
                coverImageUri = uri // 이미지 URI 저장

                //이미지 로딩
                val inputStream = contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream, null, option)
                inputStream!!.close()

                if (bitmap != null) {
                    binding.profileCoverEditIv.setImageBitmap(bitmap)
                    coverImgPath = it.data!!.data!!.toString()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding.profileCoverEditBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            requestGalleryLauncher.launch(intent)
        }
    }

    // 이미지 파일을 내부 저장소에 저장하고 경로를 반환하는 함수
    private fun saveImageToInternalStorage(uri: Uri): String {
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
        val file = File(filesDir, "${System.currentTimeMillis()}.jpg")
        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.close()
        return file.absolutePath
    }


    /*
    private fun updateUserInDatabase(coverImgPath: String, profileImgPath: String, blogName: String, userName: String, introduction: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val newUser = User(coverImgPath = coverImgPath, profileImgPath = profileImgPath, blogName = blogName, userName = userName, introduction = introduction)
            userDB.userDao().insert(newUser)
        }
    }
    */
}