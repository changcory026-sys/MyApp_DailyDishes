package com.jetpackcomposeexecise.dishinventory.ui.screen.dishlist.dishdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetpackcomposeexecise.dishinventory.data.local.repository.DishRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DishDetailsViewModel @Inject constructor(
    private val dishRepository: DishRepository,
    savedstateHandle: SavedStateHandle
) : ViewModel() {

    //1. 取出传递的数据
    val dishId: Long = checkNotNull(savedstateHandle["dishId"])

    //2. 定义uiState：改为监听带食材的菜品详情
    val uiState = dishRepository.getDishWithIngredients(dishId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )//将Room中的联合查询结果装入StateFlow

    //3. 业务逻辑
    //3.1 【delete】回调
    fun deleteDish(onSuccess: () -> Unit){
        viewModelScope.launch {
            val currentDish = dishRepository.getDishById(dishId).firstOrNull()
            if (currentDish != null) dishRepository.delete(currentDish)
            onSuccess()
        }
    }
}
