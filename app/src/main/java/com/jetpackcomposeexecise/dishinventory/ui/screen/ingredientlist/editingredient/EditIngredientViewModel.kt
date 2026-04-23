package com.jetpackcomposeexecise.dishinventory.ui.screen.ingredientlist.editingredient

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetpackcomposeexecise.dishinventory.data.local.entity.IngredientEntity
import com.jetpackcomposeexecise.dishinventory.data.local.repository.IngredientRepository
import com.jetpackcomposeexecise.dishinventory.ui.screen.ingredientlist.addingredient.AddIngredientUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditIngredientViewModel @Inject constructor(
    private val ingredientRepository: IngredientRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // 1. 取出传递的数据
    private val ingredientId: Long = checkNotNull(savedStateHandle["ingredientId"])

    // 2. 定义 uiState
    private val _uiState = MutableStateFlow(AddIngredientUiState())
    val uiState: StateFlow<AddIngredientUiState> = _uiState.asStateFlow()

    // 3. 初始化 uiState
    init {
        viewModelScope.launch {
            val initialIngredient: IngredientEntity? = ingredientRepository.getIngredientById(ingredientId).firstOrNull()
            initialIngredient?.let { ingredient ->
                _uiState.update {
                    it.copy(
                        name = ingredient.name,
                        price = ingredient.price.toString(),
                        type = ingredient.type,
                        medicine = ingredient.medicine,
                        womanPeriod = ingredient.womanPeriod
                    )
                }
            }
        }
    }

    // 4. 业务逻辑
    fun updateName(newName: String) { _uiState.update { it.copy(name = newName) } }
    fun updatePrice(newPrice: String) { _uiState.update { it.copy(price = newPrice) } }
    fun updateType(newType: String) { _uiState.update { it.copy(type = newType) } }
    fun updateMedicine(newMedicine: String) { _uiState.update { it.copy(medicine = newMedicine) } }
    fun updateWomanPeriod(newWomanPeriod: String) { _uiState.update { it.copy(womanPeriod = newWomanPeriod) } }

    // 保存修改
    fun updateIngredientItem(onSuccess: () -> Unit) {
        viewModelScope.launch {
            ingredientRepository.update(
                IngredientEntity(
                    ingredientId = ingredientId,
                    name = _uiState.value.name,
                    price = _uiState.value.price.toDoubleOrNull() ?: 0.0,
                    type = _uiState.value.type,
                    medicine = _uiState.value.medicine,
                    womanPeriod = _uiState.value.womanPeriod
                )
            )
            onSuccess()
        }
    }
}
