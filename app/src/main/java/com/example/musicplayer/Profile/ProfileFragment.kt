package com.example.musicplayer.Profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.R
import com.example.musicplayer.databinding.FragmentProfileBinding

class ProfileFragment: Fragment() {
    lateinit var binding: FragmentProfileBinding
    private var myPostDatas = ArrayList<Post>()
    private val EDIT_PROFILE_REQUEST_CODE = 123

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)


        // 스크롤 시 툴바 세팅
        val activity = activity as? AppCompatActivity
//        activity?.setSupportActionBar(binding.toolbar)
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity?.supportActionBar?.setDisplayShowHomeEnabled(true)


        // 더미데이터
        myPostDatas.apply {
            add(Post(R.drawable.img_post_1, "제목1", "", "내용1", "2024.05.28"))
            add(Post(R.drawable.img_post_2, "제목2", "","내용2\n김기찬 군은 돼지일까요 아닐까요 ?", "2024.05.27"))
            add(Post(R.drawable.img_post_3, "제목3", "","내용3\n정답은요...", "2024.05.26"))
            add(Post(R.drawable.img_post_4, "제목4", "","내용4\n돼지였습니다ㅡ!!", "2024.05.25"))
            add(Post(R.drawable.img_post_5, "제목5", "","내용5\n^0^","2024.05.24"))
        }

        val profileRVAdapter = ProfileRVAdapter(myPostDatas)
        binding.profilePostRv.adapter = profileRVAdapter
        binding.profilePostRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        profileRVAdapter.setMyItemClickListener(object : ProfileRVAdapter.MyItemClickListener {
            override fun onRemoveItem(position: Int) {
                profileRVAdapter.removeItem(position)
            }
        })


        // 프로필 수정 버튼 클릭 리스너
        binding.settingBtn.setOnClickListener {
            // Fragment에서 TextView 값 추출
            val blogName = binding.blogName.text.toString()
            val userName = binding.userName.text.toString()
            val userIntroduction = binding.userIntroduction.text.toString()

            // Intent 생성 및 값 전달
            val intent = Intent(context, EditProfileActivity::class.java).apply {
                putExtra("BLOG_NAME", blogName)
                putExtra("USER_NAME", userName)
                putExtra("USER_INTRODUCTION", userIntroduction)
            }

            startActivityForResult(intent, EDIT_PROFILE_REQUEST_CODE)
        }

        // item 구분선
        val dividerItemDecoration = DividerItemDecoration(binding.profilePostRv.context, LinearLayoutManager.VERTICAL)
        binding.profilePostRv.addItemDecoration(dividerItemDecoration)

        profileRVAdapter.setMyItemClickListener(object : ProfileRVAdapter.MyItemClickListener {
            override fun onRemoveItem(position: Int) {
                profileRVAdapter.removeItem(position)
            }
        })

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_PROFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // EditProfileActivity에서 전달된 수정된 프로필 정보 처리
            val editedBlogName = data?.getStringExtra("BLOG_NAME")
            val editedUserName = data?.getStringExtra("USER_NAME")
            val editedUserIntroduction = data?.getStringExtra("USER_INTRODUCTION")

            // 수정된 프로필 정보를 TextView에 반영
            binding.blogName.text = editedBlogName
            binding.userName.text = editedUserName
            binding.userIntroduction.text = editedUserIntroduction
        }
    }
}