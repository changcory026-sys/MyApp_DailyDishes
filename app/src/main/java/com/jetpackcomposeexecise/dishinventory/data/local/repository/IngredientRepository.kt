package com.jetpackcomposeexecise.dishinventory.data.local.repository

import com.jetpackcomposeexecise.dishinventory.data.local.dao.IngredientDao
import com.jetpackcomposeexecise.dishinventory.data.local.entity.IngredientEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IngredientRepository @Inject constructor(
    private val ingredientDao: IngredientDao
) {
    // 获取所有食材
    val allIngredients: Flow<List<IngredientEntity>> = ingredientDao.getAllIngredients()

    // 获取指定 ID 的食材
    fun getIngredientById(id: Long): Flow<IngredientEntity?> =
        ingredientDao.getIngredientById(id)

    // 插入食材
    suspend fun insert(ingredient: IngredientEntity) {
        ingredientDao.insert(ingredient)
    }

    // 删除食材
    suspend fun delete(ingredient: IngredientEntity) {
        ingredientDao.delete(ingredient)
    }

    // 更新食材
    suspend fun update(ingredient: IngredientEntity) {
        ingredientDao.update(ingredient)
    }
}
