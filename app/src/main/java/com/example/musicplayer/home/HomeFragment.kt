package com.example.musicplayer.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.Album.Album
import com.example.musicplayer.Album.AlbumFragment
import com.example.musicplayer.Album.AlbumRVAdapter
import com.example.musicplayer.MainActivity
import com.example.musicplayer.R
import com.example.musicplayer.Song.Song
import com.example.musicplayer.Song.SongDatabase
import com.example.musicplayer.databinding.FragmentHomeBinding
import com.example.musicplayer.post.Post
import com.example.musicplayer.post.PostDatabase
import com.example.musicplayer.profile.ProfileRVAdapter
import com.google.gson.Gson


class HomeFragment: Fragment() {
    lateinit var binding: FragmentHomeBinding
    private var albumDatas = ArrayList<Album>()
    private var fastSongs = ArrayList<Song>()
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
        fastSongs.addAll(songDB.songDao().getSongs())

        // 데이터베이스에서 게시물 데이터 가져오기
        postDatabase = PostDatabase.getInstance(requireContext()) as PostDatabase
        if (postDatabase == null) {
            // 데이터베이스가 null인 경우, onCreateView 함수를 종료하고 null을 반환
            return super.onCreateView(inflater, container, savedInstanceState)
        }

        // 더미데이터랑 Adapter 연결
        val albumRVAdapter = AlbumRVAdapter(albumDatas)
        val profileRVAdapter = ProfileRVAdapter(ArrayList<Post>())
        val fastSongRVAdpater = FastSongRVAdpater(fastSongs)
        // 리사이클러뷰에 어댑터를 연결
        binding.homePopularAlbumAlbumRv.adapter = albumRVAdapter
        binding.homeReplayAlbumTitleRv.adapter = albumRVAdapter
        binding.homeMypostingRv.adapter = profileRVAdapter
        binding.homeFastSongRv.adapter = fastSongRVAdpater
        // 레이아웃 매니저 설정
        binding.homePopularAlbumAlbumRv.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.homeReplayAlbumTitleRv.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.homeMypostingRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.homeFastSongRv.layoutManager = GridLayoutManager(context, 4, GridLayoutManager.HORIZONTAL, false)

        // 데이터베이스에서 포스트 목록을 가져와 어댑터에 설정
        val postDatabase = PostDatabase.getInstance(requireContext())
        val posts = postDatabase?.postDao()?.getPosts()
        posts?.let { profileRVAdapter.setData(it) }

        // 앨범 아이템 클릭 시 프래그 먼트 전환
        albumRVAdapter.setMyItemClickListener(object : AlbumRVAdapter.MyItemClickListener {
            override fun onItemClick(album: Album) {
                changeAlbumFragment(album)
            }
        })

        return binding.root
    }

    // 인기 급상승 앨범 아이템 클릭 시 프래그먼트 전환
    private fun changeAlbumFragment(album: Album) {
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, AlbumFragment().apply {
                arguments = Bundle().apply {
                    val gson = Gson()
                    val albumJson = gson.toJson(album)
                    putString("album", albumJson)
                }
            })
            .commitAllowingStateLoss()
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
                R.drawable.supernova,
                false
            )
        )
        songDB.albumDao().insert(
            Album(
                2,
                "How Sweet",
                "NewJeans",
                R.drawable.bubblegum,
                false
            )
        )
        songDB.albumDao().insert(
            Album(
                3,
                "SUPER REAL ME",
                "ILLIT",
                R.drawable.magnetic,
                false
            )
        )
        songDB.albumDao().insert(
            Album(
                4,
                "Impossible",
                "RIIZE",
                R.drawable.impossible,
                false
            )
        )
        songDB.albumDao().insert(
            Album(
                5,
                "NewJeans 2nd EP 'Get Up'",
                "NewJeans",
                R.drawable.newjeans,
                false
            )
        )
        songDB.albumDao().insert(
            Album(
                6,
                "BABYMONS7ER",
                "BABYMONSTER",
                R.drawable.sheesh,
                false
            )
        )
        songDB.albumDao().insert(
            Album(
                7,
                "TWS : 1st Mini Album 'Sparkling Blue'",
                "TWS",
                R.drawable.firstmeet,
                false
            )
        )
        songDB.albumDao().insert(
            Album(
                8,
                "Drama - The 4th Mini Album",
                "aespa",
                R.drawable.drama,
                false
            )
        )
        songDB.albumDao().insert(
            Album(
                9,
                "The Winning",
                "아이유 (IU)",
                R.drawable.shopper,
                false
            )
        )
        songDB.albumDao().insert(
            Album(
                10,
                "メズマライザー",
                "サツキ, 初音ミク, 重音テト",
                R.drawable.mesmerizer,
                false
            )
        )
    }
}