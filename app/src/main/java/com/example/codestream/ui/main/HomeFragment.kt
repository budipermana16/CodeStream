package com.example.codestream.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.codestream.R
import com.example.codestream.data.db.AppDatabaseHelper
import com.example.codestream.data.db.SessionManager
import com.example.codestream.data.model.Course
import com.example.codestream.databinding.FragmentHomeBinding
import com.example.codestream.ui.course.CourseDetailActivity
import com.example.codestream.utils.Constants

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: AppDatabaseHelper
    private lateinit var session: SessionManager

    private lateinit var popularAdapter: CourseAdapter
    private lateinit var recommendedAdapter: CourseAdapter

    private var allCourses: List<Course> = emptyList()
    private var currentCategory: String? = null // null = semua

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        db = AppDatabaseHelper(requireContext())
        session = SessionManager(requireContext())

        // Greeting
        val user = db.getUser(session.userId())
        binding.tvGreeting.text = "Selamat datang, ${user?.name ?: "User"}!"

        // Recycler Popular (horizontal)
        popularAdapter = CourseAdapter(
            onAction = { openCourseDetail(it) },
            actionText = "Detail"
        )
        binding.rvPopular.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvPopular.adapter = popularAdapter

        // Recycler Recommended (vertical)
        recommendedAdapter = CourseAdapter(
            onAction = { openCourseDetail(it) },
            actionText = "Detail"
        )
        binding.rvRecommended.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecommended.adapter = recommendedAdapter

        // Load data awal
        allCourses = db.getCourses()
        applyFilter()

        // Search
        binding.etSearch.doAfterTextChanged {
            applyFilter()
        }

        // Category buttons
        binding.btnCatAll.setOnClickListener { currentCategory = null; applyFilter() }
        binding.btnCatAndroid.setOnClickListener { currentCategory = "Android"; applyFilter() }
        binding.btnCatJava.setOnClickListener { currentCategory = "Java"; applyFilter() }
        binding.btnCatDb.setOnClickListener { currentCategory = "Database"; applyFilter() }
        binding.btnCatInf.setOnClickListener { currentCategory = "Informatika"; applyFilter() }
    }

    private fun applyFilter() {
        val q = binding.etSearch.text?.toString()?.trim().orEmpty()

        // Filter kategori + search
        val filtered = allCourses.filter { c ->
            val matchCategory = currentCategory?.let { c.category.equals(it, ignoreCase = true) } ?: true
            val matchQuery = if (q.isBlank()) true
            else (c.title.contains(q, true) || c.category.contains(q, true) || c.description.contains(q, true))
            matchCategory && matchQuery
        }

        // Popular: ambil 5 pertama
        popularAdapter.submit(filtered.take(5))

        // Recommended: semua hasil filter
        recommendedAdapter.submit(filtered)
    }

    private fun openCourseDetail(course: Course) {
        startActivity(Intent(requireContext(), CourseDetailActivity::class.java).apply {
            putExtra(Constants.EXTRA_COURSE_ID, course.id)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
