package com.example.musicplayer.locker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.Song.Song
import com.example.musicplayer.Song.SongDatabase
import com.example.musicplayer.databinding.FragmentLockerLikedsongBinding

class LikedSongFragment: Fragment() {
    lateinit var binding: FragmentLockerLikedsongBinding
    lateinit var songDB: SongDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLockerLikedsongBinding.inflate(inflater, container, false)

        songDB = SongDatabase.getInstance(requireContext())!!

        // item 구분선
        val dividerItemDecoration = DividerItemDecoration(binding.lockerLikedsongRv.context, LinearLayoutManager.VERTICAL)
        binding.lockerLikedsongRv.addItemDecoration(dividerItemDecoration)


        return binding.root
    }

    override fun onStart() {
        super.onStart()
        initRecyclerview()
    }

    private fun initRecyclerview(){
        binding.lockerLikedsongRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val songRVAdapter = LikedSongRVAdpater()

        songRVAdapter.setMyItemClickListener(object : LikedSongRVAdpater.DeleteItemClickListener{
            override fun onRemoveItem(songId: Int) {
                songDB.songDao().updateIsLikeById(false, songId)
            }

        })

        binding.lockerLikedsongRv.adapter = songRVAdapter

        songRVAdapter.addSongs(songDB.songDao().getLikedSongs(true) as ArrayList<Song>)
    }
}