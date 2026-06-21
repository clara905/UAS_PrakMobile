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
    private val onItemClick: (MenuEntity) -> Unit,
    private val onAddClick: (MenuEntity) -> Unit
) : ListAdapter<MenuEntity, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    private var isGridView = false

    fun setGridView(isGrid: Boolean) {
        this.isGridView = isGrid
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (isGridView) VIEW_TYPE_GRID else VIEW_TYPE_LIST
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_GRID) {
            val binding = com.app.smartkantin.databinding.ItemMenuGridBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            GridViewHolder(binding)
        } else {
            val binding = ItemMenuCustomerBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            ListViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if (holder is ListViewHolder) holder.bind(item)
        else if (holder is GridViewHolder) holder.bind(item)
    }

    inner class ListViewHolder(private val binding: ItemMenuCustomerBinding) :
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

            binding.root.setOnClickListener { onItemClick(menu) }
            binding.btnTambah.setOnClickListener { onAddClick(menu) }
        }
    }

    inner class GridViewHolder(private val binding: com.app.smartkantin.databinding.ItemMenuGridBinding) :
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
        private const val VIEW_TYPE_LIST = 0
        private const val VIEW_TYPE_GRID = 1

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