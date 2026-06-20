package com.app.smartkantin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.smartkantin.data.entity.CartItemEntity
import com.app.smartkantin.databinding.ItemCartBinding
import com.app.smartkantin.utils.Formatter
import com.bumptech.glide.Glide

class CartAdapter(
    private val onUpdateQty: (CartItemEntity, Int) -> Unit
) : ListAdapter<CartItemEntity, CartAdapter.CartViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CartItemEntity) {
            binding.tvNamaMenu.text = item.namaMenu
            binding.tvHarga.text = Formatter.rupiah(item.harga)
            binding.tvQty.text = item.qty.toString()

            Glide.with(binding.ivMenu.context)
                .load(item.gambar.ifBlank { null })
                .placeholder(android.R.drawable.ic_menu_gallery)
                .centerCrop()
                .into(binding.ivMenu)

            binding.btnMinus.setOnClickListener { onUpdateQty(item, -1) }
            binding.btnPlus.setOnClickListener { onUpdateQty(item, 1) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CartItemEntity>() {
            override fun areItemsTheSame(oldItem: CartItemEntity, newItem: CartItemEntity) =
                oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: CartItemEntity, newItem: CartItemEntity) =
                oldItem == newItem
        }
    }
}