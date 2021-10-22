package com.dev.gka.ktupascohub.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev.gka.ktupascohub.models.Course

class RelatedRecyclerAdapter(
    private val onClickListener: RelatedClickListener,
    private val courses: List<Course>
) : RecyclerView.Adapter<RelatedViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RelatedViewHolder {
        return RelatedViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RelatedViewHolder, position: Int) {
        val course = courses[position]
        holder.bind(course, onClickListener)
    }

    override fun getItemCount() = courses.size

    class RelatedClickListener(val clickListener: (courseProperty: Course) -> Unit) {
        fun onClick(courseProperty: Course) = clickListener(courseProperty)
    }
}