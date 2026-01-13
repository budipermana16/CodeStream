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
import com.example.codestream.databinding.FragmentWishlistBinding
import com.example.codestream.ui.course.PaymentActivity
import com.example.codestream.utils.Constants

class WishlistFragment : Fragment() {
    private var _binding: FragmentWishlistBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: AppDatabaseHelper
    private lateinit var session: SessionManager
    private lateinit var adapter: CourseAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWishlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        db = AppDatabaseHelper(requireContext())
        session = SessionManager(requireContext())

        adapter = CourseAdapter(
            onAction = { course ->
                startActivity(Intent(requireContext(), PaymentActivity::class.java).apply {
                    putExtra(Constants.EXTRA_COURSE_ID, course.id)
                })
            },
            actionText = "Bayar"
        )

        binding.rvWishlist.layoutManager = LinearLayoutManager(requireContext())
        binding.rvWishlist.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        adapter.submit(db.getWishlistCourses(session.userId()))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
