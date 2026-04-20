package com.jetpackcomposeexecise.dishinventory.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation

//把交叉表和联合查询打包类放在这一个文件里
//因为它俩是强绑定的，总是成对出现

//交叉引用表：记录哪天吃了哪道菜
@Entity(
    tableName = "meal_date_dish_cross_ref",
    primaryKeys = ["mealDate", "dishId"]
)
data class MealDateDishCrossRef(
    val mealDate: String,
    val dishId: Long
)

//联合查询打包类（复合数据类）：用于把它们打包查询出来
data class MealDateWithDishes(
    @Embedded val mealDate: MealDateEntity, //变量名避开关键字，不要直接叫 date
    @Relation(
        parentColumn = "mealDate",
        entityColumn = "dishId",
        associateBy = Junction(MealDateDishCrossRef::class)
    )
    val dishes: List<DishEntity>
)