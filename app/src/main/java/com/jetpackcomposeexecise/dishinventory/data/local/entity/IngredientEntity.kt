package com.jetpackcomposeexecise.dishinventory.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "ingredient_table")
data class IngredientEntity(
    @PrimaryKey(autoGenerate = true)
    val ingredientId: Long = 0L,
    val name: String = "",
    val price: Double = 0.0,
    val type: String = "蔬菜",
    val medicine: String = "平和", //药性：温补、寒凉…
    val womanPeriod: String = "黄体期",
) {
    companion object {
        val typeOptions = listOf("肉类", "蔬菜", "蛋类", "菌菇", "河鲜", "海鲜", "豆制品", "水果", "饮料")
        val medicineOptions = listOf("平和", "温补", "寒凉", "清热去火", "滋阴")
        val womanPeriodOptions = listOf("全周期", "经期", "卵泡期", "排卵期", "黄体期")
    }
}