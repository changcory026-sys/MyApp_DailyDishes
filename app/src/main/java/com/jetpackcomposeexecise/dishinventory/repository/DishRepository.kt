package com.jetpackcomposeexecise.dishinventory.repository

import com.jetpackcomposeexecise.dishinventory.room.DishDao
import com.jetpackcomposeexecise.dishinventory.room.DishItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DishRepository @Inject constructor(
    private val dishDao: DishDao
){
    //获取所有菜式
    val allDishes: Flow<List<DishItem>> = dishDao.getAllDishes()

    //获取指定id的菜式
    fun getDishById(id: Long): Flow<DishItem?> =
        dishDao.getDishById(id)

    //插入新菜式
    suspend fun insert(dishItem: DishItem){
        dishDao.insert(dishItem)
    }
    //删除菜式
    suspend fun delete(dishItem: DishItem){
        dishDao.delete(dishItem)
    }
    //更新菜式
    suspend fun update(dishItem: DishItem){
        dishDao.update(dishItem)
    }
}