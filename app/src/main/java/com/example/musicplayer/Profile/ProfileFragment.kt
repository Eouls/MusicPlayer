package com.example.musicplayer.Profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.MainActivity
import com.example.musicplayer.R
import com.example.musicplayer.databinding.FragmentProfileBinding

class ProfileFragment: Fragment() {
    lateinit var binding: FragmentProfileBinding
    private var postDatas = ArrayList<Post>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)


        postDatas.apply {
            add(Post(R.drawable.img_post_1, "제목1", "내용1", "2024.05.28"))
            add(Post(R.drawable.img_post_2, "제목2", "내용2\n김기찬 군은 돼지일까요 아닐까요 ?", "2024.05.27"))
            add(Post(R.drawable.img_post_3, "제목3", "내용3\n정답은요...", "2024.05.26"))
            add(Post(R.drawable.img_post_4, "제목4", "내용4\n돼지였습니다ㅡ!!", "2024.05.25"))
            add(Post(R.drawable.img_post_5, "제목5", "내용5\n^0^","2024.05.24"))
        }

        val savedSongRVAdapter = ProfileRVAdapter(postDatas)
        binding.profilePostRv.adapter = savedSongRVAdapter
        binding.profilePostRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        savedSongRVAdapter.setMyItemClickListener(object : ProfileRVAdapter.MyItemClickListener {
            override fun onRemoveItem(position: Int) {
                savedSongRVAdapter.removeItem(position)
            }
        })


        binding.settingBtn.setOnClickListener {
            startActivity(Intent(requireActivity(), EditProfileActivity::class.java))
        }

        return binding.root
    }

}