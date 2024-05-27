package com.example.musicplayer

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayer.Blog.BlogFragment
import com.example.musicplayer.Home.HomeFragment
import com.example.musicplayer.Profile.ProfileFragment
import com.example.musicplayer.SavedSong.SavedSongFragment
import com.example.musicplayer.Song.Song
import com.example.musicplayer.Song.SongActivity
import com.example.musicplayer.Song.SongDatabase
import com.example.musicplayer.databinding.ActivityMainBinding
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    private var gson: Gson = Gson()
    val songs = arrayListOf<Song>()
    lateinit var songDB: SongDatabase
    var nowPos = 0

    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private val updateSeekBarTask = object : Runnable {
        override fun run() {
            if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                binding.mainSeekbarSb.progress = mediaPlayer!!.currentPosition
                handler.postDelayed(this, 1000)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inputDummySongs()
        initClickListener()
        initPlayList()
        initBottomNavigation()

        binding.mainPlayerCl.setOnClickListener {
            val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
            editor.putInt("songId", songs[nowPos].id)
            editor.apply()

            mediaPlayer?.release() // 미디어 플레이어가 갖고 있던 리소스 해제
            mediaPlayer = null // 미디어 플레이어 해제
            val intent = Intent(this, SongActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initClickListener() {
        binding.mainMiniplayBtn.setOnClickListener {
            setPlayerStatus(true)
        }
        binding.mainPauseBtn.setOnClickListener {
            setPlayerStatus(false)
            Log.d("pausebtn", songs[nowPos].second.toString())
            Log.d("pausebtn2", mediaPlayer?.currentPosition.toString())
        }
        binding.mainMinipreviousIv.setOnClickListener {
            moveSong(-1)
        }
        binding.mainMininextIv.setOnClickListener {
            moveSong(+1)
        }
    }

    private fun setPlayerStatus(isPlaying: Boolean) {
        if (isPlaying) {
            binding.mainMiniplayBtn.visibility = View.GONE
            binding.mainPauseBtn.visibility = View.VISIBLE
            mediaPlayer?.start()
            handler.post(updateSeekBarTask)
        } else {
            binding.mainMiniplayBtn.visibility = View.VISIBLE
            binding.mainPauseBtn.visibility = View.GONE
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            }
            handler.removeCallbacks(updateSeekBarTask)
        }
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

//        startTimer()
        mediaPlayer?.release()
        mediaPlayer = null

        setMiniPlayer(songs[nowPos])
        setPlayerStatus(true)
    }

    private fun initPlayList(){
        songDB = SongDatabase.getInstance(this)!!
        songs.addAll(songDB.songDao().getSongs())
    }
    private fun setMiniPlayer(song: Song) {
        binding.mainMiniplayerTitleTv.text = song.title
        binding.mainMiniplayerSingerTv.text = song.singer
        binding.mainSeekbarSb.max = song.playTime * 1000

//        while (mediaPlayer?.isPlaying == true) {
//            runOnUiThread {
//                binding.mainSeekbarSb.progress = mediaPlayer!!.currentPosition
//            }
//        }

//        binding.mainSeekbarSb.progress = (songs[nowPos].second * 1000) / song.playTime

        val music = resources.getIdentifier(song.music, "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this, music)
        mediaPlayer?.seekTo(songs[nowPos].second * 1000)

        binding.mainSeekbarSb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                    songs[nowPos].second = progress / 1000
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                mediaPlayer?.pause()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (songs[nowPos].isPlaying) {
                    mediaPlayer?.start()
                }
            }
        })

        setPlayerStatus(song.isPlaying)
    }

    override fun onStart() {
        super.onStart()
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        // songId는 SongActivity에서 onPause가 호출되었을 때
        // 재생 중이던 노래의 id(pk)이다.
        val songId = sharedPreferences.getInt("songId", 0)

        // SongDatabase의 인스턴스를 가져온다.
        val songDB = SongDatabase.getInstance(this)!!

        songs[nowPos] = if (songId == 0) { // 재생 중이던 노래가 없었으면
            songDB.songDao().getSong(1)
        } else { // 재생 중이던 노래가 있었으면
            songDB.songDao().getSong(songId)
        }

        Log.d("song ID", songs[nowPos].id.toString())
        setMiniPlayer(songs[nowPos]) // song의 정보로 MiniPlayer를 setting
    }

    override fun onResume() {
        super.onResume()

        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val songId = sharedPreferences.getInt("songId", 0)
        songs[nowPos].second = sharedPreferences.getInt("songSecond", 0)

        nowPos = getPlayingSongPosition(songId)
        setMiniPlayer(songs[nowPos])
    }

    override fun onPause() {
        super.onPause()
        Log.d("pause", "pause 발생")
        songs[nowPos].second = binding.mainSeekbarSb.progress / 1000
        Log.d("mainpauseprogress", binding.mainSeekbarSb.progress.toString())

        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("songSecond", songs[nowPos].second)
        Log.d("mainpause", songs[nowPos].second.toString())

        editor.apply()
    }

    private fun getPlayingSongPosition(songId: Int): Int{
        for (i in 0 until songs.size){
            if (songs[i].id == songId){
                return i
            }
        }
        return 0
    }

    // 더미 데이터 삽입
    private fun inputDummySongs() {
        val songDB = SongDatabase.getInstance(this)!!
        val songs = songDB.songDao().getSongs()

        // songs에 데이터가 이미 존재해 더미 데이터를 삽입할 필요가 없음
        if (songs.isNotEmpty()) return

        // songs에 데이터가 없을 때에는 더미 데이터를 삽입해주어야 함
        songDB.songDao().insert(
            Song(
                "Supernova",
                "aespa",
                0,
                179,
                false,
                "music_supernova",
                R.drawable.supernova,
                false,
                1
            )
        )
        songDB.songDao().insert(
            Song(
                "Bubble Gum",
                "NewJeans",
                0,
                201,
                false,
                "music_bubblegum",
                R.drawable.bubblegum,
                false,
                2
            )
        )
        songDB.songDao().insert(
            Song(
                "Magenetic",
                "ILLIT",
                0,
                161,
                false,
                "music_magnetic",
                R.drawable.magnetic,
                false,
                3
            )
        )
        songDB.songDao().insert(
            Song(
                "Impossible",
                "RIIZE",
                0,
                183,
                false,
                "music_impossible",
                R.drawable.impossible,
                false,
                4
            )
        )
        songDB.songDao().insert(
            Song(
                "ETA",
                "NewJeans",
                0,
                152,
                false,
                "music_eta",
                R.drawable.newjeans,
                false,
                5
            )
        )
        songDB.songDao().insert(
            Song(
                "Super shy",
                "NewJeans",
                0,
                155,
                false,
                "music_supershy",
                R.drawable.newjeans,
                false,
                5
            )
        )
        songDB.songDao().insert(
            Song(
                "SHEESH",
                "BABYMONSTER",
                0,
                171,
                false,
                "music_sheesh",
                R.drawable.sheesh,
                false,
                6
            )
        )
        songDB.songDao().insert(
            Song(
                "첫 만남은 계획대로 되지 않아",
                "TWS",
                0,
                153,
                false,
                "music_firstmeet",
                R.drawable.firstmeet,
                false,
                7
            )
        )
        songDB.songDao().insert(
            Song(
                "Drama",
                "aespa",
                0,
                215,
                false,
                "music_drama",
                R.drawable.drama,
                false,
                8
            )
        )
        songDB.songDao().insert(
            Song(
                "Shopper",
                "아이유 (IU)",
                0,
                216,
                false,
                "music_shopper",
                R.drawable.shopper,
                false,
                9
            )
        )
        songDB.songDao().insert(
            Song(
                "メズマライザー",
                "サツキ, 初音ミク, 重音テト",
                0,
                157,
                false,
                "music_mesmerizer",
                R.drawable.mesmerizer,
                false,
                10
            )
        )
        // DB에 데이터가 잘 들어갔는지 확인
        val _songs = songDB.songDao().getSongs()
        Log.d("DB data", _songs.toString())

    }

    // 바텀 네비게이션 뷰
    private fun initBottomNavigation(){

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_bnv, HomeFragment())
            .commitAllowingStateLoss()

        binding.mainBnv.setOnItemSelectedListener{ item ->
            when (item.itemId) {

                R.id.homeFragment -> { // HomeFragment 로 전환
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.blogFragment -> { // BlogFragment 로 전환
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, BlogFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.savedSongFragment -> { // SavedSongFragment 로 전환
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, SavedSongFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.profileFragment -> { // ProfileFragment 로 전환
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, ProfileFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }
}