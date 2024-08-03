package com.knowway.ui.fragment.mainpage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.knowway.R
import pl.droidsonroids.gif.GifDrawable

class MainPersonFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_person, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val questionGif = GifDrawable(resources, R.drawable.question)
        val walkingPersonGif = GifDrawable(resources, R.drawable.walking_person)

        val questionImageButton = view.findViewById<ImageButton>(R.id.main_question)
        questionImageButton.setImageDrawable(questionGif)
        questionImageButton.setOnClickListener {
            val recordTipModal = MainRecordTipFragment()
            recordTipModal.show(parentFragmentManager, "Record Tip Modal")
        }

        val walkingPersonImageView = view.findViewById<ImageView>(R.id.main_walking_person)
        walkingPersonImageView.setImageDrawable(walkingPersonGif)
    }
}