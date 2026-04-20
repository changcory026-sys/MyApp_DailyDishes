package com.jetpackcomposeexecise.dishinventory.data.local.repository

import com.jetpackcomposeexecise.dishinventory.data.local.dao.DishDao
import com.jetpackcomposeexecise.dishinventory.data.local.entity.DishEntity
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
    //删除菜式
    suspend fun delete(dishItem: DishEntity){
        dishDao.delete(dishItem)
    }
    //更新菜式
    suspend fun update(dishItem: DishEntity){
        dishDao.update(dishItem)
    }
}