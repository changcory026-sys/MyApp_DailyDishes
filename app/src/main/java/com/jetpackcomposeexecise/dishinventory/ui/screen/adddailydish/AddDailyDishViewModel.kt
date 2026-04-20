package com.jetpackcomposeexecise.dishinventory.ui.screen.adddailydish

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetpackcomposeexecise.dishinventory.data.local.entity.DishEntity
import com.jetpackcomposeexecise.dishinventory.data.local.repository.DishRepository
import com.jetpackcomposeexecise.dishinventory.data.local.repository.MealDateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


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

    // 当前选中的菜品
    var selectedDish by mutableStateOf<DishEntity?>(null)
        private set

    fun onDishSelected(dish: DishEntity) {
        selectedDish = dish
    }

    // 保存并回调返回
    fun onSaveBtnClick(onComplete: () -> Unit) {
        val dishId = selectedDish?.dishId ?: return
        viewModelScope.launch {
            mealDateRepository.addDishToDate(date, dishId)
            Log.e("DailyDishScreen", "onSaveBtnClick: ", )
            onComplete() // 执行返回操作
        }
    }
}