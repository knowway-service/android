package com.knowway.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.knowway.R
import com.knowway.ui.activity.MypageActivity
import com.knowway.util.Utils

class SelectFooterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_select_footer, container, false)

        val myPageIcon = view.findViewById<ImageView>(R.id.ic_footer_mypage)
        myPageIcon.setOnClickListener {
            val intent = Intent(requireContext(), MypageActivity::class.java)
            startActivity(intent)
        }

        adjustFooterMargin(view)

        return view
    }

    private fun adjustFooterMargin(view: View) {
        val navigationBarHeight = Utils.getNavigationBarHeight(requireContext())
        val footerLayout = view.findViewById<RelativeLayout>(R.id.footer_layout)
        val layoutParams = footerLayout.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.bottomMargin = navigationBarHeight
        footerLayout.layoutParams = layoutParams
    }
}
