package com.dev.gka.ktupascohub.adapters



import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev.gka.ktupascohub.R
import com.dev.gka.ktupascohub.databinding.ItemPastQuestionBinding
import com.dev.gka.ktupascohub.models.Course

class CourseViewHolder private constructor(private val binding: ItemPastQuestionBinding):
    RecyclerView.ViewHolder(binding.root) {


        fun bind(course: Course, onClickListener: CourseRecyclerAdapter.OnClickListener) {
            binding.textViewLecturerName.text = course.title
            binding.textViewCourseTitle.text = course.lecturer
            binding.textViewYear.text = binding.root.context.getString(R.string.year, course.year)
            binding.textViewLevel.text = binding.root.context.getString(R.string.level, course.level)
            binding.textViewSemester.text = binding.root.context.getString(R.string.semester, course.semester)
            binding.imageViewDownload.setOnClickListener {
                onClickListener.onClick(course)
            }
        }

    companion object {
        fun from(parent: ViewGroup): CourseViewHolder {
            val itemBinding = ItemPastQuestionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return CourseViewHolder(itemBinding)
        }
    }
}