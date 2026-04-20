package com.jetpackcomposeexecise.dishinventory.ui.screen.adddailydish

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.jetpackcomposeexecise.dishinventory.R
import com.jetpackcomposeexecise.dishinventory.data.local.entity.DishEntity
import com.jetpackcomposeexecise.dishinventory.ui.theme.DishInventoryTheme

//通用界面
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDailyDish(
    modifier: Modifier = Modifier,
    mealDate: String, //顶部的日期
    dishes: List<DishEntity>, //下拉框中显示的全部Dish：viewModel.dishes
    navigateUp: () -> Unit, //顶部返回icon的回调
    selectedDish: DishEntity?,//viewModel.selectedDish
    onDishSelected: (DishEntity) -> Unit, //下拉框中点击回调:viewModel.onDishSelected()
    onSaveBtnClick: () -> Unit, //【Save】按钮回调viewModel.onSaveBtnClick
) {
    var expanded by remember { mutableStateOf(false) } //下拉框状态

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(R.string.title_add_today_dish)) },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                }
            )
        }, //顶部标题
        bottomBar = {
            Button(
                onClick = onSaveBtnClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_medium)),
                enabled = selectedDish != null //选中了菜式才能点击【Save】
            ) { Text(stringResource(R.string.save_action)) }
        } //底部按钮
    ) { innerpadding ->
        Column(
            modifier = modifier
                .padding(innerpadding)
                .padding(dimensionResource(R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_large)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //顶部标题：日期
            Text(
                text = mealDate,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            //下拉框：选择菜单
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedDish?.name ?: "点击选择菜品",
                    onValueChange = {}, //不允许用户通过键盘打字来选菜，故输入回调为null
                    readOnly = true, // 禁止直接输入，只能选
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)},//下拉icon
                    modifier = Modifier
                        .menuAnchor(//设置菜单位置：顶部边缘对其、宽度对其下拉框
                            type = MenuAnchorType.PrimaryNotEditable,//纯点选，不可编辑
                            enabled = true //是否响应点击来弹出菜单
                        )
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    dishes.forEach { dish ->
                        DropdownMenuItem(
                            text = { Text(dish.name) },
                            onClick = {
                                onDishSelected(dish)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

//UI测试
@Preview(showBackground = true)
@Composable
fun AddDailyDishPreview() {
    DishInventoryTheme {
        val dishes = List(5){
            DishEntity( name = "黄焖鸡", time = 23.6)
        }
        AddDailyDish(
            modifier = Modifier.fillMaxSize(),
            mealDate = "2026-12-26",
            dishes = dishes,
            navigateUp = {  },
            selectedDish = DishEntity( name = "黄焖鸡", time = 23.6),
            onDishSelected = {  },
            onSaveBtnClick = {  },
        )
    }
}