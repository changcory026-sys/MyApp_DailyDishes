package com.jetpackcomposeexecise.dishinventory.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetpackcomposeexecise.dishinventory.repository.DishRepository
import com.jetpackcomposeexecise.dishinventory.room.DishItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DailyDishViewModel @Inject constructor(
    private val dishRepository: DishRepository
): ViewModel() {
    //通过 Repository 获取数据库中的所有数据，并将其转化为Compose能观察的热流
    val allDishes: StateFlow<List<DishItem>> =
        dishRepository.allDishes.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}