package com.example.musicplayer.Song

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayer.databinding.ActivitySongBinding
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Timer
import kotlin.math.log

class SongActivity: AppCompatActivity() {
    lateinit var binding: ActivitySongBinding
    lateinit var timer: Timer
    private var mediaPlayer: MediaPlayer? = null // 추후에 미디어 플레이어 해제를 위해 nullable로 선언
    private var gson: Gson = Gson()

    val songs = arrayListOf<Song>()
    lateinit var songDB: SongDatabase
    var nowPos = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 재생 중이던 노래가 있으면 해당 노래의 정보로 화면을 구성하고,
        // 재생 중이던 노래가 없으면 0번 index의 노래의 정보로 화면을 구성한다.
        initSong()

        initPlayList() // SongDB에 있는 모든 노래를 songs에 저장
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
        binding.songNextIv.setOnClickListener {
            moveSong(+1)
        }
        binding.songPreviousIv.setOnClickListener {
            moveSong(-1)
        }
        binding.songLikeOffIv.setOnClickListener {
            setLike(true)
        }
        binding.songLikeOnIv.setOnClickListener {
            setLike(false)
        }
    }

    private fun initSong() {
        // SongDatabase 인스턴스를 초기화하고 songs 리스트를 채웁니다.
        songDB = SongDatabase.getInstance(this)!!
        songs.addAll(songDB.songDao().getSongs())

        if (songs.isEmpty()) {
            Toast.makeText(this, "No songs available", Toast.LENGTH_SHORT).show()
            finish() // 노래가 없으면 액티비티를 종료합니다.
            return
        }

        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 0)

        nowPos = getPlayingSongPosition(songId)
        songs[nowPos].second = spf.getInt("songSecond", 0) // 저장된 재생 위치를 받아옵니다.


        Log.d("now Song ID", songs[nowPos].id.toString())

        startTimer()
        setPlayer(songs[nowPos])
    }

    // songId로 position을 얻는 메서드
    private fun getPlayingSongPosition(songId: Int): Int {
        for (i in 0 until songs.size) {
            if (songs[i].id == songId) {
                return i
            }
        }
        return 0
    }

    private fun moveSong(direct: Int) {
        if (nowPos + direct < 0) {
            Toast.makeText(this, "first song", Toast.LENGTH_SHORT).show()
            return
        }
        if (nowPos + direct >= songs.size) {
            Toast.makeText(this, "last song", Toast.LENGTH_SHORT).show()
            return
        }

        nowPos += direct

        timer.interrupt()
        songs[nowPos].second = 0  // 노래 옮길 때마다 저장된 재생시간 초기화
        startTimer()


        mediaPlayer?.release() // 미디어 플레이어가 갖고 있던 리소스 해제
        mediaPlayer = null // 미디어 플레이어 해제

        setPlayerStatus(true) // 곡이 넘어가면 재생으로 바뀌게 함
        setPlayer(songs[nowPos])
    }

    private fun initPlayList() {
        songDB = SongDatabase.getInstance(this)!!
        songs.addAll(songDB.songDao().getSongs())
    }

    private fun setPlayer(song : Song) {
        binding.songMusicTitleTv.text = song.title
        binding.songMusicSingerTv.text = song.singer
        binding.songStartTv.text = String.format("%02d:%02d", songs[nowPos].second / 60, songs[nowPos].second % 60)
        binding.songEndTv.text = String.format("%02d:%02d", song.playTime / 60, song.playTime % 60)
        binding.songAlbumIv.setImageResource(song.coverImg!!)
        binding.songSeekbarSb.progress = (songs[nowPos].second * 1000 / song.playTime)

        val music = resources.getIdentifier(song.music, "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this, music)
        val timeformat = SimpleDateFormat("mm:ss")
        binding.songEndTv.text = timeformat.format(mediaPlayer?.duration)
        // SeekBar의 최대값을 노래의 총 재생 시간으로 설정
        binding.songSeekbarSb.max = song.playTime * 1000
        mediaPlayer?.seekTo(songs[nowPos].second * 1000) // 저장된 위치로 이동


        // progress 터치로 재생시간 옮기기
        binding.songSeekbarSb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    timer.interrupt()
//                    val newSecond = (progress * song.playTime) / 100
                    val newSecond = progress / 1000
                    mediaPlayer?.seekTo(newSecond * 1000)
                    binding.songStartTv.text = String.format("%02d:%02d", newSecond / 60, newSecond % 60)
                    songs[nowPos].second = newSecond
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // 사용자가 터치하기 시작할 때 호출
                mediaPlayer?.pause()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // 사용자가 터치를 끝냈을 때 호출
                if (songs[nowPos].isPlaying) {
                    mediaPlayer?.start()
                    startTimer()
                }
            }

        })

        if (song.isLike){
            binding.songLikeOffIv.visibility = View.GONE
            binding.songLikeOnIv.visibility = View.VISIBLE
        } else{
            binding.songLikeOffIv.visibility = View.VISIBLE
            binding.songLikeOnIv.visibility = View.GONE
        }

        setPlayerStatus(song.isPlaying)
    }

    private fun setLike(isLike: Boolean){
        songs[nowPos].isLike = isLike
        songDB.songDao().updateIsLikeById(isLike,songs[nowPos].id)

        if (isLike){
            binding.songLikeOffIv.visibility = View.GONE
            binding.songLikeOnIv.visibility = View.VISIBLE
//            CustomSnackbar.make(binding.root, "좋아요 한 곡에 담겼습니다.").setAnchorView(binding.songRelatedIv).show()

        } else{
            binding.songLikeOffIv.visibility = View.VISIBLE
            binding.songLikeOnIv.visibility = View.GONE
//            CustomSnackbar.make(binding.root, "좋아요 한 곡이 취소되었습니다.").setAnchorView(binding.songRelatedIv).show()
        }
    }

    private fun setPlayerStatus(isPlaying : Boolean){
        songs[nowPos].isPlaying = isPlaying
        if (::timer.isInitialized) {
            timer.isPlaying = isPlaying
        }

        if(isPlaying) { // 재생중
            binding.songPlayIv.visibility = View.GONE
            binding.songPauseIv.visibility = View.VISIBLE
            mediaPlayer?.start()
        } else { // 일시정지
            binding.songPlayIv.visibility = View.VISIBLE
            binding.songPauseIv.visibility = View.GONE
            if (mediaPlayer?.isPlaying == true) { // 재생 중이 아닐 때에 pause를 하면 에러가 나기 때문에 이를 방지
                mediaPlayer?.pause()
            }
        }
    }

    private fun startTimer() {
        timer = Timer(songs[nowPos].playTime, songs[nowPos].isPlaying)
        timer.start()
    }

    inner class Timer(private val playTime: Int, var isPlaying: Boolean = true) : Thread() {
        private var second : Int = songs[nowPos].second
        private var mills : Float = (songs[nowPos].second * 1000).toFloat()

        override fun run() {
            super.run()
            try {
                while (true) {
                    if (second >= playTime) {
                        runOnUiThread { // second 값과 playtime 값이 같아지면 다음 곡으로 이동
                            moveSong(+1)
                        }
                        break
                    }

                    if (isPlaying) {
                        sleep(50)
                        mills += 50

                        runOnUiThread {
                            binding.songSeekbarSb.progress = mills.toInt()
                        }

                        if (mills % 1000 == 0f) { // 1초
                            runOnUiThread {
                                binding.songStartTv.text = String.format("%02d:%02d", second / 60, second % 60)
                            }
                            second++
                        }
                    }
                }
            } catch (e: InterruptedException) {
                Log.d("Song", "쓰레드가 죽었습니다. ${e.message}")
            }
        }
    }

    override fun onPause() { // 사용자가 포커스를 잃었을 때 음악 중지
        super.onPause()
//        songs[nowPos].second = ((binding.songSeekbarSb.progress * songs[nowPos].playTime) / 100) / 1000
        songs[nowPos].second = binding.songSeekbarSb.progress / 1000
        songs[nowPos].isPlaying = false
        setPlayerStatus(false) // 음악을 중지하기 위해 false 값

        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putInt("songId", songs[nowPos].id)
        editor.putInt("songSecond", songs[nowPos].second)
        Log.d("songSecond", songs[nowPos].second.toString())

        editor.apply()
    }

    override fun onResume() {
        super.onResume()

        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val songId = sharedPreferences.getInt("songId", 0)
        songs[nowPos].second = sharedPreferences.getInt("songSecond", 0)
        Log.d("resume", songs[nowPos].second.toString())
        nowPos = getPlayingSongPosition(songId)

        setPlayer(songs[nowPos])
        setPlayerStatus(true)
    }
}