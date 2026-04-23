package com.jetpackcomposeexecise.dishinventory.ui.screen.ingredientlist.addingredient

data class AddIngredientUiState(
    val name: String = "",
    val price: String = "0",
    val type: String = "蔬菜",
    val medicine: String = "平和",
    val womanPeriod: String = "全周期"
) {
    val isSaveEnabled: Boolean
        get() = name.isNotEmpty()
}
