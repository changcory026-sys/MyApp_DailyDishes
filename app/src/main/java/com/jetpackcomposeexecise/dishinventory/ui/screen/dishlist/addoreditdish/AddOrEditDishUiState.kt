package com.jetpackcomposeexecise.dishinventory.ui.screen.dishlist.addoreditdish

import com.jetpackcomposeexecise.dishinventory.data.local.entity.IngredientEntity

data class AddOrEditDishUiState(
    val name: String = "",
    val time: String = "10",
    val type: String = "小炒",
    val medicine: String = "温补", //药性：温补、寒凉…
    val womanPeriod: String = "全周期", //黄体期、卵泡期……
    val selectedIngredients: List<IngredientEntity> = emptyList() // 已选食材列表
){
    //附属属性：通过上面属性的值自动计算本属性的值
    val isSaveEnabled: Boolean
        get() = name.isNotEmpty() &&
                time.isNotEmpty()
}