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
import android.content.Context
import java.io.File
import java.io.FileOutputStream
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.app.AlertDialog
import android.os.Environment
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    
    private lateinit var editTextItem: EditText
    private lateinit var buttonAdd: Button
    private lateinit var recyclerViewItems: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var adView: AdView
    private lateinit var buttonExport: Button
    
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
        buttonExport = findViewById(R.id.buttonExport)
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
        buttonExport.setOnClickListener {
            showExportTypeDialog()
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

    private fun exportOrderListToFile() {
        if (items.isEmpty()) {
            Toast.makeText(this, "저장할 항목이 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        val text = items.sortedBy { it.order }
            .joinToString(separator = "\n") { "${it.order + 1}. ${it.text}" }
        try {
            val fileName = "order_list.txt"
            val downloadsDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)
            file.writeText(text)
            Toast.makeText(this, "다운로드 폴더에 저장됨: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "저장 실패: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private fun showExportTypeDialog() {
        val options = arrayOf("텍스트(.txt)로 저장", "이미지(.png)로 저장")
        AlertDialog.Builder(this)
            .setTitle("내보내기 형식 선택")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> exportOrderListToFile()
                    1 -> exportOrderListToImage()
                }
            }
            .show()
    }

    private fun exportOrderListToImage() {
        if (items.isEmpty()) {
            Toast.makeText(this, "저장할 항목이 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        val sortedItems = items.sortedBy { it.order }
        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 48f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val padding = 40
        val lineSpacing = 32
        val lines = sortedItems.mapIndexed { i, item -> "${i + 1}. ${item.text}" }
        val maxWidth = lines.maxOf { paint.measureText(it).toInt() } + padding * 2
        val lineHeight = (paint.fontMetrics.bottom - paint.fontMetrics.top).toInt() + lineSpacing
        val height = lineHeight * lines.size + padding * 2
        val bitmap = Bitmap.createBitmap(maxWidth, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        var y = padding - paint.fontMetrics.top
        for (line in lines) {
            canvas.drawText(line, padding.toFloat(), y, paint)
            y += lineHeight
        }
        try {
            val fileName = "order_list.png"
            val downloadsDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)
            FileOutputStream(file).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            }
            Toast.makeText(this, "다운로드 폴더에 이미지로 저장됨: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "이미지 저장 실패: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}
