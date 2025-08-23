package com.example.orderbot

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class MainActivity : AppCompatActivity() {
    
    private lateinit var editTextItem: EditText
    private lateinit var buttonAdd: Button
    private lateinit var recyclerViewItems: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var adView: AdView
    
    private val items = mutableListOf<OrderItem>()
    private var nextId = 1
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // 애드몹 초기화
        MobileAds.initialize(this) {}
        adView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        
        initViews()
        setupRecyclerView()
        setupClickListeners()
        setupDragAndDrop()
    }
    
    override fun onPause() {
        super.onPause()
        if (::adView.isInitialized) {
            adView.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (::adView.isInitialized) {
            adView.resume()
        }
    }

    override fun onDestroy() {
        if (::adView.isInitialized) {
            adView.destroy()
        }
        super.onDestroy()
    }
    
    private fun initViews() {
        editTextItem = findViewById(R.id.editTextItem)
        buttonAdd = findViewById(R.id.buttonAdd)
        recyclerViewItems = findViewById(R.id.recyclerViewItems)
    }
    
    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(
            items = items,
            onDelete = { position -> deleteItem(position) }
        )
        
        recyclerViewItems.layoutManager = LinearLayoutManager(this)
        recyclerViewItems.adapter = orderAdapter
    }
    
    private fun setupDragAndDrop() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPos = viewHolder.bindingAdapterPosition
                val toPos = target.bindingAdapterPosition
                
                if (fromPos != RecyclerView.NO_POSITION && toPos != RecyclerView.NO_POSITION) {
                    // 아이템 위치 교환
                    val item = items[fromPos]
                    items.removeAt(fromPos)
                    items.add(toPos, item)
                    
                    // 순서 재조정
                    for (i in items.indices) {
                        items[i].order = i
                    }
                    
                    orderAdapter.notifyItemMoved(fromPos, toPos)
                    return true
                }
                return false
            }
            
            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    // 드래그 시작 시 아이템을 약간 확대하고 그림자 효과 추가
                    viewHolder?.itemView?.scaleX = 1.05f
                    viewHolder?.itemView?.scaleY = 1.05f
                    viewHolder?.itemView?.alpha = 0.8f
                }
            }
            
            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                // 드래그 종료 시 원래 상태로 복원
                viewHolder.itemView.scaleX = 1.0f
                viewHolder.itemView.scaleY = 1.0f
                viewHolder.itemView.alpha = 1.0f
                
                // 드래그 완료 후 모든 순서 번호 업데이트
                orderAdapter.notifyDataSetChanged()
            }
            
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // 스와이프 기능은 사용하지 않음
            }
            
            override fun isLongPressDragEnabled(): Boolean {
                // 길게 누르기 비활성화 (터치 감도 향상을 위해)
                return false
            }
            
            override fun isItemViewSwipeEnabled(): Boolean {
                return false
            }
            
            override fun getMoveThreshold(viewHolder: RecyclerView.ViewHolder): Float {
                // 이동 임계값을 매우 낮춰서 더 민감하게 반응
                return 0.05f
            }
            
            override fun getBoundingBoxMargin(): Int {
                // 드래그 감지 영역을 확대
                return 20
            }
        })
        
        itemTouchHelper.attachToRecyclerView(recyclerViewItems)
        
        // 어댑터에 터치 헬퍼 전달
        orderAdapter.setItemTouchHelper(itemTouchHelper)
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
    
    private fun deleteItem(position: Int) {
        val deletedItem = items.removeAt(position)
        
        // 삭제 후 모든 항목의 순서를 0부터 다시 시작하도록 재조정
        for (i in items.indices) {
            items[i].order = i
        }
        
        orderAdapter.notifyItemRemoved(position)
        // 삭제 후 전체 데이터셋을 업데이트하여 순서 번호가 1부터 시작하도록 함
        orderAdapter.notifyDataSetChanged()
        
        Toast.makeText(this, "'${deletedItem.text}' 항목이 삭제되었습니다", Toast.LENGTH_SHORT).show()
    }
}
