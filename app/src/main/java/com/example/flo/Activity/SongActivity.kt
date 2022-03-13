package com.example.flo.Activity

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.Util.MyToast
import com.example.flo.R
import com.example.flo.Class.Song
import com.example.flo.Util.SongDatabase
import com.example.flo.databinding.ActivitySongBinding
import com.google.gson.Gson

@Suppress("DEPRECATION")
class SongActivity : AppCompatActivity() {
    lateinit var binding: ActivitySongBinding

    private var song: Song = Song()
    private lateinit var player: Player
    private val handler = Handler(Looper.getMainLooper())

    private var mediaPlayer: MediaPlayer? = null

    private var gson : Gson = Gson()

    private var songs = ArrayList<Song>()
    private var nowPos = 0
    private lateinit var songDB: SongDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initPlayList()
        initSong()
        initClickListener()

        // player.interrupt() // thread 종료

    }

    private fun initPlayList() {
        songDB = SongDatabase.getInstance(this)!!
        songs.addAll(songDB.songDao().getSongs()) // table의 모든 songs 가져오기
    }

    private fun initSong() {
//        if(intent.hasExtra("title") && intent.hasExtra("singer")
//            && intent.hasExtra("playTime") && intent.hasExtra("isPlaying")
//            && intent.hasExtra("music") && intent.hasExtra("second")) {
//
//            song.title = intent.getStringExtra("title")!!
//            song.singer = intent.getStringExtra("singer")!!
//            song.playTime = intent.getIntExtra("playTime", 0)
//            song.isPlaying = intent.getBooleanExtra("isPlaying", false)
//            song.second = intent.getIntExtra("second", 0)
//            song.music = intent.getStringExtra("music")!!
//
//            binding.songAlbumIv.setImageResource(intent.getIntExtra("coverImg", R.drawable.img_album_exp))
//
//            val music = resources.getIdentifier(song.music, "raw", this.packageName) // 리소스 이름으로 받아오기
//
//            binding.songPlayerSb.progress = song.second / song.playTime
//            binding.songStartTimeTv.text = String.format("%02d:%02d", song.second/1000/60, song.second/1000%60)
//            binding.songEndTimeTv.text = String.format("%02d:%02d", song.playTime/60, song.playTime%60)
//            binding.songTitleTv.text = song.title
//            binding.songSingerTv.text = song.singer
//            setPlayerStatus(song.isPlaying)
//            mediaPlayer = MediaPlayer.create(this, music)
//        }

        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 0)

        nowPos = getPlayingSongPosition(songId)

        Log.d("now Song ID",songs[nowPos].id.toString())

        startPlayer()
        // song = songs[nowPos]
        setPlayer(songs[nowPos])
    }

    private fun initClickListener() {
        binding.songDownIb.setOnClickListener {
            finish()
        }

        binding.songPlayIb.setOnClickListener {
            setPlayerStatus(true)
            player.isPlaying = true
            song.isPlaying = true
            mediaPlayer?.seekTo(player.millis)
            mediaPlayer?.start()
        }

        binding.songPauseIb.setOnClickListener {
            setPlayerStatus(false)
            player.isPlaying = false
            song.isPlaying = false
            mediaPlayer?.seekTo(player.millis)
            mediaPlayer?.pause()
        }

        binding.songSkipPreIb.setOnClickListener {
            moveSong(-1)
        }

        binding.songSkipNextIb.setOnClickListener {
            moveSong(+1)
        }

        binding.songLikeIb.setOnClickListener {
            setLike(songs[nowPos].isLike)
        }

        // 시크바 리스너 부착
        binding.songPlayerSb.setOnSeekBarChangeListener(SeekBarListener())
    }

    private fun updateNowSong(song: Song) {
        val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
        editor.putInt("songId", song.id)
        editor.apply()
        Log.d("now", song.id.toString())
    }

    private fun moveSong(direct: Int){

        if (nowPos + direct < 0){
            Toast.makeText(this,"first song",Toast.LENGTH_SHORT).show()
            return
        }
        if (nowPos + direct >= songs.size){
            Toast.makeText(this,"last song",Toast.LENGTH_SHORT).show()
            return
        }

        nowPos += direct

        player.interrupt()
        startPlayer()

        mediaPlayer?.release() // 미디어플레이어가 가지고 있던 리소스를 해방
        mediaPlayer = null // 미디어플레이어 해제

        // song = songs[nowPos]
        updateNowSong(songs[nowPos])
        setPlayer(songs[nowPos])
    }

    private fun getPlayingSongPosition(songId: Int): Int{
        for (i in 0 until songs.size){
            if (songs[i].id == songId){
                return i
            }
        }
        return 0
    }

    private fun startPlayer() {
        player = Player(songs[nowPos].playTime, songs[nowPos].second, songs[nowPos].isPlaying)
        player.start()
    }

    private fun setPlayer(song: Song) {
        val music = resources.getIdentifier(song.music, "raw", this.packageName)

        binding.songTitleTv.text = song.title
        binding.songSingerTv.text = song.singer
        // player.millis = song.second * 1000
        binding.songStartTimeTv.text =
            String.format("%02d:%02d", song.second / 1000 / 60, song.second / 1000 % 60)

        binding.songEndTimeTv.text =
            String.format("%02d:%02d", song.playTime / 60, song.playTime % 60)

        binding.songAlbumIv.setImageResource(song.coverImg!!)

        binding.songPlayerSb.progress = song.second * 1000 / song.playTime // 1000곱해?

        setPlayerStatus(song.isPlaying)

        if (song.isLike) {
            binding.songLikeIb.setImageResource(R.drawable.ic_my_like_on)
        } else {
            binding.songLikeIb.setImageResource(R.drawable.ic_my_like_off)
        }

        mediaPlayer = MediaPlayer.create(this, music)
    }

    private fun setLike(isLike: Boolean){
        songs[nowPos].isLike = !isLike
        songDB.songDao().updateIsLikeById(!isLike, songs[nowPos].id)

        if (isLike){ // 좋아요 -> 안좋아요
            binding.songLikeIb.setImageResource(R.drawable.ic_my_like_off)
            MyToast.createToast(this, "좋아요 한 곡이 취소되었습니다.")?.show()
        }
        else{ // 안좋아요 -> 좋아요
            binding.songLikeIb.setImageResource(R.drawable.ic_my_like_on)
            MyToast.createToast(this, "좋아요 한 곡에 담겼습니다.")?.show()
        }
    }

    fun setPlayerStatus(isPlaying : Boolean) {
        player.isPlaying = isPlaying
        songs[nowPos].isPlaying = isPlaying

        if(isPlaying) {
            binding.songPlayIb.visibility = View.GONE
            binding.songPauseIb.visibility = View.VISIBLE

            mediaPlayer?.seekTo(player.millis)
            mediaPlayer?.start()
        }
        else {
            binding.songPlayIb.visibility = View.VISIBLE
            binding.songPauseIb.visibility = View.GONE

            mediaPlayer?.seekTo(player.millis)
            mediaPlayer?.pause()
        }
    }

    inner class SeekBarListener : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                player.millis = progress * song.playTime
                binding.songStartTimeTv.text = String.format("%02d:%02d", player.millis/1000/60, player.millis/1000%60)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                mediaPlayer?.seekTo(player.millis)
            }
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
                            binding.songPlayerSb.progress = millis / playTime
                            binding.songStartTimeTv.text = String.format("%02d:%02d", millis/1000/60, millis/1000%60)
                        }
                    }
                }
            }catch (e : InterruptedException) {
                Log.d("interrupt", "쓰레드가 종료되었습니다.")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("song", "onStart")
//        val spf = getSharedPreferences("song", MODE_PRIVATE)
//        val songId = spf.getInt("songId", 0)

//        val jsonSong = spf.getString("song", null) // Json format
//        // json -> 객체
//        if(jsonSong == null) {
//            song = Song(binding.songTitleTv.text.toString(),
//                binding.songSingerTv.text.toString(), false, 215, false, "music_lilac", 0)
//        }
//        else {
//            song = songDB.songDao().getSong(songId)
//            song = gson.fromJson(jsonSong, Song::class.java)
//        }

//        song = songDB.songDao().getSong(songId)
//        initSong()
    }

    override fun onResume() {
        super.onResume()
        Log.d("song", "onResume")
//        val spf = getSharedPreferences("song", MODE_PRIVATE)
//        val songId = spf.getInt("songId", 0)
//
//        song = songDB.songDao().getSong(songId)


        // initSong()
    }

    override fun onPause() {
        super.onPause()
        Log.d("song", "onPause")
//        mediaPlayer?.pause() // 미디어 플레이어 중지
//        player.isPlaying = false // 스레드 중지
//        songs[nowPos].second = (songs[nowPos].playTime * binding.songPlayerSb.progress) / 1000
//        songs[nowPos].isPlaying = false
//        setPlayerStatus(false) // 정지상태일 때의 이미지로 전환
//
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("songId", songs[nowPos].id)
        editor.apply()

        // song.second = player.millis / 1000
        // songDB.songDao().update(songs[nowPos])

//
//        // Gson : song data 객체를 Json으로 변환
//        val json = gson.toJson(song)
//        editor.putString("song", json)
//
//        editor.apply()

    }

    override fun onDestroy() {
        super.onDestroy()
        player.interrupt() // 쓰레드 해제
        mediaPlayer?.release() // mediaPlayer가 갖고 있던 리소스 해제
        mediaPlayer = null // mediaPlayer 해제
    }
}