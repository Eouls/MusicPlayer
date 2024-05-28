package com.example.musicplayer.Profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.musicplayer.R
import com.example.musicplayer.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

            // 결과 Intent 설정하고 종료
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}