package com.app.smartkantin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.smartkantin.data.entity.MenuEntity
import com.app.smartkantin.databinding.ItemMenuGridBinding
import com.app.smartkantin.utils.Formatter
import com.bumptech.glide.Glide

class MenuGridAdapter(
    private val onItemClick: (MenuEntity) -> Unit
) : ListAdapter<MenuEntity, MenuGridAdapter.GridViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val binding = ItemMenuGridBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return GridViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GridViewHolder(private val binding: ItemMenuGridBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(menu: MenuEntity) {
            binding.tvNamaMenu.text = menu.namaMenu
            binding.tvHarga.text = Formatter.rupiah(menu.harga)
            
            Glide.with(binding.ivMenu.context)
                .load(menu.gambar.ifBlank { null })
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .centerCrop()
                .into(binding.ivMenu)

            binding.root.setOnClickListener { onItemClick(menu) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MenuEntity>() {
            override fun areItemsTheSame(oldItem: MenuEntity, newItem: MenuEntity) =
                oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: MenuEntity, newItem: MenuEntity) =
                oldItem == newItem
        }
    }
}