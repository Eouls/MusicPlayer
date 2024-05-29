package com.example.musicplayer.blog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.databinding.FragmentBlogBinding
import com.example.musicplayer.post.Post
import com.example.musicplayer.post.PostDatabase

class BlogFragment: Fragment() {
    lateinit var binding: FragmentBlogBinding
    private lateinit var postDB: PostDatabase
    private lateinit var blogRVAdapter: BlogRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBlogBinding.inflate(inflater, container, false)


        // 데이터베이스에서 게시물 데이터 가져오기
        postDB = PostDatabase.getInstance(requireContext()) as PostDatabase
        if (postDB == null) {
            // 데이터베이스가 null인 경우, onCreateView 함수를 종료하고 null을 반환
            return super.onCreateView(inflater, container, savedInstanceState)
        }

        // RecyclerView 설정
        blogRVAdapter = BlogRVAdapter(ArrayList<Post>()) // 빈 리스트로 초기화
        binding.blogPostRv.adapter = blogRVAdapter
        binding.blogPostRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
        binding.blogPostRv.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL
            )
        )


        // post 삭제 클릭 이벤트
        blogRVAdapter.setMyItemClickListener(object : BlogRVAdapter.MyItemClickListener {
            override fun onRemoveItem(position: Int) {
                // blogRVAdapter.removeItem(position)
            }
        })

        // item 구분선
        val dividerItemDecoration = DividerItemDecoration(binding.blogPostRv.context, LinearLayoutManager.VERTICAL)
        binding.blogPostRv.addItemDecoration(dividerItemDecoration)

        // 데이터베이스에서 포스트 목록을 가져와 어댑터에 설정
        val postDatabase = PostDatabase.getInstance(requireContext())
        val posts = postDatabase?.postDao()?.getPosts()
        posts?.let { blogRVAdapter.setData(it) }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 데이터베이스에서 포스트 목록을 가져와 어댑터에 설정
        val postDatabase = PostDatabase.getInstance(requireContext())
        val posts = postDatabase?.postDao()?.getPosts()
        posts?.let {
            blogRVAdapter.setData(it)
            // RecyclerView를 가장 최근 항목으로 스크롤
            binding.blogPostRv.scrollToPosition(it.size - 1)
        }
    }

}