package com.example.codestream.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.codestream.data.db.AppDatabaseHelper
import com.example.codestream.data.db.SessionManager
import com.example.codestream.databinding.FragmentProfileBinding
import com.example.codestream.ui.certificate.CertificateActivity
import com.example.codestream.ui.profile.EditProfileActivity


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val session = SessionManager(requireContext())
        val db = AppDatabaseHelper(requireContext())
        val user = db.getUser(session.userId())
        binding.tvName.text = "Nama: ${user?.name ?: "-"}"
        binding.tvEmail.text = "Email: ${user?.email ?: "-"}"
        binding.tvPhone.text = "Phone: ${user?.phone ?: "-"}"
        binding.tvBio.text = "Bio: ${user?.bio ?: "-"}"

        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }
        binding.btnCertificates.setOnClickListener {
            startActivity(Intent(requireContext(), CertificateActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
