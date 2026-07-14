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

    // 版本2 → 3：无schema变更，空迁移保持版本链完整
    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // no-op
        }
    }

    // 版本3 → 4：无schema变更
    private val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // no-op
        }
    }

    // 版本4 → 5：无schema变更
    private val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // no-op
        }
    }

    // 版本5 → 6：新增 ingredient_table 和 dish_ingredient_cross_ref 表
    private val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """CREATE TABLE IF NOT EXISTS ingredient_table (
                    ingredientId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    name TEXT NOT NULL DEFAULT '',
                    price REAL NOT NULL DEFAULT 0.0,
                    type TEXT NOT NULL DEFAULT '蔬菜',
                    medicine TEXT NOT NULL DEFAULT '碳水',
                    womanPeriod TEXT NOT NULL DEFAULT '黄体期'
                )"""
            )
            db.execSQL(
                """CREATE TABLE IF NOT EXISTS dish_ingredient_cross_ref (
                    dishId INTEGER NOT NULL,
                    ingredientId INTEGER NOT NULL,
                    PRIMARY KEY(dishId, ingredientId)
                )"""
            )
        }
    }

    // 版本6 → 7：meal_date_dish_cross_ref 新增 isCompleted 字段
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
            .addMigrations(
                MIGRATION_2_3,
                MIGRATION_3_4,
                MIGRATION_4_5,
                MIGRATION_5_6,
                MIGRATION_6_7
            )
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