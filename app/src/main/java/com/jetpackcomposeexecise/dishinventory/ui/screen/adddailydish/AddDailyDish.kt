package com.jetpackcomposeexecise.dishinventory.ui.screen.adddailydish

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jetpackcomposeexecise.dishinventory.R
import com.jetpackcomposeexecise.dishinventory.data.local.entity.DishEntity
@Composable
fun AddDailyDish(
    modifier: Modifier = Modifier,
    viewModel: AddDailyDishViewModel = hiltViewModel(),
    navigateUp: () -> Unit, //顶部返回icon的回调
){
    // 1. 从 ViewModel 中剥离出所有的状态流 (StateFlow)
    val inputRows by viewModel.inputRows.collectAsStateWithLifecycle()
    val allDishes by viewModel.allDishes.collectAsStateWithLifecycle()
    val isSaveEnabled by viewModel.isSaveEnabled.collectAsStateWithLifecycle()
    val mealDate = viewModel.date
    // 2. 将状态和 ViewModel 的方法映射给纯 UI 组件
    AddDailyDishScreen(
        modifier = modifier,
        mealDate = mealDate,
        inputRows = inputRows,
        allDishes = allDishes,
        isSaveEnabled = isSaveEnabled,
        navigateUp = navigateUp,
        onSearchTextChanged = viewModel::updateSearchText, // 👈 Kotlin 的方法引用语法，极其优雅
        onDishSelected = viewModel::onDishSelected,
        onSaveBtnClick = { viewModel.onSaveBtnClick(onComplete = navigateUp) },
    )
}

// ----- 1. 主界面部分 -----
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDailyDishScreen(
    modifier: Modifier = Modifier,
    mealDate: String, //顶部的日期
    inputRows: List<DishInputState>, // 传入行数据
    allDishes: List<DishEntity>,     // 传入全部菜品字典
    isSaveEnabled: Boolean,          // 传入按钮是否可用
    navigateUp: () -> Unit, //顶部返回icon的回调
    onSearchTextChanged: (rowId: String, text: String) -> Unit, // 👈 传递行ID和文字
    onDishSelected: (rowId: String, dish: DishEntity) -> Unit,  // 👈 传递行ID和选中的菜
    onSaveBtnClick: () -> Unit, //【Save】按钮回调viewModel.onSaveBtnClick
) {
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
                enabled = isSaveEnabled  //选中了菜式才能点击【Save】
            ) { Text(stringResource(R.string.save_action)) }
        } //底部按钮
    ) { innerpadding ->
        LazyColumn(
            modifier = modifier
                .padding(innerpadding)
                .padding(dimensionResource(R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_large)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //顶部标题：日期
            item {
                Text(
                    text = mealDate,
                    textAlign = TextAlign.Center,
                    //modifier = Modifier.fillMaxWidth().padding(vertical = dimensionResource(R.dimen.padding_large))
                )
            }
            //下拉框：选择菜单
            items(items = inputRows, key = { it.id }) { rowState ->
                DishInputItem(
                    state = rowState,
                    allDishes = allDishes,
                    // 将事件向上抛出
                    onSearchTextChanged = { text -> onSearchTextChanged(rowState.id, text) },
                    onDishSelected = { dish -> onDishSelected(rowState.id, dish) }
                )
            }
        }
    }
}

// ----- 2. 独立的单行下拉框子组件 -----
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DishInputItem(
    state: DishInputState, // 这一行的数据状态
    allDishes: List<DishEntity>, // 全部的菜
    onSearchTextChanged: (String) -> Unit, // 打字回调
    onDishSelected: (DishEntity) -> Unit // 选中回调
) {
    // 这两个纯 UI 状态属于当前这一行，所以放在子组件里！
    var expanded by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }

    // 每一行自己负责过滤自己搜索的结果
    val filteredDishes = remember(allDishes, state.searchText) {
        if (state.searchText.isBlank()) {
            allDishes
        } else {
            allDishes.filter { it.name.contains(state.searchText, ignoreCase = true) }
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = state.searchText,
            onValueChange = {
                onSearchTextChanged(it)
                expanded = true
            },
            readOnly = false,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            placeholder = {
                if (!isFocused && state.searchText.isEmpty()) {
                    Text(
                        text = "点击选择菜品",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            modifier = Modifier
                .menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true)
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (filteredDishes.isEmpty()) {
                DropdownMenuItem(
                    text = {
                        Text("没有找到包含“${state.searchText}”的菜品", color = MaterialTheme.colorScheme.primary)
                    },
                    onClick = { },
                    enabled = false
                )
            } else {
                filteredDishes.forEach { dish ->
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
//UI测试
@Preview(showBackground = true)
@Composable
fun AddDailyDishPreview() {
    // 构造一些假数据用于预览
    val mockDishes = listOf(
        DishEntity(dishId = 1, name = "红烧肉", time = 38.0),
        DishEntity(dishId = 2, name = "清炒土豆丝", time = 15.0),
        DishEntity(dishId = 3, name = "番茄炒蛋", time = 22.0)
    )

    // 模拟屏幕上有一行已经填好，一行是默认空行
    val mockInputRows = listOf(
        DishInputState(searchText = "红烧肉", selectedDish = mockDishes[0]),
        DishInputState()
    )

    MaterialTheme { // 记得包裹你的主题
        AddDailyDishScreen(
            mealDate = "2026-04-21",
            inputRows = mockInputRows,
            allDishes = mockDishes,
            isSaveEnabled = true,
            navigateUp = {},
            onSearchTextChanged = { _, _ -> },
            onDishSelected = { _, _ -> },
            onSaveBtnClick = {}
        )
    }
}