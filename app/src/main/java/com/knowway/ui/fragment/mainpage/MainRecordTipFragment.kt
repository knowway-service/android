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

class MainRecordTipFragment : DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.CustomDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_record_tip, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val confirmButtonContainer = view.findViewById<FrameLayout>(R.id.record_confirm)
        val cancelButtonContainer = view.findViewById<FrameLayout>(R.id.record_cancel)

        val confirmButtonView = LayoutInflater.from(requireContext())
            .inflate(R.layout.custom_confirm_button, confirmButtonContainer, false)
        val cancelButtonView = LayoutInflater.from(requireContext())
            .inflate(R.layout.custom_cancel_button, cancelButtonContainer, false)

        confirmButtonContainer.addView(confirmButtonView)
        cancelButtonContainer.addView(cancelButtonView)

        val confirmButton = confirmButtonView.findViewById<ImageButton>(R.id.confirmButton)
        confirmButton.setOnClickListener {
            // 확인 버튼 클릭시 닫기
            dismiss()
        }

        val cancelButton = cancelButtonView.findViewById<ImageButton>(R.id.cancelButton)
        cancelButton.setOnClickListener {
            // 취소 버튼 클릭시 닫기
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(R.drawable.border_background)
        return dialog
    }
}