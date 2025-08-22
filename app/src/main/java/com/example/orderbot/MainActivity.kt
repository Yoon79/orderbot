package com.example.orderbot

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    
    private lateinit var editTextItem: EditText
    private lateinit var buttonAdd: Button
    private lateinit var recyclerViewItems: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    
    private val items = mutableListOf<OrderItem>()
    private var nextId = 1
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initViews()
        setupRecyclerView()
        setupClickListeners()
    }
    
    private fun initViews() {
        editTextItem = findViewById(R.id.editTextItem)
        buttonAdd = findViewById(R.id.buttonAdd)
        recyclerViewItems = findViewById(R.id.recyclerViewItems)
    }
    
    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(
            items = items,
            onMoveUp = { position -> moveItemUp(position) },
            onMoveDown = { position -> moveItemDown(position) },
            onDelete = { position -> deleteItem(position) }
        )
        
        recyclerViewItems.layoutManager = LinearLayoutManager(this)
        recyclerViewItems.adapter = orderAdapter
    }
    
    private fun setupClickListeners() {
        buttonAdd.setOnClickListener {
            addItem()
        }
    }
    
    private fun addItem() {
        val itemText = editTextItem.text.toString().trim()
        
        if (itemText.isEmpty()) {
            Toast.makeText(this, "항목을 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }
        
        val newItem = OrderItem(
            id = nextId++,
            text = itemText,
            order = items.size
        )
        
        items.add(newItem)
        orderAdapter.notifyItemInserted(items.size - 1)
        
        // 입력 필드 초기화
        editTextItem.text.clear()
        editTextItem.requestFocus()
        
        Toast.makeText(this, "항목이 추가되었습니다", Toast.LENGTH_SHORT).show()
    }
    
    private fun moveItemUp(position: Int) {
        if (position > 0) {
            val item = items[position]
            val previousItem = items[position - 1]
            
            // 순서 교환
            val tempOrder = item.order
            item.order = previousItem.order
            previousItem.order = tempOrder
            
            // 리스트에서 위치 교환
            items[position] = previousItem
            items[position - 1] = item
            
            orderAdapter.notifyItemMoved(position, position - 1)
        }
    }
    
    private fun moveItemDown(position: Int) {
        if (position < items.size - 1) {
            val item = items[position]
            val nextItem = items[position + 1]
            
            // 순서 교환
            val tempOrder = item.order
            item.order = nextItem.order
            nextItem.order = tempOrder
            
            // 리스트에서 위치 교환
            items[position] = nextItem
            items[position + 1] = item
            
            orderAdapter.notifyItemMoved(position, position + 1)
        }
    }
    
    private fun deleteItem(position: Int) {
        val deletedItem = items.removeAt(position)
        
        // 삭제된 항목 이후의 모든 항목들의 순서를 재조정
        for (i in position until items.size) {
            items[i].order = i
        }
        
        orderAdapter.notifyItemRemoved(position)
        
        Toast.makeText(this, "'${deletedItem.text}' 항목이 삭제되었습니다", Toast.LENGTH_SHORT).show()
    }
}
