package com.jetpackcomposeexecise.dishinventory.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "dish_table")
data class DishItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String = "",
    val time: Double = 0.0,
    val type: String = "小炒",
    val medicine: String = "温补", //药性：温补、寒凉…
    val dayTime: String = "中饭",
    val womanPeriod: String = "黄体期",
    )