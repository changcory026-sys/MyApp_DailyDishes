package com.jetpackcomposeexecise.dishinventory.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation

//交叉引用表：记录哪天吃了哪道菜
@Entity(
    tableName = "meal_date_dish_cross_ref",
    primaryKeys = ["mealDate", "dishId", "mealTime"] // 联合主键支持同菜不同餐
)
data class MealDateDishCrossRef(
    val mealDate: String,
    val dishId: Long,
    val mealTime: String = "中饭"
) {
    companion object {
        val mealTimeOptions = listOf("早饭", "中饭", "下午茶", "晚饭", "宵夜")
    }
}

// 包含用餐时段信息的包装类，用于 DailyDishScreen 展示
data class DishWithMealTime(
    @Embedded val dish: DishEntity,
    val mealTime: String
)

// 包含食材和用餐时段信息的包装类，用于今日食材清单页
data class DishWithIngredientsAndMealTime(
    @Embedded val dish: DishEntity,
    val mealTime: String,
    @Relation(
        entity = IngredientEntity::class,
        parentColumn = "dishId",
        entityColumn = "ingredientId",
        associateBy = Junction(
            value = DishIngredientCrossRef::class,
            parentColumn = "dishId",
            entityColumn = "ingredientId"
        )
    )
    val ingredients: List<IngredientEntity>
)

// 联合查询打包类
data class MealDateWithDishes(
    @Embedded val mealDate: MealDateEntity,
    @Relation(
        entity = DishEntity::class,
        parentColumn = "mealDate",
        entityColumn = "dishId",
        associateBy = Junction(
            value = MealDateDishCrossRef::class,
            parentColumn = "mealDate",
            entityColumn = "dishId"
        )
    )
    val dishes: List<DishEntity> // 这里的 Relation 只能拿到 Dish，拿不到 CrossRef 的字段
)

// 最终给 DailyDishScreen 使用的复合类
data class DailyDishRecord(
    val mealDate: String,
    val dishesWithTime: List<DishWithMealTime>
)