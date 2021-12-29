package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.databinding.FragmentLockerSaveBinding

class LockerSaveFragment : Fragment() {
    lateinit var binding: FragmentLockerSaveBinding
    private var albumDatas = ArrayList<Album>();

    lateinit var songDB: SongDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLockerSaveBinding.inflate(inflater, container, false)

        songDB = SongDatabase.getInstance(requireContext())!!

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        initRecyclerview()
    }

    private fun initRecyclerview() {
        binding.saveRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val saveRVAdapter = LockerSaveRVAdapter()

        saveRVAdapter.setMyItemClickListener(object: LockerSaveRVAdapter.MyItemClickListener{

            override fun onRemoveSong(songId: Int) {
                songDB.songDao().updateIsLikeById(false, songId)
            }
        })

        binding.saveRv.adapter = saveRVAdapter

        saveRVAdapter.addSongs(songDB.songDao().getLikedSongs(true) as ArrayList)
    }

}