package com.example.musicplayer.profile

import android.app.Activity
import android.content.Context
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
import com.example.musicplayer.user.User
import com.example.musicplayer.user.UserDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment: Fragment() {
    lateinit var binding: FragmentProfileBinding
    private lateinit var postDatabase: PostDatabase
    private lateinit var profileRVAdapter: ProfileRVAdapter
    private lateinit var userDatabase: UserDatabase
    private val EDIT_PROFILE_REQUEST_CODE = 123

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        // UserDatabase 초기화
        userDatabase = UserDatabase.getInstance(requireContext())!!

        GlobalScope.launch(Dispatchers.IO) {
            val allUsers = userDatabase.userDao().getUsers()
            allUsers.forEach { user ->
                Log.d("UserData", "ID: ${user.id}, Blog Name: ${user.blogName}, User Name: ${user.userName}, Introduction: ${user.introduction}")
            }
        }

        // 스크롤 시 툴바 세팅
        val activity = activity as? AppCompatActivity
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity?.supportActionBar?.setDisplayShowHomeEnabled(true)

        // 데이터베이스에서 게시물 데이터 가져오기
        postDatabase = PostDatabase.getInstance(requireContext()) as PostDatabase
        if (postDatabase == null) {
            // 데이터베이스가 null인 경우, onCreateView 함수를 종료하고 null을 반환
            return super.onCreateView(inflater, container, savedInstanceState)
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

        // 게시물 데이터 가져오기
        loadPostsFromDatabase()

        // 사용자 정보 가져오기
        loadUserData()

        // 프로필 수정 버튼 클릭 시 EditProfileActivity 실행
        binding.settingBtn.setOnClickListener {
            // Fragment에서 TextView 값 추출
            val blogName = binding.blogName.text.toString()
            val userName = binding.userName.text.toString()
            val userIntroduction = binding.userIntroduction.text.toString()

            // SharedPreferences에 사용자 이름 저장
            saveUserNameToPreferences(userName)

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

    

    private fun loadPostsFromDatabase() {
        GlobalScope.launch(Dispatchers.IO) {
            val postsFromDatabase = postDatabase.postDao().getPosts()
            withContext(Dispatchers.Main) {
                profileRVAdapter.setData(postsFromDatabase)
            }
        }
    }

    private fun loadUserData() {
        GlobalScope.launch(Dispatchers.IO) {
            val user = userDatabase.userDao().getUsers().firstOrNull()
            withContext(Dispatchers.Main) {
                user?.let {
                    binding.blogName.text = it.blogName
                    binding.userName.text = it.userName
                    binding.userIntroduction.text = it.introduction
                }
            }
        }
    }

    // sharePreferences에 수정된 사용자 이름 저장
    private fun saveUserNameToPreferences(userName: String) {
        val sharedPreferences = requireContext().getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("USER_NAME", userName)
        editor.apply()
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

            // 수정된 사용자 정보를 데이터베이스에 저장
            updateUserInDatabase(editedBlogName ?: "", editedUserName ?: "", editedUserIntroduction ?: "")

            // 사용자 정보 업데이트
            loadUserData()
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