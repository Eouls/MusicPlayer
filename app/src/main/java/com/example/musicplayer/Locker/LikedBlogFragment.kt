package com.example.musicplayer.Locker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.musicplayer.databinding.FragmentLockerLikedblogBinding

class LikedBlogFragment: Fragment() {
    lateinit var binding: FragmentLockerLikedblogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLockerLikedblogBinding.inflate(inflater, container, false)

        return binding.root
    }
}