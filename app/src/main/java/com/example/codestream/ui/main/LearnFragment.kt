package com.example.codestream.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.codestream.data.db.AppDatabaseHelper
import com.example.codestream.data.db.SessionManager
import com.example.codestream.databinding.FragmentLearnBinding
import com.example.codestream.ui.course.CourseDashboardActivity
import com.example.codestream.utils.Constants

class LearnFragment : Fragment() {
    private var _binding: FragmentLearnBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: AppDatabaseHelper
    private lateinit var session: SessionManager
    private lateinit var adapter: CourseAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLearnBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        db = AppDatabaseHelper(requireContext())
        session = SessionManager(requireContext())

        adapter = CourseAdapter(
            onAction = { course ->
                startActivity(Intent(requireContext(), CourseDashboardActivity::class.java).apply {
                    putExtra(Constants.EXTRA_COURSE_ID, course.id)
                })
            },
            actionText = "Buka"
        )

        binding.rvEnrolled.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEnrolled.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        adapter.submit(db.getEnrolledCourses(session.userId()))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
