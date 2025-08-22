package com.example.orderbot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrderAdapter(
    private var items: MutableList<OrderItem>,
    private val onMoveUp: (Int) -> Unit,
    private val onMoveDown: (Int) -> Unit,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewOrder: TextView = view.findViewById(R.id.textViewOrder)
        val textViewItem: TextView = view.findViewById(R.id.textViewItem)
        val buttonMoveUp: ImageButton = view.findViewById(R.id.buttonMoveUp)
        val buttonMoveDown: ImageButton = view.findViewById(R.id.buttonMoveDown)
        val buttonDelete: ImageButton = view.findViewById(R.id.buttonDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val item = items[position]
        
        holder.textViewOrder.text = (item.order + 1).toString()
        holder.textViewItem.text = item.text
        
        // 위로 이동 버튼 (첫 번째 항목이면 비활성화)
        holder.buttonMoveUp.isEnabled = position > 0
        holder.buttonMoveUp.alpha = if (position > 0) 1.0f else 0.3f
        
        // 아래로 이동 버튼 (마지막 항목이면 비활성화)
        holder.buttonMoveDown.isEnabled = position < items.size - 1
        holder.buttonMoveDown.alpha = if (position < items.size - 1) 1.0f else 0.3f
        
        holder.buttonMoveUp.setOnClickListener {
            onMoveUp(position)
        }
        
        holder.buttonMoveDown.setOnClickListener {
            onMoveDown(position)
        }
        
        holder.buttonDelete.setOnClickListener {
            onDelete(position)
        }
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<OrderItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun getItems(): List<OrderItem> = items.toList()
}
