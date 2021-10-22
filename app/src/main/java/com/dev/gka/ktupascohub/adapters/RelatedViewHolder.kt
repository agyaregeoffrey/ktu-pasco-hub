package com.dev.gka.ktupascohub.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev.gka.ktupascohub.R
import com.dev.gka.ktupascohub.databinding.ItemSimilarQuestionBinding
import com.dev.gka.ktupascohub.models.Course

class RelatedViewHolder(val binding: ItemSimilarQuestionBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(course: Course, onClickListener: RelatedRecyclerAdapter.RelatedClickListener) {
        binding.textViewLecturerName.text = course.title
        binding.textViewCourseTitle.text = course.lecturer
        binding.textViewYear.text = binding.root.context.getString(R.string.year, course.year)
        binding.textViewLevel.text = binding.root.context.getString(R.string.level, course.level)
        binding.textViewSemester.text = binding.root.context.getString(R.string.semester, course.semester)
        binding.cardViewPaper.setOnClickListener {
            onClickListener.onClick(course)
        }
    }

    companion object {
        fun from(parent: ViewGroup): RelatedViewHolder {
            val itemBinding = ItemSimilarQuestionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return RelatedViewHolder(itemBinding)
        }
    }
}