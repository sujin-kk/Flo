package com.example.flo.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.flo.*
import com.example.flo.Activity.MainActivity
import com.example.flo.Adapter.AlbumRVAdapter
import com.example.flo.Class.Album
import com.example.flo.Util.SongDatabase
import com.example.flo.databinding.FragmentHomeBinding
import com.google.gson.Gson


class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding

    private var albums = ArrayList<Album>()

    private lateinit var songDB: SongDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        songDB = SongDatabase.getInstance(requireContext())!!
        albums.addAll(songDB.albumDao().getAlbums())

        // 어댑터 설정
        binding.homeTodayAlbumRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val albumRVAdapter = AlbumRVAdapter(albums)

        // rv와 adapter 연결
        binding.homeTodayAlbumRv.adapter = albumRVAdapter


        albumRVAdapter.setMyItemClickListener(object: AlbumRVAdapter.MyItemClickListener {
            override fun onItemClick(album: Album) {
                changeAlbumFragment(album)

                // album 수록곡 중 첫 곡으로 mini player의 정보 변환
                val mainActivity = activity as MainActivity
                val songs = songDB.songDao().getSongsInAlbum(album.id)
                // val firstSong = songDB.songDao().getFirstSongInAlbum(album.id)
//                val firstSong = songs[0]
//                firstSong.second = 0
                songs[0].second = 0
                mainActivity.setMainMiniPlayerData(songs[0])

                // spf에 현재곡으로 set
//                val editor = requireActivity().getSharedPreferences("song", AppCompatActivity.MODE_PRIVATE).edit()
//                editor.putInt("songId", songs[0].id)
//                editor.apply()
//                Log.d("Home에서 set한 nowSongId", songs[0].id.toString())
            }
        })

        // Set Banner Fragment
        val bannerAdapter = BannerViewpagerAdapter(this)
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp2))

        binding.homeBannerVp.adapter = bannerAdapter
        binding.homeBannerVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        return binding.root
    }

    private fun changeAlbumFragment(album: Album) {
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, AlbumFragment().apply {
                arguments = Bundle().apply {
                    val gson = Gson()
                    val albumJson = gson.toJson(album)
                    putString("album", albumJson)
                }
            })
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    private inner class BannerViewpagerAdapter(fragment : Fragment) : FragmentStateAdapter(fragment) {

        private val fragmentList : ArrayList<Fragment> = ArrayList()

        override fun getItemCount(): Int = fragmentList.size

        override fun createFragment(position: Int): Fragment = fragmentList[position]

        fun addFragment(fragment: Fragment) {
            fragmentList.add(fragment)
            notifyItemInserted(fragmentList.size - 1) // adpater에선 새 프래그먼트가 추가되면 viewpager에 알려줘야 함
        }
    }


}