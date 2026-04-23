package com.jetpackcomposeexecise.dishinventory.ui.utils

/**
 * 获取食材类型的 Emoji
 */
fun getIngredientTypeEmoji(type: String): String = when (type) {
    "肉类" -> "🥩"
    "蔬菜" -> "🥬"
    "蛋类" -> "🥚"
    "菌菇" -> "🍄"
    "河鲜" -> "🐟"
    "海鲜" -> "🦀"
    "豆制品" -> "🥛"
    "水果" -> "🍎"
    "饮料" -> "🍹"
    else -> "📦"
}

/**
 * 获取菜品类型的 Emoji
 */
fun getDishTypeEmoji(type: String): String = when (type) {
    "大荤" -> "🍖"
    "小炒" -> "🍳"
    "烧菜" -> "🥘"
    "素菜" -> "🥬"
    "汤羹" -> "🥣"
    "凉菜" -> "🥗"
    "蒸菜" -> "🥟"
    "主食" -> "🍚"
    "水产" -> "🐟"
    "炸物" -> "🍗"
    "面食" -> "🍜"
    "甜点" -> "🍰"
    else -> "🍽️"
}

/**
 * 获取药性的 Emoji
 */
fun getMedicineEmoji(medicine: String): String = when (medicine) {
    "平和" -> "🟢"
    "温补" -> "🔴"
    "寒凉" -> "🔵"
    "清热去火" -> "❄️"
    "滋阴" -> "💧"
    else -> "✨"
}

/**
 * 获取女性周期的 Emoji
 */
fun getWomanPeriodEmoji(period: String): String = when (period) {
    "全周期" -> "♾️"
    "经期" -> "🩸"
    "卵泡期" -> "🌱"
    "排卵期" -> "✨"
    "黄体期" -> "🍂"
    else -> "📅"
}
