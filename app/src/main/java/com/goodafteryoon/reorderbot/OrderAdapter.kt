package com.goodafteryoon.reorderbot

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class OrderAdapter(
    private var items: MutableList<OrderItem>,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    private var itemTouchHelper: ItemTouchHelper? = null

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewOrder: TextView = view.findViewById(R.id.textViewOrder)
        val textViewItem: TextView = view.findViewById(R.id.textViewItem)
        val buttonDelete: ImageButton = view.findViewById(R.id.buttonDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val item = items[position]
        
        // 항상 현재 position을 기준으로 순서 번호 표시 (1부터 시작)
        // 이렇게 하면 삭제 후에도 항상 1, 2, 3... 순서로 표시됨
        holder.textViewOrder.text = (position + 1).toString()
        holder.textViewItem.text = item.text
        
        holder.buttonDelete.setOnClickListener {
            onDelete(position)
        }
        
        // 아이템 전체를 터치하면 즉시 드래그 시작 (삭제 버튼 제외)
        holder.itemView.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 삭제 버튼이 아닌 다른 영역을 터치했을 때만 드래그 시작
                    val deleteButton = holder.buttonDelete
                    val deleteX = deleteButton.x
                    val deleteY = deleteButton.y
                    val deleteWidth = deleteButton.width
                    val deleteHeight = deleteButton.height
                    
                    if (event.x < deleteX || event.x > deleteX + deleteWidth ||
                        event.y < deleteY || event.y > deleteY + deleteHeight) {
                        itemTouchHelper?.startDrag(holder)
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }
        
        // 순서 번호를 터치해도 드래그 시작
        holder.textViewOrder.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                itemTouchHelper?.startDrag(holder)
                return@setOnTouchListener true
            }
            false
        }
        
        // 텍스트 부분을 터치해도 드래그 시작
        holder.textViewItem.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                itemTouchHelper?.startDrag(holder)
                return@setOnTouchListener true
            }
            false
        }
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<OrderItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun getItems(): List<OrderItem> = items.toList()
    
    fun setItemTouchHelper(helper: ItemTouchHelper) {
        this.itemTouchHelper = helper
    }
}
