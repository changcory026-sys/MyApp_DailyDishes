package com.jetpackcomposeexecise.dishinventory.ui.screen.adddailydish

import android.util.Log
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
    // 从导航参数中获取日期 (例如 "2026-04-18")
    val date: String = savedStateHandle.get<String>("mealDate")?:""

    // 所有可选的菜品列表
    val allDishes: StateFlow<List<DishEntity>> = dishRepository.allDishes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 👇 核心修改 1：维护一个输入行的列表，初始化时默认给一个空行
    private val _inputRows = MutableStateFlow(listOf(DishInputState()))
    val inputRows = _inputRows.asStateFlow()

    // 👇 核心修改 2：动态计算【Save】按钮是否可用（只要有一行选中了真实的菜，就能点）
    val isSaveEnabled = _inputRows.map { rows ->
        rows.any { it.selectedDish != null && it.searchText == it.selectedDish.name }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // 当用户在某一行打字时调用
    fun updateSearchText(rowId: String, text: String) {
        val currentRows = _inputRows.value.toMutableList()
        val index = currentRows.indexOfFirst { it.id == rowId }
        if (index != -1) {
            val row = currentRows[index]
            // 如果修改了文字，导致文字和原来选中的菜名不匹配，就清空选中状态
            val newSelectedDish = if (row.selectedDish != null && row.selectedDish.name != text) null else row.selectedDish
            currentRows[index] = row.copy(searchText = text, selectedDish = newSelectedDish)
            _inputRows.value = currentRows
        }
    }

    // 当用户在下拉菜单中点击了某个菜品时调用
    fun onDishSelected(rowId: String, dish: DishEntity) {
        val currentRows = _inputRows.value.toMutableList()
        val index = currentRows.indexOfFirst { it.id == rowId }
        if (index != -1) {
            // 更新当前行为选中状态
            currentRows[index] = currentRows[index].copy(searchText = dish.name, selectedDish = dish)

            // 👇 核心需求：如果当前操作的是最后一行，则在底部追加一个新的空白行！
            if (index == currentRows.lastIndex) {
                currentRows.add(DishInputState())
            }

            _inputRows.value = currentRows
        }
    }

    // 保存并回调返回
    fun onSaveBtnClick(onComplete: () -> Unit) {
        // 过滤出所有有真实选中菜品的行
        val validDishes = _inputRows.value.mapNotNull { it.selectedDish }
        if (validDishes.isEmpty()) return

        viewModelScope.launch {
            // 循环将所有选中的菜品添加到这一天
            validDishes.forEach { dish ->
                mealDateRepository.addDishToDate(date, dish.dishId)
            }
            Log.e("DailyDishScreen", "成功保存了 ${validDishes.size} 个菜式")
            onComplete()
        }
    }
}

//定义行状态数据类：记录界面上每一行下拉输入框的状态
data class DishInputState(
    val id: String = UUID.randomUUID().toString(), // 唯一标识符，给 LazyColumn 的 key 用
    val searchText: String = "",
    val selectedDish: DishEntity? = null
)