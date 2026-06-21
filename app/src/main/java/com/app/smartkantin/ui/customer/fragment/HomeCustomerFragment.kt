package com.app.smartkantin.ui.customer.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.app.smartkantin.SmartKantinApp
import com.app.smartkantin.adapter.MenuGridAdapter
import com.app.smartkantin.data.repository.MenuRepository
import com.app.smartkantin.data.repository.PromoRepository
import com.app.smartkantin.databinding.FragmentHomeCustomerBinding
import com.app.smartkantin.ui.customer.MenuDetailActivity
import com.app.smartkantin.utils.SessionManager
import com.app.smartkantin.viewmodel.MenuViewModel
import com.app.smartkantin.viewmodel.MenuViewModelFactory
import com.app.smartkantin.viewmodel.PromoViewModel
import com.app.smartkantin.viewmodel.PromoViewModelFactory

class HomeCustomerFragment : Fragment() {

    private var _binding: FragmentHomeCustomerBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MenuViewModel
    private lateinit var promoViewModel: PromoViewModel
    private lateinit var adapter: MenuGridAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeCustomerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = SessionManager(requireContext())
        binding.tvWelcome.text = "Halo, ${sessionManager.getNama()}!"

        val app = requireActivity().application as SmartKantinApp
        val menuRepository = MenuRepository(app.database.menuDao())
        val promoRepository = PromoRepository(app.database.promoDao())
        
        viewModel = ViewModelProvider(this, MenuViewModelFactory(menuRepository))[MenuViewModel::class.java]
        promoViewModel = ViewModelProvider(this, PromoViewModelFactory(promoRepository))[PromoViewModel::class.java]

        setupRecyclerView()
        setupCategories()
        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnCart.setOnClickListener {
            startActivity(Intent(requireContext(), com.app.smartkantin.ui.customer.CartActivity::class.java))
        }
    }

    private fun setupCategories() {
        binding.catMakanan.tvCategoryName.text = "Makanan"
        binding.catMinuman.tvCategoryName.text = "Minuman"
        binding.catCemilan.tvCategoryName.text = "Cemilan"
        binding.catSayuran.tvCategoryName.text = "Sayuran"

        binding.catMakanan.ivCategory.setImageResource(com.app.smartkantin.R.drawable.ic_category_makanan)
        binding.catMinuman.ivCategory.setImageResource(com.app.smartkantin.R.drawable.ic_category_minuman)
        binding.catCemilan.ivCategory.setImageResource(com.app.smartkantin.R.drawable.ic_category_cemilan)
        binding.catSayuran.ivCategory.setImageResource(com.app.smartkantin.R.drawable.ic_category_sayuran)
    }

    private fun setupRecyclerView() {
        adapter = MenuGridAdapter { menu ->
            val intent = Intent(requireContext(), MenuDetailActivity::class.java)
            intent.putExtra(MenuDetailActivity.EXTRA_MENU_ID, menu.id)
            startActivity(intent)
        }
        binding.rvMenuGrid.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = this@HomeCustomerFragment.adapter
        }
    }

    private fun observeViewModel() {
        viewModel.allMenu.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list.take(4)) 
        }

        promoViewModel.allPromos.observe(viewLifecycleOwner) { promos ->
            if (promos.isNotEmpty()) {
                val p = promos.first()
                binding.tvPromoTitle.text = "Promo Spesial!"
                binding.tvPromoDesc.text = "Gunakan kode ${p.kodePromo} untuk diskon ${p.persenPotongan}%"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}