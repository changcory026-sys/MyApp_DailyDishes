package com.jetpackcomposeexecise.dishinventory.ui.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
//功能：卡片向左滑动后显示操作按钮icon
@Composable
fun SwipeRevealItem(
    menuWidth: Dp = 64.dp, // 👈 核心参数：菜单的固定宽度
    backgroundContent: @Composable (isSliding: Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val menuWidthPx = with(density) { menuWidth.toPx() }

    // 记录当前的水平滑动偏移量
    val offsetX = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 1. 底层：固定的菜单区域
        Box(
            modifier = Modifier.matchParentSize(), // 撑满整个 Box 的高度
            contentAlignment = Alignment.CenterEnd // 靠右对齐
        ) {
            // 将是否正在滑动的状态传递给内部，用于处理圆角透色的透明度变化
            backgroundContent(offsetX.value < -10f)
        }

        // 2. 上层：可滑动的卡片
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), 0) } // 根据计算出的偏移量移动
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            // 松手时：如果滑过了一半，就完全展开；否则缩回去
                            coroutineScope.launch {
                                if (offsetX.value < -menuWidthPx / 2) {
                                    offsetX.animateTo(-menuWidthPx)
                                } else {
                                    offsetX.animateTo(0f)
                                }
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            coroutineScope.launch {
                                // 👈 最关键的一步：限制位移只能在 [-80dp, 0] 之间！
                                // 这保证了向左最多只能滑 80dp，向右滑不能超过初始位置。
                                val newOffset = (offsetX.value + dragAmount).coerceIn(-menuWidthPx, 0f)
                                offsetX.snapTo(newOffset)
                            }
                        }
                    )
                }
        ) {
            content()
        }
    }
}