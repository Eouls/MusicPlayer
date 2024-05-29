package com.example.musicplayer.post

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayer.User.UserDatabase
import com.example.musicplayer.databinding.ActivityPostBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostBinding
    private lateinit var postDatabase: PostDatabase
    private var imageUri: Uri? = null
    private var imagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // PostDatabase 초기화
        postDatabase = PostDatabase.getInstance(this)!!

        initClickListener()
        initRequestImage()
    }

    private fun initClickListener() {
        // 뒤로가기 버튼
        binding.postEditCancelBtn.setOnClickListener {
            finish()
        }

        // 등록 버튼
        binding.postEditApplyBtn.setOnClickListener {
            val title = binding.postTitleTv.text.toString()
            val content = binding.postContentTv.text.toString()
            val currentDate = getCurrentDate()
            val userName = getUserName()
            val imagePath = imageUri?.let { saveImageToInternalStorage(it) }

            // 입력된 데이터를 데이터베이스에 저장
            val post = Post(
                title = title,
                name = userName,
                content = content,
                date = currentDate,
                imagePath = imagePath
            )
            insertPost(post)

            // Activity 종료
            finish()
        }
    }

    // 현재 날짜 및 시간을 가져오는 함수
    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        return sdf.format(Date())
    }

    // 데이터베이스에 게시물을 추가하는 함수
    private fun insertPost(post: Post) {
        GlobalScope.launch(Dispatchers.IO) {
            postDatabase.postDao().insert(post)
        }
    }

    // 갤러리 런처
    private fun initRequestImage() {
        val requestGalleryLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            try {
                val option = BitmapFactory.Options()
                val uri = it.data!!.data!!
                imageUri = uri // 이미지 URI 저장

                //이미지 로딩
                val inputStream = contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream, null, option)
                inputStream!!.close()

                if (bitmap != null) {
                    binding.postImgIv.setImageBitmap(bitmap)
                    imagePath = it.data!!.data!!.toString()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding.postAddPhotoIv.setOnClickListener {
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

    // 작성자 이름을 가져오는 함수
    private fun getUserName(): String {
        val sharedPreferences = getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
        return sharedPreferences.getString("USER_NAME", "Unknown User") ?: "Unknown User"
    }
}
