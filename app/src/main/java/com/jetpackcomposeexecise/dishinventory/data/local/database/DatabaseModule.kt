package com.jetpackcomposeexecise.dishinventory.data.local.database

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jetpackcomposeexecise.dishinventory.data.local.dao.IngredientDao
import com.jetpackcomposeexecise.dishinventory.data.local.dao.MealDateDao
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

    private val MIGRATION_6_7 = object : Migration(6, 7) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE meal_date_dish_cross_ref ADD COLUMN isCompleted INTEGER NOT NULL DEFAULT 0")
        }
    }

    //构建数据库
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "dish_database")
            .addMigrations(MIGRATION_6_7)
            .build()
    }
    //构建DAO
    @Provides
    fun provideDishDao(database: AppDatabase) = database.DishDao()
    //教 Hilt 如何提供 MealDateDao
    @Provides
    fun provideMealDateDao(database: AppDatabase): MealDateDao {
        return database.mealDateDao()
    }

    @Provides
    fun provideIngredientDao(database: AppDatabase): IngredientDao {
        return database.ingredientDao()
    }
}