package com.dev.gka.ktupascohub.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev.gka.ktupascohub.models.Course

class CourseRecyclerAdapter(
    private val onClickListener: OnClickListener,
    private val courses: List<Course>
) :
    RecyclerView.Adapter<CourseViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        return CourseViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courses[position]
        holder.bind(course, onClickListener)
    }

    override fun getItemCount() = courses.size


    class OnClickListener(val clickListener: (courseProperty: Course) -> Unit) {
        fun onClick(courseProperty: Course) = clickListener(courseProperty)
    }
}