package com.app.smartkantin.ui.admin.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.smartkantin.databinding.FragmentHomePenjualBinding
import com.app.smartkantin.utils.SessionManager

class HomePenjualFragment : Fragment() {

    private var _binding: FragmentHomePenjualBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomePenjualBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sessionManager = SessionManager(requireContext())
        binding.tvNamaToko.text = sessionManager.getNamaToko() ?: "Kantin Anda"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}