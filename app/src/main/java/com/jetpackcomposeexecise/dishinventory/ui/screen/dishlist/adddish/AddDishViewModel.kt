package com.jetpackcomposeexecise.dishinventory.ui.screen.dishlist.adddish

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetpackcomposeexecise.dishinventory.data.local.entity.DishEntity
import com.jetpackcomposeexecise.dishinventory.data.local.entity.IngredientEntity
import com.jetpackcomposeexecise.dishinventory.data.local.repository.DishRepository
import com.jetpackcomposeexecise.dishinventory.data.local.repository.IngredientRepository
import com.jetpackcomposeexecise.dishinventory.ui.screen.dishlist.addoreditdish.AddOrEditDishUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddDishViewModel @Inject constructor(
    private val repository: DishRepository,
    private val ingredientRepository: IngredientRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddOrEditDishUiState())
    val uiState: StateFlow<AddOrEditDishUiState> = _uiState.asStateFlow()

    val allIngredients: StateFlow<List<IngredientEntity>> = ingredientRepository.allIngredients
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateName(newName: String) { _uiState.update { it.copy(name = newName) } }
    fun updatePrice(newPrice: String) { _uiState.update { it.copy(time = newPrice) } }
    fun updateType(newType: String) { _uiState.update { it.copy(type = newType) } }
    fun updateMedicine(newMedicine: String) { _uiState.update { it.copy(medicine = newMedicine) } }
    fun updateWomanPeriod(newWomanPeriod: String) { _uiState.update { it.copy(womanPeriod = newWomanPeriod) } }

    fun onIngredientSelected(ingredient: IngredientEntity) {
        _uiState.update { state ->
            val newList = state.selectedIngredients.toMutableList()
            if (!newList.contains(ingredient)) {
                newList.add(ingredient)
            }
            state.copy(selectedIngredients = newList)
        }
    }

    // 👈 修正：按索引更新/替换指定食材，确保处理重复食材时不崩溃
    fun updateIngredient(index: Int, newIngredient: IngredientEntity) {
        _uiState.update { state ->
            val newList = state.selectedIngredients.toMutableList()
            if (index in newList.indices) {
                newList[index] = newIngredient
            }
            state.copy(selectedIngredients = newList)
        }
    }

    fun removeIngredient(ingredient: IngredientEntity) {
        _uiState.update { state ->
            val newList = state.selectedIngredients.toMutableList()
            newList.remove(ingredient)
            state.copy(selectedIngredients = newList)
        }
    }

    fun addDishItem(onSuccess: () -> Unit){
        viewModelScope.launch {
            val dishId = repository.insertAndGetId(
                DishEntity(
                    name = _uiState.value.name,
                    time = _uiState.value.time,
                    type = _uiState.value.type,
                    medicine = _uiState.value.medicine,
                    womanPeriod = _uiState.value.womanPeriod
                )
            )
            // 保存时去重，确保数据库关联唯一
            _uiState.value.selectedIngredients.distinctBy { it.ingredientId }.forEach { ingredient ->
                repository.addIngredientToDish(dishId, ingredient.ingredientId)
            }
            onSuccess()
        }
    }
}
