package com.example.musicplayer.Album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.musicplayer.Function.CustomSnackbar
import com.example.musicplayer.MainActivity
import com.example.musicplayer.R
import com.example.musicplayer.Song.Song
import com.example.musicplayer.databinding.FragmentAlbumBinding
import com.example.musicplayer.home.HomeFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson

class AlbumFragment:Fragment() {
    lateinit var binding : FragmentAlbumBinding
    private var gson: Gson = Gson()

    val albums = arrayListOf<Album>()
    var nowPos = 0
    lateinit var albumDB: AlbumDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumBinding.inflate(inflater, container, false)

        val albumJson = arguments?.getString("album")
        val album = gson.fromJson(albumJson, Album::class.java)
        setInit(album)

        binding.albumBackIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager
                .beginTransaction()
                .replace(R.id.main_frm, HomeFragment())
                .commitAllowingStateLoss()
        }

//        initClickListener()

        return binding.root
    }

    private fun setInit(album: Album?) {
        binding.albumImgIv.setImageResource(album?.coverImg!!)
        binding.albumSongTitle.text = album.title.toString()
        binding.albumSingerNameTv.text = album.singer.toString()
    }

//    private fun initClickListener() {
//        binding.albumUnlikeIv.setOnClickListener {
//            setLike(true)
//        }
//        binding.albumLikeIv.setOnClickListener {
//            setLike(false)
//        }
//    }

//    // 앨범 좋아요 구현
//    private fun setLike(isLike: Boolean){
//        albums[nowPos].isLike = isLike
//        albumDB.albumDao().updateIsLikeById(isLike,albums[nowPos].id)
//
//        if (isLike){
//            binding.albumUnlikeIv.visibility = View.GONE
//            binding.albumLikeIv.visibility = View.VISIBLE
//            CustomSnackbar.make(binding.root, "좋아요 한 곡에 담겼습니다.").setAnchorView(binding.albumAnchorLocation).show()
//
//        } else{
//            binding.albumUnlikeIv.visibility = View.VISIBLE
//            binding.albumLikeIv.visibility = View.GONE
//            CustomSnackbar.make(binding.root, "좋아요 한 곡이 취소되었습니다.").setAnchorView(binding.albumAnchorLocation).show()
//        }
//    }

}