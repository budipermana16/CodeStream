package com.example.codestream.ui.main

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.codestream.data.db.AppDatabaseHelper
import com.example.codestream.databinding.FragmentFindBinding
import com.example.codestream.ui.course.CourseDetailActivity
import com.example.codestream.utils.Constants

class FindFragment : Fragment() {
    private var _binding: FragmentFindBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: AppDatabaseHelper
    private lateinit var adapter: CourseAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFindBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        db = AppDatabaseHelper(requireContext())
        adapter = CourseAdapter(
            onAction = { course ->
                startActivity(Intent(requireContext(), CourseDetailActivity::class.java).apply {
                    putExtra(Constants.EXTRA_COURSE_ID, course.id)
                })
            },
            actionText = "Detail"
        )

        binding.rvCourses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCourses.adapter = adapter
        adapter.submit(db.getCourses())

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                adapter.submit(db.getCourses(s?.toString()))
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
