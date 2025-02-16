package com.knowway.ui.fragment.mainpage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.knowway.R
import com.knowway.databinding.FragmentMainPersonBinding
import pl.droidsonroids.gif.GifDrawable

/**
 * 메인페이지-메인화면(사람이 걷고있는 화면) 프래그먼트
 *
 * @author 김진규
 * @since 2024.08.02
 *
 * @version 1.0
 */
class MainPersonFragment : Fragment() {
    private var _binding: FragmentMainPersonBinding? = null
    private val binding get() = _binding!!
    private var audioFileUrl: String? = null

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

        binding.mainQuestion.setImageDrawable(GifDrawable(resources, R.drawable.question))
        binding.mainQuestion.setOnClickListener {
            MainRecordTipFragment().apply {
                audioFileUrl?.let { it1 -> setAudioFileUrl(it1) }
            }.show(parentFragmentManager, "Record Tip Modal")
        }

        binding.mainWalkingPerson.setImageDrawable(GifDrawable(resources, R.drawable.walking_person))
        hideQuestionButton()
    }

    fun showQuestionButton(recordPath: String) {
        binding.mainQuestion.visibility = View.VISIBLE
        audioFileUrl = recordPath
    }

    fun hideQuestionButton() {
        binding.mainQuestion.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}