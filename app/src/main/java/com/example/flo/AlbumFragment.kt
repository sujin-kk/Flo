package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.flo.databinding.FragmentAlbumBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class AlbumFragment : Fragment() {
    lateinit var binding : FragmentAlbumBinding
    private var gson: Gson = Gson()
    val tabLayoutTextArray = arrayListOf("수록곡", "상세정보", "영상")

    private var isLiked: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumBinding.inflate(inflater, container, false)

        // Home에서 넘어온 Album data 받아오기
        val albumData = arguments?.getString("album")
        val album = gson.fromJson(albumData, Album::class.java)

        // 현재 앨범의 좋아요 여부 확인
        isLiked = isLikedAlbum(album.id)

        // Home에서 넘어온 data를 반영
        setInit(album)
        setClickListeners(album)

        // 해당 앨범에 해당하는 수록곡들 가져옴
        val songs = getSongs(album.id)

        binding.albumBackIb.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, HomeFragment())
                .commitAllowingStateLoss()
        }

        binding.albumVp.adapter = AlbumViewpagerAdapter(this, songs)
        binding.albumVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        TabLayoutMediator(binding.albumTl, binding.albumVp){tab, position->
            tab.text = tabLayoutTextArray[position]
        }.attach()

        return binding.root
    }

    private fun setInit(album: Album) {
        binding.albumTitleTv.text = album.title.toString()
        binding.albumSingerTv.text = album.singer.toString()
        binding.albumIv.setImageResource(album.coverImg!!)

        if(isLiked) {
            binding.albumLikeIb.setImageResource(R.drawable.ic_my_like_on)
        }
        else {
            binding.albumLikeIb.setImageResource(R.drawable.ic_my_like_off)
        }
    }

    private fun setClickListeners(album: Album) {
        val userId: Int = getUserIdx(requireContext())

        binding.albumLikeIb.setOnClickListener {
            if(isLiked){
                binding.albumLikeIb.setImageResource(R.drawable.ic_my_like_off)
                disLikedAlbum(userId, album.id)
            }
            else {
                binding.albumLikeIb.setImageResource(R.drawable.ic_my_like_on)
                likeAlbum(userId, album.id)
            }
        }
    }

    private fun likeAlbum(userId: Int, albumId: Int) {
        val songDB = SongDatabase.getInstance(requireContext())!!
        val like = Like(userId, albumId)

        songDB.albumDao().likeAlbum(like)
    }

    private fun isLikedAlbum(albumId: Int) : Boolean {
        val songDB = SongDatabase.getInstance(requireContext())!!
        val userId = getUserIdx(requireContext())

        val likeId: Int? = songDB.albumDao().isLikeAlbum(userId, albumId)

        // likeId == null
        return likeId != null
    }

    private fun disLikedAlbum(userId: Int, albumId: Int) {
        val songDB = SongDatabase.getInstance(requireContext())!!
        songDB.albumDao().disLikeAlbum(userId, albumId)
    }

//    private fun getJwt(): Int {
//        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
//
//        return spf!!.getInt("jwt", 0)
//    }

    private fun getSongs(albumIdx: Int): ArrayList<Song>{
        val songDB = SongDatabase.getInstance(requireContext())!!

        val songs = songDB.songDao().getSongsInAlbum(albumIdx) as ArrayList

        return songs
    }

    private inner class AlbumViewpagerAdapter(fragment: Fragment, songs: ArrayList<Song>) : FragmentStateAdapter(fragment) {
        val songs : ArrayList<Song> = songs // 받아온 songs
        val listType : TypeToken<ArrayList<Song>> = object  : TypeToken<ArrayList<Song>>() {}

        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when(position) {
                0 -> AlbumTrackFragment().apply {
                    arguments = Bundle().apply {
                        val gson = Gson()
                        val songsJson = gson.toJson(songs, listType.type)
                        putString("songs", songsJson)
                    }
                }

                1 -> AlbumDetailFragment()
                2 -> AlbumVideoFragment()
                else -> AlbumTrackFragment()
            }
        }
    }

}