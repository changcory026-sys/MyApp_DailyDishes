package com.jetpackcomposeexecise.dishinventory.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetpackcomposeexecise.dishinventory.repository.DishRepository
import com.jetpackcomposeexecise.dishinventory.room.DishItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.text.toDoubleOrNull

@HiltViewModel
class AddDishViewModel @Inject constructor(
    private val repository: DishRepository
) : ViewModel() {
    //--------- 业务数据 ---------
    private val _uiState = MutableStateFlow(AddOrEditDishUiState())
    val uiState: StateFlow<AddOrEditDishUiState> = _uiState.asStateFlow()//将StateFlow变为只读

    //--------- 业务逻辑 ---------
    //根据输入框内容，更新uiState的值
    fun updateName(newName: String) { _uiState.update { it.copy(name = newName) } }
    fun updatePrice(newPrice: String) { _uiState.update { it.copy(time = newPrice) } }
    fun updateType(newType: String) { _uiState.update { it.copy(type = newType) } }
    fun updateMedicine(newMedicine: String) { _uiState.update { it.copy(medicine = newMedicine) } }
    fun updateDayTime(newDayTime: String) { _uiState.update { it.copy(dayTime = newDayTime) } }
    fun updateWomanPeriod(newWomanPeriod: String) { _uiState.update { it.copy(womanPeriod = newWomanPeriod) } }

    //保存回调：将uiState的值插入Room数据库（通过Repository）
    fun addDishItem(onSuccess: () -> Unit){
        viewModelScope.launch {
            repository.insert(
                DishItem(
                    name = _uiState.value.name,
                    time = _uiState.value.time.toDoubleOrNull() ?: 0.0,//从string转化成数字类型,
                    type = _uiState.value.type,
                    medicine = _uiState.value.medicine,
                    dayTime = _uiState.value.dayTime,
                    womanPeriod = _uiState.value.womanPeriod
                )
            )
            onSuccess()//成功存入数据后的回调：返回上一页
        }
    }
}