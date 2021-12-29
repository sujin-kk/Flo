package com.example.flo

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.databinding.ActivityMainBinding
import com.google.gson.Gson


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    private lateinit var player: Player
    private var mediaPlayer: MediaPlayer? = null

    private var gson:Gson = Gson()
    private var song:Song = Song()

    private var nowPos = 0
    private var songs = ArrayList<Song>()

    private var curAlbumImg : Int = 0

    private lateinit var songDB: SongDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_BottomNaviTemplate) // main 화면 setting 전에 원래 테마로 되돌림
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        songDB = SongDatabase.getInstance(this)!!

        initNavigation()
        inputDummySongs()
        inputDummyAlbums()
        initPlayList()
        initClickListener()

        // song & mini player 초기화
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 0)
        nowPos = getPlayingSongPosition(songId)

        Log.d("now Song ID",songs[nowPos].id.toString())

        startPlayer()
        setMiniPlayer(songs[nowPos])
    }

    private fun updateNowSong(song: Song) {
        val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
        editor.putInt("songId", song.id)
        editor.apply()
        Log.d("nowSongId", song.id.toString())
    }

    private fun getPlayingSongPosition(songId: Int): Int{
        for (i in 0 until songs.size){
            if (songs[i].id == songId){
                return i
            }
        }
        return 0
    }

    private fun moveSong(direct: Int){

        if (nowPos + direct < 0){
            Toast.makeText(this,"first song", Toast.LENGTH_SHORT).show()
            return
        }
        if (nowPos + direct >= songs.size){
            Toast.makeText(this,"last song", Toast.LENGTH_SHORT).show()
            return
        }

        nowPos += direct

        player.interrupt()
        startPlayer()

        mediaPlayer?.release() // 미디어플레이어가 가지고 있던 리소스를 해방
        mediaPlayer = null // 미디어플레이어 해제

        // song = songs[nowPos]
        updateNowSong(songs[nowPos])
        setMiniPlayer(songs[nowPos])
    }

    private fun startPlayer() {
        player = Player(songs[nowPos].playTime, songs[nowPos].second, songs[nowPos].isPlaying)
        player.start()
    }

    private fun setMiniPlayer(song : Song) {
        binding.mainMiniplayerTitleTv.text = song.title
        binding.mainMiniplayerSingerTv.text = song.singer
        binding.mainProgressSb.progress = song.second * 1000 / song.playTime

        val music = resources.getIdentifier(song.music, "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this, music)

        if(song.isPlaying) {
            setPlayerStatus(true)
        }
        else {
            setPlayerStatus(false)
        }
        updateNowSong(song)
    }

    fun setMainMiniPlayerData(song: Song) {
        binding.mainMiniplayerTitleTv.text = song.title
        binding.mainMiniplayerSingerTv.text = song.singer
        binding.mainProgressSb.progress = song.second * 1000 / song.playTime
        curAlbumImg = song.coverImg!!
        updateNowSong(song)
    }

    fun setPlayerStatus(isPlaying : Boolean) {
        if(isPlaying) {
            binding.mainPauseBtn.visibility = View.VISIBLE
            binding.mainMiniplayerBtn.visibility = View.GONE
        }
        else {
            binding.mainPauseBtn.visibility = View.GONE
            binding.mainMiniplayerBtn.visibility = View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("main", "onStart")

        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 0)

        val songDB = SongDatabase.getInstance(this)!!
        song = if (songId == 0) {
            songDB.songDao().getSong(1) // lilac 가져옴
        } else {
            songDB.songDao().getSong(songId)
        }

        Log.d("song ID", song.id.toString())
        setMiniPlayer(song)

//        val jsonSong = sharedPreferences.getString("song", null) // Json format
//        // json -> 객체
//        song = if(jsonSong == null) {
//            Song(binding.mainMiniplayerTitleTv.text.toString(),
//                binding.mainMiniplayerSingerTv.text.toString(), false, 215, false, "music_lilac", 0)
//        }
//        else {
//            gson.fromJson(jsonSong, Song::class.java)
//        }
//        setMiniPlayer(song)
    }

    override fun onResume() {
        super.onResume()
        Log.d("main", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("main", "onPause")
        mediaPlayer?.pause() // 미디어 플레이어 중지
        player.isPlaying = false // 스레드 중지
        songs[nowPos].second = (songs[nowPos].playTime * binding.mainProgressSb.progress) / 1000
        songs[nowPos].isPlaying = false
        setPlayerStatus(false) // 정지상태 이미지로 변환
        // player.millis = song.second * 1000

        // sharedPreferences
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit() // sharePreferences 조작할 때 사용

        editor.putInt("songId", songs[nowPos].id)
        editor.apply()

//        editor.putInt("songId", song.id)
//        editor.apply()

        // song.second = player.millis / 1000
        // songDB.songDao().update(songs[nowPos])


        // 원래
         // Gson : song data 객체를 Json으로 변환
//        val json = gson.toJson(song)
//        editor.putString("song", json)

    }

    override fun onDestroy() {
        super.onDestroy()
        player.interrupt() // 쓰레드 해제
        mediaPlayer?.release() // mediaPlayer가 갖고 있던 리소스 해제
        mediaPlayer = null // mediaPlayer 해제
    }

    private fun initPlayList() {
        songDB = SongDatabase.getInstance(this)!!
        songs.addAll(songDB.songDao().getSongs()) // table의 모든 songs 가져오기
    }

    private fun initClickListener() {
        binding.mainPlayerInfoLayout.setOnClickListener {
            Log.d("nowSongId", songs[nowPos].id.toString())

//            song.second = player.millis / 1000
//            songDB.songDao().update(song)

            val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
            editor.putInt("songId", songs[nowPos].id)
            editor.apply()

            val intent = Intent(this@MainActivity, SongActivity::class.java)
            startActivity(intent)
        }

        binding.mainMiniplayerBtn.setOnClickListener {
            setPlayerStatus(true)
            player.isPlaying = true
            song.isPlaying = true
            mediaPlayer?.seekTo(player.millis)
            mediaPlayer?.start()
        }

        binding.mainPauseBtn.setOnClickListener {
            setPlayerStatus(false)
            player.isPlaying = false
            song.isPlaying = false
            mediaPlayer?.pause()
        }

        binding.mainPrevIv.setOnClickListener {
            moveSong(-1)
        }

        binding.mainNextIv.setOnClickListener {
            moveSong(+1)
        }
    }

    private fun initNavigation() {
        supportFragmentManager.beginTransaction().replace(R.id.main_frm, HomeFragment())
            .commitAllowingStateLoss()

        binding.mainBnv.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.homeFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.lookFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LookFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.searchFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, SearchFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.lockerFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LockerFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

            }
            false
        }
    }

    private fun inputDummySongs() {
        val songDB = SongDatabase.getInstance(this)!!
        val songs = songDB.songDao().getSongs()

        if(songs.isNotEmpty()) return

        songDB.songDao().insert(
            Song(
                "Lilac",
                "아이유 (IU)",
                true,
                200,
                false,
                "music_lilac",
                0,
                R.drawable.img_album_exp2,
                false,
                1
            )
        )

        songDB.songDao().insert(
            Song(
                "Flu",
                "아이유 (IU)",
                false,
                200,
                false,
                "music_lilac",
                0,
                R.drawable.img_album_exp2,
                false,
                1
            )
        )

        songDB.songDao().insert(
            Song(
                "Coin",
                "아이유 (IU)",
                true,
                200,
                false,
                "music_lilac",
                0,
                R.drawable.img_album_exp2,
                false,
                1
            )
        )

        songDB.songDao().insert(
            Song(
                "봄 안녕 봄",
                "아이유 (IU)",
                false,
                200,
                false,
                "music_lilac",
                0,
                R.drawable.img_album_exp2,
                false,
                1
            )
        )

        songDB.songDao().insert(
            Song(
                "Celebrity",
                "아이유 (IU)",
                false,
                200,
                false,
                "music_lilac",
                0,
                R.drawable.img_album_exp2,
                false,
                1
            )
        )

        songDB.songDao().insert(
            Song(
                "돌림노래 (Feat. DEAN)",
                "아이유 (IU)",
                false,
                200,
                false,
                "music_lilac",
                0,
                R.drawable.img_album_exp2,
                false,
                1
            )
        )

        songDB.songDao().insert(
            Song(
                "빈 컵 (Empty Cup)",
                "아이유 (IU)",
                false,
                200,
                false,
                "music_lilac",
                0,
                R.drawable.img_album_exp2,
                false,
                1
            )
        )

        songDB.songDao().insert(
            Song(
                "아이와 나의 바다",
                "아이유 (IU)",
                false,
                200,
                false,
                "music_lilac",
                0,
                R.drawable.img_album_exp2,
                false,
                1
            )
        )

        songDB.songDao().insert(
            Song(
                "Butter",
                "방탄소년단 (BTS)",
                true,
                190,
                false,
                "music_lilac",
                0,
                R.drawable.img_album_exp,
                false,
                2
            )
        )

        songDB.songDao().insert(
            Song(
                "Butter (Hotter Remix)",
                "방탄소년단 (BTS)",
                false,
                190,
                false,
                "music_lilac",
                0,
                R.drawable.img_album_exp,
                false,
                2
            )
        )

        songDB.songDao().insert(
            Song(
                "Butter (Sweeter Remix)",
                "방탄소년단 (BTS)",
                false,
                190,
                false,
                "music_lilac",
                0,
                R.drawable.img_album_exp,
                false,
                2
            )
        )

        songDB.songDao().insert(
            Song(
                "Next Level",
                "에스파 (AESPA)",
                true,
                210,
                false,
                "music_lilac",
                0,
                R.drawable.img_album_exp5,
                false,
                3
            )
        )

        songDB.songDao().insert(
            Song(
                "Boy with Luv",
                "방탄소년단 (BTS)",
                true,
                230,
                false,
                "music_lilac",
                0,
                R.drawable.img_album_exp3,
                false,
                4
            )
        )

        songDB.songDao().insert(
            Song(
                "소우주 (Mikrokosmos)",
                "방탄소년단 (BTS)",
                false,
                230,
                false,
                "music_lilac",
                0,
                R.drawable.img_album_exp3,
                false,
                4
            )
        )

        songDB.songDao().insert(
            Song(
                "Make It Right",
                "방탄소년단 (BTS)",
                false,
                230,
                false,
                "music_lilac",
                0,
                R.drawable.img_album_exp3,
                false,
                4
            )
        )

        songDB.songDao().insert(
            Song(
                "City",
                "오왼 (Owen)",
                true,
                200,
                false,
                "music_lilac",
                0,
                R.drawable.img_album_exp6,
                false,
                5
            )
        )

        songDB.songDao().insert(
            Song(
                "고해",
                "오왼 (Owen)",
                true,
                210,
                false,
                "music_lilac",
                0,
                R.drawable.img_album_exp4,
                false,
                6
            )
        )

        songDB.songDao().insert(
            Song(
                "위해",
                "오왼 (Owen)",
                false,
                210,
                false,
                "music_lilac",
                0,
                R.drawable.img_album_exp4,
                false,
                6
            )
        )

        val _songs = songDB.songDao().getSongs()
        Log.d("DB DATA", _songs.toString())

    }

    private fun inputDummyAlbums() {
        val songDB = SongDatabase.getInstance(this)!!
        val albums = songDB.albumDao().getAlbums()

        if (albums.isNotEmpty()) return

        songDB.albumDao().insert(
            Album(
                1,
                "IU 5th Album 'LILAC'", "아이유 (IU)", R.drawable.img_album_exp2
            )
        )

        songDB.albumDao().insert(
            Album(
                2,
                "Butter", "방탄소년단 (BTS)", R.drawable.img_album_exp
            )
        )

        songDB.albumDao().insert(
            Album(
                3,
                "Next Level", "에스파 (AESPA)", R.drawable.img_album_exp5
            )
        )

        songDB.albumDao().insert(
            Album(
                4,
                "MAP OF THE SOUL : PERSONA", "방탄소년단 (BTS)", R.drawable.img_album_exp3
            )
        )

        songDB.albumDao().insert(
            Album(
                5,
                "City", "오왼 (Owen)", R.drawable.img_album_exp6
            )
        )

        songDB.albumDao().insert(
            Album(
                6,
                "너에게", "오왼 (Owen)", R.drawable.img_album_exp4
            )
        )
    }

    inner class Player(private val playTime:Int, private var currentMillis:Int, var isPlaying: Boolean) : Thread() {
        // private var second = 0 // 진행 초
        var millis = currentMillis

        override fun run() {
            try {
                while(true) {
                    if(millis/1000 >= playTime) {
                        break
                    }

                    if(isPlaying) { // play중에만 seekbar 백그라운드 실행
                        sleep(1)
                        millis++
                        runOnUiThread { // handler.post
                            binding.mainProgressSb.progress = millis/playTime
                        }
                    }
                }
            }catch (e : InterruptedException) {
                Log.d("interrupt", "쓰레드가 종료되었습니다.")
            }
        }
    }


}

