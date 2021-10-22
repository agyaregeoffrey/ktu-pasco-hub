package com.dev.gka.ktupascohub.activities.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.dev.gka.ktupascohub.databinding.FragmentPreviewBinding
import com.dev.gka.ktupascohub.utilities.Constants.LECTURER
import com.dev.gka.ktupascohub.utilities.Constants.LEVEL
import com.dev.gka.ktupascohub.utilities.Constants.QUESTION_URL
import com.dev.gka.ktupascohub.utilities.Constants.SEMESTER
import com.dev.gka.ktupascohub.utilities.Constants.SOLUTION_URL
import com.dev.gka.ktupascohub.utilities.Constants.TITLE
import com.dev.gka.ktupascohub.utilities.Constants.YEAR
import com.dev.gka.ktupascohub.utilities.UploadConfirmationListener


class Preview : DialogFragment() {

    private lateinit var binding: FragmentPreviewBinding
    private lateinit var listener: UploadConfirmationListener

    private var title: String? = null
    private var lecturer: String? = null
    private var level: String? = null
    private var year: String? = null
    private var semester: String? = null
    private var question: String? = null
    private var solution: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            title = bundle.getString(TITLE)
            lecturer = bundle.getString(LECTURER)
            level = bundle.getString(LEVEL)
            year = bundle.getString(YEAR)
            semester = bundle.getString(SEMESTER)
            question = bundle.getString(QUESTION_URL)
            solution = bundle.getString(SOLUTION_URL)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        binding = FragmentPreviewBinding.inflate(layoutInflater, null, false)

        return activity.let {
            val builder = AlertDialog.Builder(it)

            binding.editTextTitle.setText(title)
            binding.editTextLecturer.setText(lecturer)
            binding.editTextLevel.setText(level)
            binding.editTextYear.setText(year)
            binding.editTextSemester.setText(semester)
            binding.editTextQuestion.setText(question)
            binding.editTextSolution.setText(solution)

            binding.buttonCancel.setOnClickListener {
                dismiss()
            }
            binding.buttonUpload.setOnClickListener {
                listener.onConfirmed()
                dismiss()
            }
            builder.setView(binding.root)
            builder.create()
        } ?: throw IllegalStateException("Parent cannot be null")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = activity as UploadConfirmationListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                (context.toString() +
                        " must implement RequestConfirmationListener")
            )
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment Preview.
         */
        @JvmStatic
        fun newInstance(
            title: String,
            lecturer: String,
            year: String,
            level: String,
            sem: String,
            question: String,
            solution: String
        ) =
            Preview().apply {
                arguments = Bundle().apply {
                    val bundle = bundleOf(
                        TITLE to title,
                        LECTURER to lecturer,
                        LEVEL to level,
                        YEAR to year,
                        SEMESTER to sem,
                        QUESTION_URL to question,
                        SOLUTION_URL to solution
                    )
                    putAll(bundle)
                }
            }
    }
}