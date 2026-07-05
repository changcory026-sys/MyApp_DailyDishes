package com.jetpackcomposeexecise.dishinventory.ui.screen.dailydish.dailydish

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetpackcomposeexecise.dishinventory.data.local.entity.DishEntity
import com.jetpackcomposeexecise.dishinventory.data.local.entity.DishWithMealTime
import com.jetpackcomposeexecise.dishinventory.data.local.repository.MealDateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DailyDishViewModel @Inject constructor(
    private val repository: MealDateRepository
): ViewModel() {

    //------- 业务属性 -------
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    
    val selectedDateText: StateFlow<String> = _selectedDate
        .map { it.toString() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LocalDate.now().toString())

    val dateOptions: StateFlow<List<String>> = _selectedDate
        .map { current ->
            (-3..3).map { offset ->
                current.plusDays(offset.toLong()).toString()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 联动数据库查询：实时显示当前的菜单，按 mealTime 分组，且组内按菜品类型排序
    @OptIn(ExperimentalCoroutinesApi::class)
    val dailyDishes: StateFlow<Map<String, List<DishWithMealTime>>> = _selectedDate
        .flatMapLatest { date ->
            repository.getDishesWithMealTimeByDate(date.toString()).map { list ->
                // 1. 先按 mealTime 分组
                list.groupBy { it.mealTime }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    //------- 业务逻辑 -------
    fun onDateSelected(newDate: String) {
        _selectedDate.value = LocalDate.parse(newDate)
    }

    fun moveStepBack() {
        _selectedDate.value = _selectedDate.value.minusDays(1)
    }

    fun moveStepForward() {
        _selectedDate.value = _selectedDate.value.plusDays(1)
    }

    // 删除当天的指定菜式
    fun deleteDishFromCurrentDate(dishId: Long, mealTime: String) {
        viewModelScope.launch {
            repository.deleteDishFromDate(_selectedDate.value.toString(), dishId, mealTime)
        }
    }

    // 更新菜式的完成状态
    fun updateDishCompletedStatus(dishId: Long, mealTime: String, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateDishCompletedStatus(_selectedDate.value.toString(), dishId, mealTime, isCompleted)
        }
    }
}
