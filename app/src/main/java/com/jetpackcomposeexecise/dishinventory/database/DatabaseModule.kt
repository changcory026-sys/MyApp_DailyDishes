package com.jetpackcomposeexecise.dishinventory.database

import android.content.Context
import androidx.room.Room
import com.jetpackcomposeexecise.dishinventory.room.DishesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//告诉 Hilt 如何构建数据库和 DAO
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    //构建数据库
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DishesDatabase {
        return Room.databaseBuilder(
            context,
            DishesDatabase::class.java,
            "fruit_database"
        ).build()
    }
    //构建DAO
    @Provides
    fun provideFruitDao(database: DishesDatabase) = database.DishDao()

}