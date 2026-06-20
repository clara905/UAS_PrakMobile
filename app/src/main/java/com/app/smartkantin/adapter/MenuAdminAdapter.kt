package com.app.smartkantin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.smartkantin.data.entity.MenuEntity
import com.app.smartkantin.databinding.ItemMenuAdminBinding
import com.app.smartkantin.utils.Formatter
import com.bumptech.glide.Glide

class MenuAdminAdapter(
    private val onEditClick: (MenuEntity) -> Unit,
    private val onDeleteClick: (MenuEntity) -> Unit
) : ListAdapter<MenuEntity, MenuAdminAdapter.MenuViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = ItemMenuAdminBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MenuViewHolder(private val binding: ItemMenuAdminBinding) :
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

            binding.btnEdit.setOnClickListener { onEditClick(menu) }
            binding.btnDelete.setOnClickListener { onDeleteClick(menu) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<MenuEntity>() {
        override fun areItemsTheSame(oldItem: MenuEntity, newItem: MenuEntity) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: MenuEntity, newItem: MenuEntity) =
            oldItem == newItem
    }
}