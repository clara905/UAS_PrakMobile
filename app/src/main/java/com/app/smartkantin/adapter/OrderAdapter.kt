package com.app.smartkantin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.smartkantin.R
import com.app.smartkantin.data.entity.OrderEntity
import com.app.smartkantin.databinding.ItemOrderBinding
import com.app.smartkantin.utils.Formatter
import com.app.smartkantin.utils.OrderStatus

class OrderAdapter(
    private val isAdmin: Boolean = false,
    private val onUpdateStatus: (OrderEntity) -> Unit = {}
) : ListAdapter<OrderEntity, OrderAdapter.OrderViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrderViewHolder(private val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: OrderEntity) {
            binding.tvOrderId.text = "Order #${order.id}"
            binding.tvTanggal.text = order.tanggal
            binding.tvTotalHarga.text = Formatter.rupiah(order.totalHarga)
            binding.tvStatus.text = order.status

            val statusColor = when (order.status) {
                OrderStatus.MENUNGGU -> R.color.text_secondary
                OrderStatus.DIPROSES -> R.color.primary
                OrderStatus.SELESAI -> R.color.accent
                else -> R.color.text_primary
            }
            binding.tvStatus.setTextColor(ContextCompat.getColor(binding.root.context, statusColor))

            if (isAdmin && order.status != OrderStatus.SELESAI) {
                binding.btnUpdateStatus.visibility = android.view.View.VISIBLE
                binding.btnUpdateStatus.text = if (order.status == OrderStatus.MENUNGGU) "Proses" else "Selesaikan"
                binding.btnUpdateStatus.setOnClickListener { onUpdateStatus(order) }
            } else {
                binding.btnUpdateStatus.visibility = android.view.View.GONE
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<OrderEntity>() {
            override fun areItemsTheSame(oldItem: OrderEntity, newItem: OrderEntity) =
                oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: OrderEntity, newItem: OrderEntity) =
                oldItem == newItem
        }
    }
}