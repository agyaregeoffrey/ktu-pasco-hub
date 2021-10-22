package com.dev.gka.ktupascohub.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.dev.gka.ktupascohub.R
import com.dev.gka.ktupascohub.activities.SimilarPapersActivity
import com.dev.gka.ktupascohub.databinding.ItemPastQuestionBinding
import com.dev.gka.ktupascohub.models.Course
import com.dev.gka.ktupascohub.utilities.Constants.LECTURER

class CourseRecyclerAdapter(
    private val onClickListener: OnClickListener,
    private val courses: List<Course>
) : RecyclerView.Adapter<CourseRecyclerAdapter.CourseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val binding = ItemPastQuestionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CourseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        with(holder) {
           with(courses[position]) {
               binding.textViewLecturerName.text = title
               binding.textViewCourseTitle.text = lecturer
               binding.textViewYear.text = binding.root.context.getString(R.string.year, year)
               binding.textViewLevel.text = binding.root.context.getString(R.string.level, level)
               binding.textViewSemester.text = binding.root.context.getString(R.string.semester, semester)
           }
            binding.imageViewDownload.setOnClickListener(this)
            binding.cardViewPaper.setOnClickListener {
                onClickListener.onClick(courses[position])
            }
        }
    }

    override fun getItemCount() = courses.size


    inner class CourseViewHolder (val binding: ItemPastQuestionBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        override fun onClick(v: View?) {
            showPopupMenu(v)
        }

        private fun showPopupMenu(v: View?) {
            val popupMenu = PopupMenu(v?.context, v)
            popupMenu.inflate(R.menu.context_menu)
            popupMenu.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.menu_similar -> {
                        val intent = Intent(v?.context, SimilarPapersActivity::class.java)
                        intent.putExtra(LECTURER, courses[adapterPosition].lecturer)
                        v?.context?.startActivity(intent)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

    }

    class OnClickListener(val clickListener: (courseProperty: Course) -> Unit) {
        fun onClick(courseProperty: Course) = clickListener(courseProperty)
    }

}