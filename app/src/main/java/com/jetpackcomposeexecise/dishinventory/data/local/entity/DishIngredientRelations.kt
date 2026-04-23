package com.jetpackcomposeexecise.dishinventory.data.local.entity


import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation





//交叉引用表：记录哪天吃了哪道菜
@Entity(
    tableName = "dish_ingredient_cross_ref",
    primaryKeys = ["dishId", "ingredientId"] // 联合主键
)
data class DishIngredientCrossRef(
    val dishId: Long,
    val ingredientId: Long,
)

// 联合查询打包类
data class DishWithIngredients(
    @Embedded val dish: DishEntity,
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