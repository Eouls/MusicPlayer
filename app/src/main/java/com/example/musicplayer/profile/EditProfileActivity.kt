package com.example.musicplayer.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayer.databinding.ActivityEditProfileBinding
import com.example.musicplayer.user.User
import com.example.musicplayer.user.UserDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class EditProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditProfileBinding
    private lateinit var userDatabase: UserDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // UserDatabase 초기화
        userDatabase = UserDatabase.getInstance(this)!!

        GlobalScope.launch(Dispatchers.IO) {
            val allUsers = userDatabase.userDao().getUsers()
            allUsers.forEach { user ->
                Log.d("UserDataEdit", "ID: ${user.id}, Blog Name: ${user.blogName}, User Name: ${user.userName}, Introduction: ${user.introduction}")
            }
        }

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

            // 업데이트된 사용자 이름 및 소개 정보를 전역 변수에 할당
            var updatedBlogName = blogName
            var updatedUserName = userName
            var updatedUserIntroduction = userIntroduction

            // 데이터베이스 업데이트
            updateUserInDatabase(updatedBlogName, updatedUserName, updatedUserIntroduction)

            finish()
        }
    }
    private fun updateUserInDatabase(blogName: String, userName: String, introduction: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val user = userDatabase.userDao().getUsers().firstOrNull()
            if (user != null) {
                // 사용자 정보 업데이트
                val updatedUser = user.copy(blogName = blogName, userName = userName, introduction = introduction)
                userDatabase.userDao().update(updatedUser)
            } else {
                // 사용자 정보가 없으면 새로 삽입
                val newUser = User(blogName = blogName, userName = userName, introduction = introduction)
                userDatabase.userDao().insert(newUser)
            }
        }
    }
}