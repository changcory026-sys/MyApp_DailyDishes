package com.jetpackcomposeexecise.dishinventory.data.local.repository

import com.jetpackcomposeexecise.dishinventory.data.local.dao.DishDao
import com.jetpackcomposeexecise.dishinventory.data.local.entity.DishEntity
import com.jetpackcomposeexecise.dishinventory.data.local.entity.DishIngredientCrossRef
import com.jetpackcomposeexecise.dishinventory.data.local.entity.DishWithIngredients
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DishRepository @Inject constructor(
    private val dishDao: DishDao
){
    //获取所有菜式
    val allDishes: Flow<List<DishEntity>> = dishDao.getAllDishes()

    //获取指定id的菜式
    fun getDishById(id: Long): Flow<DishEntity?> =
        dishDao.getDishById(id)

    //插入新菜式
    suspend fun insert(dishItem: DishEntity){
        dishDao.insert(dishItem)
    }

    //插入新菜式并返回 ID
    suspend fun insertAndGetId(dishItem: DishEntity): Long {
        return dishDao.insert(dishItem)
    }

    //删除菜式
    suspend fun delete(dishItem: DishEntity){
        dishDao.delete(dishItem)
    }
    //更新菜式
    suspend fun update(dishItem: DishEntity){
        dishDao.update(dishItem)
    }

    // --- 多对多：Dish 和 Ingredient 关联操作 ---

    // 获取指定菜品及其包含的所有食材
    fun getDishWithIngredients(dishId: Long): Flow<DishWithIngredients?> =
        dishDao.getDishWithIngredients(dishId)

    // 同步菜品的所有食材关联
    suspend fun syncIngredientsForDish(dishId: Long, ingredientIds: List<Long>) {
        dishDao.syncIngredients(dishId, ingredientIds)
    }

    // 组合操作：更新菜品并同步食材
    suspend fun updateDishAndIngredients(dish: DishEntity, ingredientIds: List<Long>) {
        dishDao.update(dish)
        dishDao.syncIngredients(dish.dishId, ingredientIds)
    }

    // 给指定的 dish 绑定一个指定的 ingredient
    suspend fun addIngredientToDish(dishId: Long, ingredientId: Long) {
        dishDao.insertDishIngredientCrossRef(DishIngredientCrossRef(dishId, ingredientId))
    }

    // 删除指定 dish 下指定的 ingredient (解除绑定)
    suspend fun removeIngredientFromDish(dishId: Long, ingredientId: Long) {
        dishDao.removeIngredientFromDish(dishId, ingredientId)
    }
}
