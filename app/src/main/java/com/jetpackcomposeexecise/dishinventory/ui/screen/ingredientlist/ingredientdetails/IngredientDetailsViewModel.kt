package com.jetpackcomposeexecise.dishinventory.ui.screen.ingredientlist.ingredientdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetpackcomposeexecise.dishinventory.data.local.repository.IngredientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IngredientDetailsViewModel @Inject constructor(
    private val ingredientRepository: IngredientRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // 1. 取出传递的食材 ID
    val ingredientId: Long = checkNotNull(savedStateHandle["ingredientId"])

    // 2. 定义 UI 状态
    val uiState = ingredientRepository.getIngredientById(ingredientId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // 3. 业务逻辑：删除食材
    fun deleteIngredient(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val currentIngredient = ingredientRepository.getIngredientById(ingredientId).firstOrNull()
            if (currentIngredient != null) {
                ingredientRepository.delete(currentIngredient)
                onSuccess()
            }
        }
    }
}
