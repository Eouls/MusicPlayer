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
import com.example.musicplayer.post.PostActivity
import com.example.musicplayer.Home.HomeFragment
import com.example.musicplayer.Profile.ProfileFragment
import com.example.musicplayer.Locker.LockerFragment
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
        setTheme(R.style.Theme_MusicPlayer)
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
        }
        binding.mainMinipreviousIv.setOnClickListener {
            moveSong(-1)
        }
        binding.mainMininextIv.setOnClickListener {
            moveSong(+1)
        }
    }

    private fun setPlayerStatus(isPlaying: Boolean) {
        if (isPlaying) { // 재생 중
            binding.mainMiniplayBtn.visibility = View.GONE
            binding.mainPauseBtn.visibility = View.VISIBLE
            mediaPlayer?.start()

            // 추가: 현재 노래의 재생 시간을 체크하는 스레드 시작
            handler.post(updateSeekBarTask)
            checkSongEnd() // 추가: 노래가 끝났는지 확인하고 다음 노래로 이동할 수 있도록 함
        } else { // 일시 정지
            binding.mainMiniplayBtn.visibility = View.VISIBLE
            binding.mainPauseBtn.visibility = View.GONE
            if (mediaPlayer?.isPlaying == true) { // 재생 중이 아닐 때에 pause를 하면 에러가 나기 때문에 이를 방지
                mediaPlayer?.pause()
            }
            handler.removeCallbacks(updateSeekBarTask)
        }
    }

    // 다음 노래로 자동으로 넘어가게 함
    private fun checkSongEnd() {
        handler.postDelayed({
            if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                val currentProgress = mediaPlayer!!.currentPosition / 1000
                val totalDuration = songs[nowPos].playTime

                if (currentProgress + 1 >= totalDuration) {
                    // 현재 노래가 종료되었으므로 다음 노래로 이동
                    moveSong(+1)
                } else {
                    // 다음 체크를 위해 재귀 호출
                    checkSongEnd()
                    Log.d("progress", currentProgress.toString())
                    Log.d("progressplaytime", songs[nowPos].playTime.toString())

                }
            }
        }, 1000) // 1초마다 체크
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

        mediaPlayer?.release()
        mediaPlayer = null
        songs[nowPos].second = 0

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
        // SeekBar의 최대값을 노래의 총 재생 시간으로 설정
        binding.mainSeekbarSb.max = song.playTime * 1000

        val music = resources.getIdentifier(song.music, "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this, music)

        // 미디어 플레이어의 재생 위치를 저장 된 재생시간으로 설정
        mediaPlayer?.seekTo(songs[nowPos].second * 1000)

        // Seekbar progress 터치로 재생시간 옮기기
        binding.mainSeekbarSb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                    songs[nowPos].second = progress / 1000
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // 사용자가 터치하기 시작할 때 호출
                mediaPlayer?.pause()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // 사용자가 터치를 끝냈을 때 호출
                mediaPlayer?.start()
                checkSongEnd()
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
        Log.d("resume", "resume 발생")

        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val songId = sharedPreferences.getInt("songId", 0)
        songs[nowPos].second = sharedPreferences.getInt("songSecond", 0)

        nowPos = getPlayingSongPosition(songId)
        setMiniPlayer(songs[nowPos])
        setPlayerStatus(true) // 미디어 플레이어 다시 재생

    }

    // 현재 재생 정보를 저장해두는 역할
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


    // 사용자가 포커스를 잃으면 미디어 플레이어 해제
    override fun onStop() {
        super.onStop()
        Log.d("onStop", "onstop발생")

        songs[nowPos].second = binding.mainSeekbarSb.progress / 1000

        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("songSecond", songs[nowPos].second)
        editor.putInt("songId", songs[nowPos].id)

        editor.apply()
        mediaPlayer?.release() // 미디어 플레이어가 갖고 있던 리소스 해제
        mediaPlayer = null // 미디어 플레이어 해제
    }

    // songId로 position을 얻는 메서드
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
                200,
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
                182,
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
                151,
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
                154,
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
                170,
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
                152,
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
                215,
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
            .replace(R.id.main_frm, HomeFragment())
            .commitAllowingStateLoss()

        binding.mainBnv.setOnItemSelectedListener{ item ->
            when (item.itemId) {
                R.id.homeFragment -> { // HomeFragment 로 전환
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.savedSongFragment -> { // SavedSongFragment 로 전환
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LockerFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.postActivity -> { // PostActivity 실행
                    startActivity(Intent(this@MainActivity, PostActivity::class.java))
                    return@setOnItemSelectedListener true
                }

                R.id.blogFragment -> { // BlogFragment 로 전환
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, BlogFragment())
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