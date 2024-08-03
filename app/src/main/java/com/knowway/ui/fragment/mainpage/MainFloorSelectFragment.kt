package com.knowway.ui.fragment.mainpage

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import com.knowway.R

class MainFloorSelectFragment : DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.CustomDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_floor_select, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val confirmButtonContainer = view.findViewById<FrameLayout>(R.id.confirm_button_container)
        val confirmButtonView = LayoutInflater.from(requireContext())
            .inflate(R.layout.custom_confirm_button, confirmButtonContainer, false)

        // 레이아웃에 버튼 추가
        confirmButtonContainer.addView(confirmButtonView)

        // 버튼 클릭 리스너 설정
        val confirButton = confirmButtonView.findViewById<ImageButton>(R.id.confirmButton)
        confirButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(R.drawable.border_background)
        return dialog
    }
}