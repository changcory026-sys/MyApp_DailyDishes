package com.jetpackcomposeexecise.dishinventory.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "dish_table")
data class DishEntity(
    @PrimaryKey(autoGenerate = true)
    val dishId: Long = 0L,
    val name: String = "",
    val time: String = "10",
    val type: String = "小炒",
    val medicine: String = "温补", //药性：温补、寒凉…
    val womanPeriod: String = "黄体期",
) {
    companion object {
        val typeOptions = listOf("大荤", "小炒", "烧菜","素菜", "汤羹", "凉菜", "蒸菜", "主食", "水产", "炸物", "面食", "甜点")
        val medicineOptions = listOf("平和", "温补", "寒凉", "清热去火", "滋阴")
        val womanPeriodOptions = listOf("全周期", "经期", "卵泡期", "排卵期", "黄体期")
        val timeOptions = listOf("5", "10", "15", "20", "25", "30", "40", "50", "60", "75", "90", "120")
    }
}
