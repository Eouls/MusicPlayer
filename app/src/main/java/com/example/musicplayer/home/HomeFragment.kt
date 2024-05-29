package com.example.musicplayer.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.Album.Album
import com.example.musicplayer.Album.AlbumRVAdapter
import com.example.musicplayer.Profile.ProfileRVAdapter
import com.example.musicplayer.R
import com.example.musicplayer.Song.SongDatabase
import com.example.musicplayer.databinding.FragmentHomeBinding
import com.example.musicplayer.post.Post
import com.example.musicplayer.post.PostDatabase

class HomeFragment: Fragment() {
    lateinit var binding: FragmentHomeBinding
    private var albumDatas = ArrayList<Album>()
    private lateinit var postDatabase: PostDatabase
    private lateinit var songDB: SongDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        inputDummyAlbums()

        songDB = SongDatabase.getInstance(requireContext())!!
        albumDatas.addAll(songDB.albumDao().getAlbums()) // songDB에서 album list를 가져옵니다.

        // 데이터베이스에서 게시물 데이터 가져오기
        postDatabase = PostDatabase.getInstance(requireContext()) as PostDatabase
        if (postDatabase == null) {
            // 데이터베이스가 null인 경우, onCreateView 함수를 종료하고 null을 반환
            return super.onCreateView(inflater, container, savedInstanceState)
        }

        // 더미데이터랑 Adapter 연결
        val albumRVAdapter = AlbumRVAdapter(albumDatas)
        val profileRVAdapter = ProfileRVAdapter(ArrayList<Post>())
        // 리사이클러뷰에 어댑터를 연결
        binding.homePopularAlbumAlbumRv.adapter = albumRVAdapter
        binding.homeReplayAlbumTitleRv.adapter = albumRVAdapter
        binding.homeMypostingRv.adapter = profileRVAdapter
        // 레이아웃 매니저 설정
        binding.homePopularAlbumAlbumRv.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.homeReplayAlbumTitleRv.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.homeMypostingRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // 데이터베이스에서 포스트 목록을 가져와 어댑터에 설정
        val postDatabase = PostDatabase.getInstance(requireContext())
        val posts = postDatabase?.postDao()?.getPosts()
        posts?.let { profileRVAdapter.setData(it) }

        return binding.root
    }

    private fun inputDummyAlbums() {
        val songDB = SongDatabase.getInstance(requireActivity())!!
        val songs = songDB.albumDao().getAlbums()

        if (songs.isNotEmpty()) return

        songDB.albumDao().insert(
            Album(
                1,
                "Armageddon - The 1st Album",
                "aespa",
                R.drawable.supernova
            )
        )
        songDB.albumDao().insert(
            Album(
                2,
                "How Sweet",
                "NewJeans",
                R.drawable.bubblegum
            )
        )
        songDB.albumDao().insert(
            Album(
                3,
                "SUPER REAL ME",
                "ILLIT",
                R.drawable.magnetic
            )
        )
        songDB.albumDao().insert(
            Album(
                4,
                "Impossible",
                "RIIZE",
                R.drawable.impossible
            )
        )
        songDB.albumDao().insert(
            Album(
                5,
                "NewJeans 2nd EP 'Get Up'",
                "NewJeans",
                R.drawable.newjeans
            )
        )
        songDB.albumDao().insert(
            Album(
                6,
                "BABYMONS7ER",
                "BABYMONSTER",
                R.drawable.sheesh
            )
        )
        songDB.albumDao().insert(
            Album(
                7,
                "TWS : 1st Mini Album 'Sparkling Blue'",
                "TWS",
                R.drawable.firstmeet
            )
        )
        songDB.albumDao().insert(
            Album(
                8,
                "Drama - The 4th Mini Album",
                "aespa",
                R.drawable.drama
            )
        )
        songDB.albumDao().insert(
            Album(
                9,
                "The Winning",
                "아이유 (IU)",
                R.drawable.shopper
            )
        )
        songDB.albumDao().insert(
            Album(
                10,
                "メズマライザー",
                "サツキ, 初音ミク, 重音テト",
                R.drawable.mesmerizer
            )
        )
    }
}