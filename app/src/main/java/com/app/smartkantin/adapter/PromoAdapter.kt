package com.app.smartkantin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.smartkantin.data.entity.PromoEntity
import com.app.smartkantin.databinding.ItemPromoBinding

class PromoAdapter(
    private val onDelete: (PromoEntity) -> Unit
) : ListAdapter<PromoEntity, PromoAdapter.PromoViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromoViewHolder {
        val binding = ItemPromoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PromoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PromoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PromoViewHolder(private val binding: ItemPromoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(promo: PromoEntity) {
            binding.tvKodePromo.text = promo.kodePromo
            binding.tvDeskripsi.text = "${promo.deskripsi} (Diskon ${promo.persenPotongan}%)"
            binding.btnDelete.setOnClickListener { onDelete(promo) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PromoEntity>() {
            override fun areItemsTheSame(oldItem: PromoEntity, newItem: PromoEntity) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: PromoEntity, newItem: PromoEntity) = oldItem == newItem
        }
    }
}