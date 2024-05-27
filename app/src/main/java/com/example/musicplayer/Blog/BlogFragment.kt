package com.example.musicplayer.Blog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.musicplayer.databinding.FragmentBlogBinding

class BlogFragment: Fragment() {
    lateinit var binding: FragmentBlogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBlogBinding.inflate(inflater, container, false)

        return binding.root
    }
}