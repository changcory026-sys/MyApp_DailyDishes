package com.jetpackcomposeexecise.dishinventory.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DishItem::class], version = 1, exportSchema = false)
abstract class DishesDatabase : RoomDatabase() {
    abstract fun DishDao(): DishDao
}