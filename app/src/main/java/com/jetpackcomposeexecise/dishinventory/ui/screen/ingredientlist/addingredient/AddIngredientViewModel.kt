package com.jetpackcomposeexecise.dishinventory.ui.screen.ingredientlist.addingredient

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetpackcomposeexecise.dishinventory.data.local.entity.IngredientEntity
import com.jetpackcomposeexecise.dishinventory.data.local.repository.IngredientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddIngredientViewModel @Inject constructor(
    private val repository: IngredientRepository
) : ViewModel() {
    //--------- 业务数据 ---------
    private val _uiState = MutableStateFlow(AddIngredientUiState())
    val uiState: StateFlow<AddIngredientUiState> = _uiState.asStateFlow()

    //--------- 业务逻辑 ---------
    fun updateName(newName: String) { _uiState.update { it.copy(name = newName) } }
    fun updatePrice(newPrice: String) { _uiState.update { it.copy(price = newPrice) } }
    fun updateType(newType: String) { _uiState.update { it.copy(type = newType) } }
    fun updateMedicine(newMedicine: String) { _uiState.update { it.copy(medicine = newMedicine) } }
    fun updateWomanPeriod(newWomanPeriod: String) { _uiState.update { it.copy(womanPeriod = newWomanPeriod) } }

    //保存回调
    fun addIngredientItem(onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.insert(
                IngredientEntity(
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
