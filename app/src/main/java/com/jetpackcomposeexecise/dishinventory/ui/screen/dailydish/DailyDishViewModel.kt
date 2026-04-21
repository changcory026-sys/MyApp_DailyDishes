package com.jetpackcomposeexecise.dishinventory.ui.screen.dailydish

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetpackcomposeexecise.dishinventory.data.local.entity.DishEntity
import com.jetpackcomposeexecise.dishinventory.data.local.repository.MealDateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DailyDishViewModel @Inject constructor(
    private val repository: MealDateRepository // 注入新的 Repository
): ViewModel() {

    //------- 业务属性 -------
    //界面的uiState：此处因为只有一个属性，故直接使用
    private val _selectedDate = MutableStateFlow(LocalDate.now())//默认值为手机当天的日期
    // 将 LocalDate 转换为 String 供 UI 显示和数据库查询
    val selectedDateText: StateFlow<String> = _selectedDate
        .map { it.toString() } // 默认格式 yyyy-MM-dd
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LocalDate.now().toString())

    // 2. 动态计算下拉框选项：当前选中的日期及前后各 3 天
    val dateOptions: StateFlow<List<String>> = _selectedDate
        .map { current ->
            (-3..3).map { offset ->
                current.plusDays(offset.toLong()).toString()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    //3. 联动数据库查询：实时显示当前的菜单
    @OptIn(ExperimentalCoroutinesApi::class)
    val dailyDishes: StateFlow<List<DishEntity>> = _selectedDate
        .flatMapLatest { date ->     //flatMapLatest：一旦_selectedDate改变，则执行{}中内容
            repository.getDishesByDate(date.toString()).map { mealDateWithDishes ->
                mealDateWithDishes?.dishes ?: emptyList()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    //4. 控制截图时的 Loading 状态
    private val _isScreenshotLoading = MutableStateFlow(false)
    val isScreenshotLoading = _isScreenshotLoading.asStateFlow()

    fun setScreenshotLoading(isLoading: Boolean) {
        _isScreenshotLoading.value = isLoading
    }
    //------- 业务逻辑 -------
    //更新日期
    fun onDateSelected(newDate: String) {
        _selectedDate.value = LocalDate.parse(newDate)
    }
    // 前一天逻辑
    fun moveStepBack() {
        _selectedDate.value = _selectedDate.value.minusDays(1)
    }

    // 后一天逻辑
    fun moveStepForward() {
        _selectedDate.value = _selectedDate.value.plusDays(1)
    }

}