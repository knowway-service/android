package com.knowway.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.knowway.R
import com.knowway.ui.activity.DepartmentStoreSearchActivity
import com.knowway.util.Utils

class MapFooterFragment : Fragment() {

    private var isToggled = false
    private var listener: OnToggleChangeListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map_footer, container, false)

        val toggleButton = view.findViewById<RelativeLayout>(R.id.toggleButton)
        val toggleCircle = view.findViewById<ImageView>(R.id.toggleCircle)
        val chatIcon = view.findViewById<ImageView>(R.id.ic_footer_chat)
        val myPageIcon = view.findViewById<ImageView>(R.id.ic_footer_mypage)

        toggleButton.setOnClickListener {
            toggleState(toggleCircle, toggleButton)
        }

        chatIcon.setOnClickListener {
            val intent = Intent(requireContext(), DepartmentStoreSearchActivity::class.java).apply {
                putExtra("key", 1)
            }
            startActivity(intent)
        }

        myPageIcon.setOnClickListener {
            val intent = Intent(requireContext(), MypageActivity::class.java)
            startActivity(intent)
        }

        adjustFooterMargin(view)

        return view
    }

    private fun toggleState(circle: ImageView, button: RelativeLayout) {
        val fromX = if (isToggled) 35f else 0f
        val toX = if (isToggled) 0f else 35f
        val animation = TranslateAnimation(fromX, toX, 0f, 0f)
        animation.duration = 300
        animation.fillAfter = true

        circle.startAnimation(animation)

        button.setBackgroundResource(if (isToggled) R.drawable.toggle_off_background else R.drawable.toggle_on_background)
        circle.setBackgroundResource(if (isToggled) R.drawable.toggle_off_circle else R.drawable.toggle_on_circle)

        isToggled = !isToggled
        listener?.onToggleChanged(isToggled)
    }

    fun setOnToggleChangeListener(listener: OnToggleChangeListener) {
        this.listener = listener
    }

    private fun adjustFooterMargin(view: View) {
        val navigationBarHeight = Utils.getNavigationBarHeight(requireContext())
        val footerLayout = view.findViewById<RelativeLayout>(R.id.footer_layout)
        val layoutParams = footerLayout.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.bottomMargin = navigationBarHeight
        footerLayout.layoutParams = layoutParams
    }
}
