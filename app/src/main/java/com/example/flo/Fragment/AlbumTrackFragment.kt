package com.example.flo.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.flo.R
import com.example.flo.Class.Song
import com.example.flo.Activity.SongActivity
import com.example.flo.databinding.FragmentAlbumTrackBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


@Suppress("DEPRECATION")
class AlbumTrackFragment : Fragment() {
    lateinit var binding: FragmentAlbumTrackBinding
    private var  gson: Gson = Gson()

    var songList = arrayListOf<Song>(
        Song("라일락", "아이유 (IU)", true),
        Song("Flu", "아이유 (IU)", false),
        Song("Coin", "아이유 (IU)", true),
        Song("봄 안녕 봄", "아이유 (IU)", false),
        Song("Celebrity", "아이유 (IU)", false),
        Song("돌림노래 (Feat. DEAN)", "아이유 (IU)", false),
        Song("빈 컵 (Empty Cup)", "아이유 (IU)", false),
        Song("아이와 나의 바다", "아이유 (IU)", false),
        Song("어푸 (Ah puh)", "아이유 (IU)", false),
        Song("에필로그", "아이유 (IU)", false)
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentAlbumTrackBinding.inflate(inflater, container, false)

        var listType : TypeToken<ArrayList<Song>> = object  : TypeToken<ArrayList<Song>>() {}

        val songsData = arguments?.getString("songs")
        val songs = gson.fromJson(songsData, listType.type) as ArrayList<Song>
        Log.d("songs", songs.toString())

        val trackSongListView = binding.trackSongLv
        val adapter = MyListViewAdapter(requireContext(), songs)
        trackSongListView.adapter = adapter
        
        binding.trackMixTb.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                binding.trackMixTb.setBackgroundResource(R.drawable.btn_toggle_on)
            }
            else {
                binding.trackMixTb.setBackgroundResource(R.drawable.btn_toggle_off)
            }
        }

        return binding.root
    }

    private inner class MyListViewAdapter(val context: Context, val items: ArrayList<Song>) : BaseAdapter() {

        override fun getCount(): Int {
            return items.size
        }

        override fun getItem(position: Int): Any? {
            return items[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view : View = LayoutInflater.from(parent?.context).inflate(R.layout.row_track, parent, false)
            val numberTv = view.findViewById<TextView>(R.id.row_track_number_tv)
            val titleTv = view.findViewById<TextView>(R.id.row_track_title_tv)
            val singerTv = view.findViewById<TextView>(R.id.row_track_singer_tv)
            val playBtn = view.findViewById<ImageButton>(R.id.row_track_play_ib)
            val infoLayout = view.findViewById<LinearLayout>(R.id.track_info_layout)
            val titleCv = view.findViewById<CardView>(R.id.row_track_title_cv)

            val item = items[position]
            numberTv.text = "0"+(position+1).toString()
            titleTv.text = item.title
            singerTv.text = item.singer

            val song = Song(titleTv.text.toString(),
                singerTv.text.toString(), item.isTitle)

            Log.d("Log Song Test", song.title + song.singer)

            if(item.isTitle) {
                titleCv.visibility = View.VISIBLE
            }

            playBtn.setOnClickListener {
                val intent = Intent(requireContext(), SongActivity::class.java)
                intent.putExtra("title", song.title)
                intent.putExtra("singer", song.singer)
                startActivity(intent)
            }

            infoLayout.setOnClickListener {
                Toast.makeText(requireContext(), item.title, Toast.LENGTH_SHORT).show()
            }

            return view
        }

    }



}