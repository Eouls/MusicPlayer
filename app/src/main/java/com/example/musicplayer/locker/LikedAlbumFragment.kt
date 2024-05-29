package com.example.musicplayer.locker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.musicplayer.databinding.FragmentLockerLikedAlbumBinding

class LikedAlbumFragment: Fragment() {
    lateinit var binding: FragmentLockerLikedAlbumBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLockerLikedAlbumBinding.inflate(inflater, container, false)

        return binding.root
    }
}