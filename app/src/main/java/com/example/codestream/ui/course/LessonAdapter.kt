package com.example.codestream.ui.course

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.codestream.data.model.Lesson
import com.example.codestream.databinding.ItemLessonBinding

class LessonAdapter(
    private val onClick: (Lesson) -> Unit
) : RecyclerView.Adapter<LessonAdapter.VH>() {

    private val items = mutableListOf<Lesson>()

    fun submit(newItems: List<Lesson>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemLessonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])
    override fun getItemCount(): Int = items.size

    inner class VH(private val binding: ItemLessonBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(lesson: Lesson) {
            binding.tvTitle.text = lesson.title
            binding.tvType.text = "Tipe: ${lesson.type}"
            binding.tvDone.text = if (lesson.isDone) "Selesai" else "Belum selesai"
            binding.root.setOnClickListener { onClick(lesson) }
        }
    }
}
