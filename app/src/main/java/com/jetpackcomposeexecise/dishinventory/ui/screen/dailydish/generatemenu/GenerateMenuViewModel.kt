package com.jetpackcomposeexecise.dishinventory.ui.screen.dailydish.generatemenu

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetpackcomposeexecise.dishinventory.data.local.repository.DishRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class GenerateMenuViewModel @Inject constructor(
    private val dishRepository: DishRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val mealDate: String = checkNotNull(savedStateHandle["mealDate"])

    // 用餐时段状态
    private val _selectedMealTime = MutableStateFlow("中饭")
    val selectedMealTime = _selectedMealTime.asStateFlow()

    // 配置行状态：默认数量设为 1 👈
    private val _inputRows = MutableStateFlow(listOf(GenerateRowState(count = 1)))
    val inputRows = _inputRows.asStateFlow()

    // 统计每种类型的菜品总数
    val dishCountsByType: StateFlow<Map<String, Int>> = dishRepository.allDishes
        .map { list -> list.groupingBy { it.type }.eachCount() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // 按钮可用性
    val isGenerateEnabled = _inputRows.map { rows ->
        rows.any { it.type.isNotEmpty() && it.count > 0 }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun updateMealTime(time: String) {
        _selectedMealTime.value = time
    }

    fun updateRow(rowId: String, type: String? = null, count: Int? = null) {
        _inputRows.update { currentRows ->
            val newList = currentRows.toMutableList()
            val index = newList.indexOfFirst { it.id == rowId }
            if (index != -1) {
                val oldRow = newList[index]
                val newRow = oldRow.copy(
                    type = type ?: oldRow.type,
                    count = count ?: oldRow.count
                )
                newList[index] = newRow

                // 动态增行逻辑：如果当前修改的是最后一行，且已经选好了类型和数量
                if (index == newList.lastIndex && newRow.type.isNotEmpty() && newRow.count > 0) {
                    // 新行默认数量也设为 1 👈
                    newList.add(GenerateRowState(count = 1))
                }
                newList
            } else currentRows
        }
    }

    fun removeRow(rowId: String) {
        _inputRows.update { it.filter { row -> row.id != rowId } }
    }

    fun getGenerateConfigJson(): String {
        val validConfigs = _inputRows.value
            .filter { it.type.isNotEmpty() && it.count > 0 }
            .map { it.type to it.count }
        return Json.encodeToString(validConfigs)
    }
}

data class GenerateRowState(
    val id: String = UUID.randomUUID().toString(),
    val type: String = "小炒",
    val count: Int = 1 // 👈 默认值改为 1
)
