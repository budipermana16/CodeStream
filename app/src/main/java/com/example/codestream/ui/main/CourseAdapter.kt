package com.example.codestream.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.codestream.data.model.Course
import com.example.codestream.databinding.ItemCourseBinding

class CourseAdapter(
    private val onAction: (Course) -> Unit,
    private val actionText: String
) : RecyclerView.Adapter<CourseAdapter.VH>() {

    private val items = mutableListOf<Course>()

    fun submit(newItems: List<Course>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemCourseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])
    override fun getItemCount(): Int = items.size

    inner class VH(private val binding: ItemCourseBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(course: Course) {
            binding.tvTitle.text = course.title
            binding.tvCategory.text = "Kategori: ${course.category}"
            binding.tvPrice.text = "Rp ${course.price}"
            binding.btnAction.text = actionText
            binding.btnAction.setOnClickListener { onAction(course) }
        }
    }
}
