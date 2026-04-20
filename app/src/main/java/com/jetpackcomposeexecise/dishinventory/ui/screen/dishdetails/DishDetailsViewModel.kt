package com.jetpackcomposeexecise.dishinventory.ui.screen.dishdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetpackcomposeexecise.dishinventory.data.local.repository.DishRepository
import com.jetpackcomposeexecise.dishinventory.data.local.entity.DishEntity
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

    //2. 定义uiState
    val uiState = dishRepository.getDishById(dishId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DishEntity(dishId, "", 0.0, "", "", "")//根据参数中的数据，创建uiState初始值
    )//将Room中的Item装入StateFlow，这样一旦Room中的Item改变，则uiState也会随之改变，UI对应的组件会重组

    //3. 业务逻辑
    //3.1 【delete】回调
    fun deleteDish(onSuccess: () -> Unit){
        //（协程）通过Repository删除该Item
        viewModelScope.launch {
            //获取当前的FruitItem，如果为空则结束本方法
            val currentDish = dishRepository.getDishById(dishId).firstOrNull()
            //（协程）通过Repository删除该Item
            if (currentDish != null) dishRepository.delete(currentDish)
            //成功删除数据后的回调：返回上一页
            onSuccess()
        }
    }
}