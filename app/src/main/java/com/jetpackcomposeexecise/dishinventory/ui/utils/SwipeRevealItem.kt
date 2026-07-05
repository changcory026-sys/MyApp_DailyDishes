package com.jetpackcomposeexecise.dishinventory.ui.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

//功能：卡片双向滑动：未完成时，右滑直接标记完成并自动弹回；左滑呼出删除。已完成时，左滑取消完成并弹回。
@Composable
fun SwipeRevealItem(
    leftDragMax: Dp = 64.dp,      // 右滑的最大视觉偏移
    rightMenuWidth: Dp = 64.dp,   // 左滑菜单（删除按钮）的宽度
    isDone: Boolean = false,
    onDoneChanged: (Boolean) -> Unit,
    backgroundContent: @Composable (isSlidingLeft: Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val leftDragMaxPx = with(density) { leftDragMax.toPx() }
    val rightMenuWidthPx = with(density) { rightMenuWidth.toPx() }

    // 记录当前的水平滑动偏移量
    val offsetX = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    // 监听完成状态：如果变为未完成，且当前偏离，弹回 0
    LaunchedEffect(isDone) {
        if (!isDone && offsetX.value != 0f && offsetX.value > 0f) {
            offsetX.animateTo(0f)
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 1. 底层：固定的菜单区域
        Box(
            modifier = Modifier.matchParentSize()
        ) {
            // 右滑不显示任何背景图标，只有在非完成状态下左滑才显示删除背景
            backgroundContent(offsetX.value < -10f && !isDone)
        }

        // 2. 上层：可滑动的卡片
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), 0) } // 根据计算出的偏移量移动
                .pointerInput(isDone) { // 当完成状态改变时，重新绑定手势，确保读取最新状态
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            coroutineScope.launch {
                                if (isDone) {
                                    // 处于已完成状态：左滑（负位移）超过一半则取消已完成，并自动弹回 0
                                    if (offsetX.value < -leftDragMaxPx / 2) {
                                        onDoneChanged(false)
                                    }
                                    offsetX.animateTo(0f)
                                } else {
                                    // 处于未完成状态：
                                    if (offsetX.value > leftDragMaxPx / 2) {
                                        // 右滑超过一半，标记为已完成，并自动弹回 0
                                        onDoneChanged(true)
                                        offsetX.animateTo(0f)
                                    } else if (offsetX.value < -rightMenuWidthPx / 2) {
                                        // 左滑超过一半，呼出删除菜单
                                        offsetX.animateTo(-rightMenuWidthPx)
                                    } else {
                                        offsetX.animateTo(0f)
                                    }
                                }
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            coroutineScope.launch {
                                val minLimit = -rightMenuWidthPx
                                val maxLimit = leftDragMaxPx

                                val newOffset = if (isDone) {
                                    // 已完成状态下，左滑取消已完成（限制在负向滑动，不显示删除菜单）
                                    (offsetX.value + dragAmount).coerceIn(-maxLimit, 0f)
                                } else {
                                    // 未完成状态下，可右滑至完成，或左滑呼出删除
                                    (offsetX.value + dragAmount).coerceIn(minLimit, maxLimit)
                                }
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
