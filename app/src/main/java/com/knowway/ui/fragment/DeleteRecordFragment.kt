package com.knowway.ui.fragment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import com.knowway.R

class DeleteRecordFragment : DialogFragment() {

    private var onConfirmListener: (() -> Unit)? = null

    // Method to set the listener
    fun setOnConfirmListener(listener: () -> Unit) {
        onConfirmListener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.CustomDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.delete_record_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val confirmButtonContainer = view.findViewById<FrameLayout>(R.id.delete_confirm)
        val cancelButtonContainer = view.findViewById<FrameLayout>(R.id.delete_cancel)

        // Debug logging
        Log.d("DeleteRecordFragment", "confirmButtonContainer: $confirmButtonContainer")
        Log.d("DeleteRecordFragment", "cancelButtonContainer: $cancelButtonContainer")

        if (confirmButtonContainer == null || cancelButtonContainer == null) {
            Log.e("DeleteRecordFragment", "FrameLayouts are null. Check your layout file.")
            return
        }

        val confirmButtonView = LayoutInflater.from(requireContext())
            .inflate(R.layout.custom_confirm_button, confirmButtonContainer, false)
        val cancelButtonView = LayoutInflater.from(requireContext())
            .inflate(R.layout.custom_cancel_button, cancelButtonContainer, false)

        confirmButtonContainer.addView(confirmButtonView)
        cancelButtonContainer.addView(cancelButtonView)

        val confirmButton = confirmButtonView.findViewById<ImageButton>(R.id.confirmButton)
        confirmButton.setOnClickListener {
            onConfirmListener?.invoke() // Trigger the listener
            dismiss()
        }

        val cancelButton = cancelButtonView.findViewById<ImageButton>(R.id.cancelButton)
        cancelButton.setOnClickListener {
            dismiss()
        }
    }
}