package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.databinding.FragmentLockerSavedAlbumBinding


class LockerSavedAlbumFragment : Fragment() {

    lateinit var binding: FragmentLockerSavedAlbumBinding
    lateinit var albumDB: SongDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLockerSavedAlbumBinding.inflate(inflater, container, false)

        albumDB = SongDatabase.getInstance(requireContext())!!

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        initRecyclerview()
    }

    private fun initRecyclerview() {
        binding.savedAlbumRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val savedAlbumRVAdapter = LockerSavedAlbumRVAdapter()

        savedAlbumRVAdapter.setMyItemClickListener(object: LockerSavedAlbumRVAdapter.MyItemClickListener{

            override fun onRemoveSong(songId: Int) {
                albumDB.albumDao().getLikedAlbums(getUserIdx(requireContext()))
            }
        })

        binding.savedAlbumRv.adapter = savedAlbumRVAdapter
        savedAlbumRVAdapter.addAlbums(albumDB.albumDao().getLikedAlbums(getUserIdx(requireContext())) as ArrayList)
    }

    private fun getJwt(): Int {
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)

        return spf!!.getInt("jwt", 0)
    }


}