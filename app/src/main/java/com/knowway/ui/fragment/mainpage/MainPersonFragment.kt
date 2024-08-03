package com.knowway.ui.fragment.mainpage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.knowway.R
import com.knowway.databinding.FragmentMainPersonBinding
import pl.droidsonroids.gif.GifDrawable

class MainPersonFragment : Fragment() {
    private var _binding: FragmentMainPersonBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainPersonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val questionGif = GifDrawable(resources, R.drawable.question)
        binding.mainQuestion.setImageDrawable(questionGif)
        binding.mainQuestion.setOnClickListener {
            val recordTipModal = MainRecordTipFragment()
            recordTipModal.show(parentFragmentManager, "Record Tip Modal")
        }

        val walkingPersonGif = GifDrawable(resources, R.drawable.walking_person)
        binding.mainWalkingPerson.setImageDrawable(walkingPersonGif)

        hideQuestionButton()
    }

    fun showQuestionButton() {
        binding.mainQuestion.visibility = View.VISIBLE
    }

    fun hideQuestionButton() {
        binding.mainQuestion.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}