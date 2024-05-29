package com.example.musicplayer.locker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.musicplayer.databinding.FragmentLockerBinding
import com.google.android.material.tabs.TabLayoutMediator

class LockerFragment: Fragment() {
    lateinit var binding: FragmentLockerBinding
    private val tabList = arrayListOf("좋아요 한 곡", "좋아요 한 블로그")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLockerBinding.inflate(inflater, container, false)

        initViewPager()

        return binding.root
    }

    private fun initViewPager() {
        val lockerAdapter = LockerVPAdapter(this)

        binding.lockerViewpagerVp.adapter = lockerAdapter
        TabLayoutMediator(binding.lockerSegTb, binding.lockerViewpagerVp) { tab, position ->
            tab.text = tabList[position]
        }.attach()
    }
}