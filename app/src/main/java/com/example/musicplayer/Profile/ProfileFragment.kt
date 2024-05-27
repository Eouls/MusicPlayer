package com.example.musicplayer.Profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
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
            add(Post(R.drawable.img_post_1, "제목1", "내용1"))
            add(Post(R.drawable.img_post_1, "제목2", "내용2\n^_^"))
            add(Post(R.drawable.img_post_1, "제목3", "내용3\nㅇㅅㅇ"))
            add(Post(R.drawable.img_post_1, "제목4", "내용4\n=ㅅ="))
            add(Post(R.drawable.img_post_1, "제목5", "내용5\nㅍ_ㅍ"))
        }


        val savedSongRVAdapter = ProfileRVAdapter(postDatas)
        binding.profilePostRv.adapter = savedSongRVAdapter
        binding.profilePostRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        savedSongRVAdapter.setMyItemClickListener(object : ProfileRVAdapter.MyItemClickListener {
            override fun onRemoveItem(position: Int) {
                savedSongRVAdapter.removeItem(position)
            }
        })

        return binding.root
    }

}