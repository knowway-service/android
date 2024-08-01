package com.knowway.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.knowway.R
import com.knowway.ui.activity.DepartmentStoreSearchActivity
import com.knowway.ui.activity.MypageActivity

class ChatFooterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat_footer, container, false)

        val mapIcon = view.findViewById<ImageView>(R.id.ic_footer_map)
        val myPageIcon = view.findViewById<ImageView>(R.id.ic_footer_mypage)

        mapIcon.setOnClickListener {
            val intent = Intent(requireContext(), DepartmentStoreSearchActivity::class.java)
            startActivity(intent)
        }

        myPageIcon.setOnClickListener {
            val intent = Intent(requireContext(), MypageActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}
