package com.jetpackcomposeexecise.dishinventory.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meal_dates")
data class MealDateEntity( // 完美避开 java.util.Date
    @PrimaryKey val mealDate: String //格式建议统一，如 "2026-04-17"
)



