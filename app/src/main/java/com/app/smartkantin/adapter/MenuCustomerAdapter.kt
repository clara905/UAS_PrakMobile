package com.app.smartkantin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.smartkantin.data.entity.MenuEntity
import com.app.smartkantin.databinding.ItemMenuCustomerBinding
import com.app.smartkantin.utils.Formatter
import com.bumptech.glide.Glide

class MenuCustomerAdapter(
    private val onAddClick: (MenuEntity) -> Unit
) : ListAdapter<MenuEntity, MenuCustomerAdapter.MenuViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = ItemMenuCustomerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MenuViewHolder(private val binding: ItemMenuCustomerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(menu: MenuEntity) {
            binding.tvNamaMenu.text = menu.namaMenu
            binding.tvDeskripsi.text = menu.deskripsi
            binding.tvHarga.text = Formatter.rupiah(menu.harga)
            
            Glide.with(binding.ivMenu.context)
                .load(menu.gambar.ifBlank { null })
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .centerCrop()
                .into(binding.ivMenu)

            binding.btnTambah.setOnClickListener { onAddClick(menu) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MenuEntity>() {
            override fun areItemsTheSame(oldItem: MenuEntity, newItem: MenuEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: MenuEntity, newItem: MenuEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}