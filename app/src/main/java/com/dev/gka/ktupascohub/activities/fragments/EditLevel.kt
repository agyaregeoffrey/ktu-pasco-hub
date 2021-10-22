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
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.dev.gka.ktupascohub.R
import com.dev.gka.ktupascohub.databinding.FragmentEditLevelBinding
import com.dev.gka.ktupascohub.utilities.RequestChangeLevelListener
import com.dev.gka.ktupascohub.utilities.UploadConfirmationListener

class EditLevel : DialogFragment() {

    private lateinit var binding: FragmentEditLevelBinding
    private lateinit var listener: RequestChangeLevelListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentEditLevelBinding.inflate(layoutInflater, null, false)

        val levels: MutableList<String> = mutableListOf("300", "200", "100")
        val levelAdapter = ArrayAdapter(requireContext(), R.layout.drop_down_list_item, levels)
        binding.levelDropdown.setAdapter(levelAdapter)

        return activity.let {
            val builder = AlertDialog.Builder(it)
            binding.buttonCancel.setOnClickListener {
                dismiss()
            }
            binding.buttonSave.setOnClickListener {
                if (binding.levelSelection.editText?.text?.isEmpty() == true) {
                    binding.levelSelection.error = getString(R.string.field_cannot_be_empty)
                } else {
                    val newLevel = binding.levelSelection.editText?.text?.toString()
                    listener.onChangeRequest(newLevel!!)
                    dismiss()
                }
            }
            builder.setView(binding.root)
            builder.create()
        }?: throw IllegalStateException("Parent cannot be null")
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
            listener = activity as RequestChangeLevelListener
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
         * @return A new instance of fragment EditLevel.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            EditLevel().apply {
                arguments = Bundle().apply {

                }
            }
    }
}