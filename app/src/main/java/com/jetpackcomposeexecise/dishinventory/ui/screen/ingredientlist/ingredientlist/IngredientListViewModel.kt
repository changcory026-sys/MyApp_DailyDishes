package com.jetpackcomposeexecise.dishinventory.ui.screen.ingredientlist.ingredientlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetpackcomposeexecise.dishinventory.data.local.entity.IngredientEntity
import com.jetpackcomposeexecise.dishinventory.data.local.repository.IngredientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class IngredientListViewModel @Inject constructor(
    private val ingredientRepository: IngredientRepository
): ViewModel(){
    //通过 Repository 获取数据库中的所有数据，并将其转化为Compose能观察的热流
    val allIngredients: StateFlow<List<IngredientEntity>> =
        ingredientRepository.allIngredients.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}