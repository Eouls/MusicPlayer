package com.example.musicplayer.Profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.databinding.FragmentProfileBinding
import com.example.musicplayer.post.Post
import com.example.musicplayer.post.PostDatabase

class ProfileFragment: Fragment() {
    lateinit var binding: FragmentProfileBinding
    private lateinit var postDatabase: PostDatabase
    private lateinit var profileRVAdapter: ProfileRVAdapter
    private val EDIT_PROFILE_REQUEST_CODE = 123

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)


        // 스크롤 시 툴바 세팅
        val activity = activity as? AppCompatActivity
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity?.supportActionBar?.setDisplayShowHomeEnabled(true)

        // 사용자의 이름을 Singleton 객체에 저장
        UserData.userName = binding.userName.text.toString()


        // 데이터베이스에서 게시물 데이터 가져오기
        postDatabase = PostDatabase.getInstance(requireContext()) as PostDatabase
        if (postDatabase == null) {
            // 데이터베이스가 null인 경우, onCreateView 함수를 종료하고 null을 반환
            return super.onCreateView(inflater, container, savedInstanceState)
        }

        // postDatabase의 모든 데이터 로그캣 출력
        val postt = postDatabase.postDao().getPosts()
        for (post in postt) {
            Log.d("PostData", "Title: ${post.title}, Content: ${post.content}")
        }

        // RecyclerView 설정
        profileRVAdapter = ProfileRVAdapter(ArrayList<Post>()) // 빈 리스트로 초기화
        binding.profilePostRv.adapter = profileRVAdapter
        binding.profilePostRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.profilePostRv.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL
            )
        )
        /*// RecyclerView 설정
        profileRVAdapter = ProfileRVAdapter(ArrayList())
        binding.profilePostRv.adapter = profileRVAdapter
        binding.profilePostRv.layoutManager = LinearLayoutManager(context)
        binding.profilePostRv.addItemDecoration(
            DividerItemDecoration(
                binding.profilePostRv.context,
                LinearLayoutManager.VERTICAL
            )
        )*/

        // 프로필 수정 버튼 클릭 시 EditProfileActivity 실행
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

        // post 삭제 클릭 이벤트
        profileRVAdapter.setMyItemClickListener(object : ProfileRVAdapter.MyItemClickListener {
            override fun onRemoveItem(position: Int) {
                profileRVAdapter.removeItem(position)
            }
        })

        // 데이터베이스에서 포스트 목록을 가져와 어댑터에 설정
        val postDatabase = PostDatabase.getInstance(requireContext())
        val posts = postDatabase?.postDao()?.getPosts()
        posts?.let { profileRVAdapter.setData(it) }

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

            // 수정된 사용자 이름을 Singleton 객체에 저장
            UserData.userName = editedUserName
        }
    }
}