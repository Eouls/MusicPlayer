package com.example.musicplayer.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
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
    private lateinit var postDB: PostDatabase
    private lateinit var userDB: UserDatabase
    private lateinit var profileRVAdapter: ProfileRVAdapter
    private val EDIT_PROFILE_REQUEST_CODE = 123

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        // DB 초기화
        userDB = UserDatabase.getInstance(requireContext())!!
        postDB = PostDatabase.getInstance(requireContext())!!


        // userData 로그캣 출력
        lifecycleScope.launch(Dispatchers.IO) {
            val allUsers = userDB.userDao().getUsers()
            allUsers.forEach { user ->
                Log.d("UserData", "ID: ${user.id}, Blog Name: ${user.blogName}, User Name: ${user.userName}, Introduction: ${user.introduction}")
            }
        }

        // 스크롤 시 툴바 위로
        val activity = activity as? AppCompatActivity
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity?.supportActionBar?.setDisplayShowHomeEnabled(true)


        // RecyclerView 설정
        profileRVAdapter = ProfileRVAdapter(ArrayList<Post>()) // 빈 리스트로 초기화
        binding.profilePostRv.adapter = profileRVAdapter
        binding.profilePostRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
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
                val postToRemove = profileRVAdapter.getPost(position)
                removePostFromDatabase(postToRemove)
                profileRVAdapter.removeItem(position)
            }
        })

        return binding.root
    }

    private fun removePostFromDatabase(post: Post) {
        lifecycleScope.launch(Dispatchers.IO) {
            postDB.postDao().delete(post)
        }
    }

    // 데이터베이스에서 포스트 목록을 가져와 어댑터에 설정
    private fun loadPostsFromDatabase() {
        lifecycleScope.launch(Dispatchers.IO) {
            val posts = postDB?.postDao()?.getPosts()
            posts?.let { profileRVAdapter.setData(it) }
        }
    }

    private fun loadUserData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val user = userDB.userDao().getUsers().lastOrNull()
            withContext(Dispatchers.Main) {
                user?.let {
                    binding.blogName.text = it.blogName
                    binding.userName.text = it.userName
                    binding.userIntroduction.text = it.introduction
                    it.coverImgPath?.let { path -> binding.coverImgIv.setImageURI(Uri.parse(path)) }
                    it.profileImgPath?.let { path -> binding.profileImgIv.setImageURI(Uri.parse(path)) }
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
            val coverImgPath = data?.getStringExtra("COVER_IMG_PATH")
            val profileImgPath = data?.getStringExtra("PROFILE_IMG_PATH")

            // 수정된 프로필 정보를 반영
            binding.blogName.text = editedBlogName
            binding.userName.text = editedUserName
            binding.userIntroduction.text = editedUserIntroduction
            coverImgPath?.let {
                binding.coverImgIv.setImageURI(Uri.parse(it))
            }
            profileImgPath?.let {
                binding.profileImgIv.setImageURI(Uri.parse(it))
            }

            // 수정된 사용자 정보를 데이터베이스에 저장
            updateUserInDatabase(coverImgPath?: "", profileImgPath?: "", editedBlogName ?: "", editedUserName ?: "", editedUserIntroduction ?: "")

            // 사용자 정보 업데이트
            loadUserData()
        }
    }
    private fun updateUserInDatabase(coverImgPath: String, profileImgPath: String, blogName: String, userName: String, introduction: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val newUser = User(coverImgPath = coverImgPath, profileImgPath = profileImgPath, blogName = blogName, userName = userName, introduction = introduction)
            userDB.userDao().insert(newUser)
        }
        // SharedPreferences에 사용자 이름 저장
        saveUserNameToPreferences(userName)
    }
}