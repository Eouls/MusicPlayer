package com.example.musicplayer.Song

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayer.databinding.ActivitySongBinding

class SongActivity: AppCompatActivity() {
    lateinit var binding: ActivitySongBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initClickListener()
    }

    private fun initClickListener() {
        binding.songDownIb.setOnClickListener {
            finish()
        }
        binding.songPlayIv.setOnClickListener {
            setPlayerStatus(true)
        }
        binding.songPauseIv.setOnClickListener {
            setPlayerStatus(false)
        }
//        binding.songNextIv.setOnClickListener {
//            moveSong(+1)
//        }
//        binding.songPreviousIv.setOnClickListener {
//            moveSong(-1)
//        }
//        binding.songLikeOffIv.setOnClickListener {
//            setLike(true)
//        }
//        binding.songLikeOnIv.setOnClickListener {
//            setLike(false)
//        }
    }

    private fun setPlayerStatus(isPlaying : Boolean){
//        songs[nowPos].isPlaying = isPlaying
//        timer.isPlaying = isPlaying

        if(isPlaying) { // 재생중
            binding.songPlayIv.visibility = View.GONE
            binding.songPauseIv.visibility = View.VISIBLE
//            mediaPlayer?.start()
        } else { // 일시정지
            binding.songPlayIv.visibility = View.VISIBLE
            binding.songPauseIv.visibility = View.GONE
//            if (mediaPlayer?.isPlaying == true) { // 재생 중이 아닐 때에 pause를 하면 에러가 나기 때문에 이를 방지
//                mediaPlayer?.pause()
//            }
        }
    }
}