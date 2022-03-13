package com.example.flo.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.flo.Activity.LoginActivity
import com.example.flo.Activity.MainActivity
import com.example.flo.databinding.FragmentLockerBinding
import com.example.flo.Util.getUserIdx
import com.google.android.material.tabs.TabLayoutMediator


class LockerFragment : Fragment() {
    lateinit var binding: FragmentLockerBinding
    val tabLayoutTextArray = arrayListOf("저장한 곡", "음악파일", "저장앨범")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLockerBinding.inflate(inflater, container, false)

        binding.lockerVp.adapter = LockerViewpagerAdapter(this)
        binding.lockerVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        TabLayoutMediator(binding.lockerTl, binding.lockerVp){tab, position->
            tab.text = tabLayoutTextArray[position]
        }.attach()

        binding.lockerLoginTv.setOnClickListener {
            startActivity(Intent(activity, LoginActivity::class.java))
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        initView()
    }

    private fun initView() {
        val jwt = getUserIdx(requireContext())

        if(jwt == 0) {
            binding.lockerLoginTv.text = "로그인"
            binding.lockerLoginTv.setOnClickListener {
                startActivity(Intent(activity, LoginActivity::class.java))
            }
        }
        else {
            binding.lockerLoginTv.text = "로그아웃"
            binding.lockerLoginTv.setOnClickListener{
                logout()
                startActivity(Intent(activity, MainActivity::class.java))
            }
        }

    }

    private fun logout() {
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        val editor = spf!!.edit()

        // editor.remove("jwt")
        editor.remove("userIdx")
        editor.apply()
    }

//    private fun getJwt(): Int {
//        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
//
//        return spf!!.getInt("jwt", 0)
//    }

    private inner class LockerViewpagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 3


        override fun createFragment(position: Int): Fragment {
            return when(position) {
                0 -> LockerSaveFragment()
                1 -> LockerFileFragment()
                else -> LockerSavedAlbumFragment()
            }
        }
    }


}