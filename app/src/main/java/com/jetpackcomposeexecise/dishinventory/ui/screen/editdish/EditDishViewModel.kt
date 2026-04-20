package com.jetpackcomposeexecise.dishinventory.ui.screen.editdish

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetpackcomposeexecise.dishinventory.data.local.entity.DishEntity
import com.jetpackcomposeexecise.dishinventory.data.local.repository.DishRepository
import com.jetpackcomposeexecise.dishinventory.ui.screen.addoreditdish.AddOrEditDishUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditDishViewModel @Inject constructor(
    private val dishRepository: DishRepository,
    savedstateHandle: SavedStateHandle
) : ViewModel() {

    //1. 取出传递的数据
    private val dishId: Long = checkNotNull(savedstateHandle["dishId"])

    //2. 定义uiState
    private val _uiState = MutableStateFlow(AddOrEditDishUiState())
    val uiState: StateFlow<AddOrEditDishUiState> = _uiState.asStateFlow()

    //3. 初始化uiState
    init {
        viewModelScope.launch {
            //通过传递的数据，获取初始化需要的对象
            val initialDish: DishEntity? = dishRepository.getDishById(dishId).firstOrNull()
            //该对象不为空时初始化uiState
            initialDish?.let { initialDish ->
                _uiState.update {
                    it.copy(
                        name = initialDish.name,
                        time = initialDish.time.toString(),
                        type = initialDish.type,
                        medicine = initialDish.medicine,
                        dayTime = initialDish.dayTime,
                        womanPeriod = initialDish.womanPeriod
                    )
                }
            }
        }
    }

    //4. 业务逻辑
    //--------- 业务逻辑 ---------
    //根据输入框内容，更新uiState的值
    fun updateName(newName: String) { _uiState.update { it.copy(name = newName) } }
    fun updatePrice(newPrice: String) { _uiState.update { it.copy(time = newPrice) } }
    fun updateType(newType: String) { _uiState.update { it.copy(type = newType) } }
    fun updateMedicine(newMedicine: String) { _uiState.update { it.copy(medicine = newMedicine) } }
    fun updateDayTime(newDayTime: String) { _uiState.update { it.copy(dayTime = newDayTime) } }
    fun updateWomanPeriod(newWomanPeriod: String) { _uiState.update { it.copy(womanPeriod = newWomanPeriod) } }

    //保存回调：将uiState的值插入Room数据库（通过Repository）
    fun updateDishItem(onSuccess: () -> Unit){
        viewModelScope.launch {
            dishRepository.update(
                DishEntity(
                    dishId = dishId,
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