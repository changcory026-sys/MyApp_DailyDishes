package com.jetpackcomposeexecise.dishinventory.ui.screen.adddailydish

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetpackcomposeexecise.dishinventory.data.local.entity.DishEntity
import com.jetpackcomposeexecise.dishinventory.data.local.repository.DishRepository
import com.jetpackcomposeexecise.dishinventory.data.local.repository.MealDateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.*

@HiltViewModel
class AddDailyDishViewModel @Inject constructor(
    private val mealDateRepository: MealDateRepository,
    private val dishRepository: DishRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    // 从导航参数中获取日期
    val date: String = savedStateHandle.get<String>("mealDate")?:""

    // 所有可选的菜品列表
    val allDishes: StateFlow<List<DishEntity>> = dishRepository.allDishes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 维护输入行列表
    private val _inputRows = MutableStateFlow(listOf(DishInputState()))
    val inputRows = _inputRows.asStateFlow()

    // 动态计算保存按钮是否可用
    val isSaveEnabled = _inputRows.map { rows ->
        rows.any { it.selectedDish != null && it.searchText == it.selectedDish.name }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // 更新搜索文字
    fun updateSearchText(rowId: String, text: String) {
        val currentRows = _inputRows.value.toMutableList()
        val index = currentRows.indexOfFirst { it.id == rowId }
        if (index != -1) {
            val row = currentRows[index]
            val newSelectedDish = if (row.selectedDish != null && row.selectedDish.name != text) null else row.selectedDish
            currentRows[index] = row.copy(searchText = text, selectedDish = newSelectedDish)
            _inputRows.value = currentRows
        }
    }

    // 更新用餐时段 (mealTime)
    fun updateMealTime(rowId: String, newMealTime: String) {
        val currentRows = _inputRows.value.toMutableList()
        val index = currentRows.indexOfFirst { it.id == rowId }
        if (index != -1) {
            currentRows[index] = currentRows[index].copy(mealTime = newMealTime)
            _inputRows.value = currentRows
        }
    }

    // 选中菜品
    fun onDishSelected(rowId: String, dish: DishEntity) {
        val currentRows = _inputRows.value.toMutableList()
        val index = currentRows.indexOfFirst { it.id == rowId }
        if (index != -1) {
            val currentRow = currentRows[index]
            currentRows[index] = currentRow.copy(searchText = dish.name, selectedDish = dish)

            if (index == currentRows.lastIndex) {
                // 优化：新增加的一行，继承上一行的用餐时段，而不是死板的默认“早饭”
                currentRows.add(DishInputState(mealTime = currentRow.mealTime))
            }
            _inputRows.value = currentRows
        }
    }

    // 保存
    fun onSaveBtnClick(onComplete: () -> Unit) {
        val validRows = _inputRows.value.filter { it.selectedDish != null }
        if (validRows.isEmpty()) return

        viewModelScope.launch {
            validRows.forEach { row ->
                mealDateRepository.addDishToDate(date, row.selectedDish!!.dishId, row.mealTime)
            }
            onComplete()
        }
    }
}

data class DishInputState(
    val id: String = UUID.randomUUID().toString(),
    val searchText: String = "",
    val selectedDish: DishEntity? = null,
    val mealTime: String = "早饭"
)