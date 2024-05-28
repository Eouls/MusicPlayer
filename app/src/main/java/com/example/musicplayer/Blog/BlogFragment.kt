package com.example.musicplayer.Blog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.Profile.Post
import com.example.musicplayer.R
import com.example.musicplayer.databinding.FragmentBlogBinding

class BlogFragment: Fragment() {
    lateinit var binding: FragmentBlogBinding
    private var postDatas = ArrayList<Post>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBlogBinding.inflate(inflater, container, false)

        postDatas.apply {
            add(Post(R.drawable.img_post_1, "제목1", "기찬", "내용1", "2024.05.28"))
            add(Post(R.drawable.img_post_2, "제목2", "다희","내용2\n김기찬 군은 돼지일까요 아닐까요 ?", "2024.05.27"))
            add(Post(R.drawable.img_post_3, "제목3", "기찬","내용3\n정답은요...", "2024.05.26"))
            add(Post(R.drawable.img_post_4, "제목4", "다희","내용4\n돼지였습니다ㅡ!!", "2024.05.25"))
            add(Post(R.drawable.img_post_5, "제목5", "기찬","내용5\n^0^","2024.05.24"))
        }

        val blogRVAdapter = BlogRVAdapter(postDatas)
        binding.blogPostRv.adapter = blogRVAdapter
        binding.blogPostRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        blogRVAdapter.setMyItemClickListener(object : BlogRVAdapter.MyItemClickListener {
            override fun onRemoveItem(position: Int) {
                blogRVAdapter.removeItem(position)
            }
        })

        // item 구분선
        val dividerItemDecoration = DividerItemDecoration(binding.blogPostRv.context, LinearLayoutManager.VERTICAL)
        binding.blogPostRv.addItemDecoration(dividerItemDecoration)

        blogRVAdapter.setMyItemClickListener(object : BlogRVAdapter.MyItemClickListener {
            override fun onRemoveItem(position: Int) {
                blogRVAdapter.removeItem(position)
            }
        })

        return binding.root
    }
}