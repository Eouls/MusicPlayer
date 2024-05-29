package com.example.musicplayer.locker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.Album.Album
import com.example.musicplayer.Album.AlbumDatabase
import com.example.musicplayer.Song.Song
import com.example.musicplayer.databinding.FragmentLockerLikedAlbumBinding

class LikedAlbumFragment: Fragment() {
    lateinit var binding: FragmentLockerLikedAlbumBinding
    lateinit var albumDB: AlbumDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLockerLikedAlbumBinding.inflate(inflater, container, false)

        albumDB = AlbumDatabase.getInstance(requireContext())!!

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        initRecyclerview()
    }

    private fun initRecyclerview(){
        binding.lockerLikedAlbumRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val albumRVAdapter = LikedAlbumRVAdapter()
//
//        LikedAlbumRVAdapter.setMyItemClickListener(object : LikedAlbumRVAdapter.DeleteItemClickListener{
//            override fun onRemoveItem(albumId: Int) {
//                albumDB.albumDao().updateIsLikeById(false, albumId)
//            }
//
//        })

        binding.lockerLikedAlbumRv.adapter = albumRVAdapter

        albumRVAdapter.addAlbums(albumDB.albumDao().getLikedAlbums(true) as ArrayList<Album>)
    }
}