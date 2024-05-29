package com.example.musicplayer.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.musicplayer.databinding.ActivityEditProfileBinding
import com.example.musicplayer.user.User
import com.example.musicplayer.user.UserDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditProfileBinding
    private lateinit var userDB: UserDatabase

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

        // 뒤로가기 버튼
        binding.profileEditCancelBtn.setOnClickListener {
            finish()
        }

        // 적용 버튼 클릭 시 변경된 값들을 ProfileActivity에 전달하고 EditProfileActivity 종료
        binding.profileEditApplyBtn.setOnClickListener {
            // 변경된 값들을 가져옴
            val blogName = binding.blogNameEdit.text.toString()
            val userName = binding.userNameEdit.text.toString()
            val userIntroduction = binding.userIntroductionEdit.text.toString()

            // 변경된 값들을 Intent에 설정
            val resultIntent = Intent().apply {
                putExtra("BLOG_NAME", blogName)
                putExtra("USER_NAME", userName)
                putExtra("USER_INTRODUCTION", userIntroduction)
            }

            // 결과 Intent 설정
            setResult(Activity.RESULT_OK, resultIntent)

            // 데이터베이스 업데이트
            updateUserInDatabase(blogName, userName, userIntroduction)

            finish()
        }
    }
    private fun updateUserInDatabase(blogName: String, userName: String, introduction: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val newUser = User(blogName = blogName, userName = userName, introduction = introduction)
            userDB.userDao().insert(newUser)
        }
    }
}