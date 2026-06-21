package com.app.smartkantin.ui.customer.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatDelegate
import com.app.smartkantin.databinding.FragmentProfileCustomerBinding
import com.app.smartkantin.ui.auth.LoginActivity
import com.app.smartkantin.utils.SessionManager

class ProfileCustomerFragment : Fragment() {

    private var _binding: FragmentProfileCustomerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileCustomerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sessionManager = SessionManager(requireContext())

        binding.tvNama.text = sessionManager.getNama()
        binding.tvEmail.text = sessionManager.getEmail()
        
        // NIM static placeholder as requested (Student data)
        binding.tvNim.text = "20240001" 

        // Dark Mode Logic
        binding.switchDarkMode.isChecked = sessionManager.isDarkMode()
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            sessionManager.setDarkMode(isChecked)
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        binding.btnLogout.setOnClickListener {
            sessionManager.logout()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}