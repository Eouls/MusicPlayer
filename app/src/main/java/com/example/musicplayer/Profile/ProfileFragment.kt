package com.example.musicplayer.Profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.R
import com.example.musicplayer.databinding.FragmentProfileBinding

class ProfileFragment: Fragment() {
    lateinit var binding: FragmentProfileBinding
    private var myPostDatas = ArrayList<Post>()

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


        // 프로필 수정 클릭리스너
        binding.settingBtn.setOnClickListener {
            startActivity(Intent(requireActivity(), EditProfileActivity::class.java))
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

}