package com.jetpackcomposeexecise.dishinventory.ui.screen.dailydish.generatemenu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jetpackcomposeexecise.dishinventory.R
import com.jetpackcomposeexecise.dishinventory.data.local.entity.DishEntity
import com.jetpackcomposeexecise.dishinventory.data.local.entity.MealDateDishCrossRef
import com.jetpackcomposeexecise.dishinventory.ui.utils.getDishTypeEmoji

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateMenuScreen(
    modifier: Modifier = Modifier,
    viewModel: GenerateMenuViewModel = hiltViewModel(),
    navigateUp: () -> Unit,
    onNavigateToRecommend: (date: String, time: String, configJson: String) -> Unit
) {
    val mealDate = viewModel.mealDate
    val selectedMealTime by viewModel.selectedMealTime.collectAsStateWithLifecycle()
    val inputRows by viewModel.inputRows.collectAsStateWithLifecycle()
    val dishCountsByType by viewModel.dishCountsByType.collectAsStateWithLifecycle()
    val isGenerateEnabled by viewModel.isGenerateEnabled.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("今天吃啥？") },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    onNavigateToRecommend(mealDate, selectedMealTime, viewModel.getGenerateConfigJson())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_medium)),
                enabled = isGenerateEnabled
            ) {
                Text("生成今日菜单")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "📅 $mealDate",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            item {
                MealTimeDropdown(
                    selectedTime = selectedMealTime,
                    onTimeSelected = viewModel::updateMealTime
                )
            }

            item { HorizontalDivider() }

            items(inputRows, key = { it.id }) { row ->
                GenerateRowItem(
                    state = row,
                    maxCount = dishCountsByType[row.type] ?: 0,
                    onTypeSelected = { type -> viewModel.updateRow(row.id, type = type) },
                    onCountSelected = { count -> viewModel.updateRow(row.id, count = count) },
                    onRemove = { viewModel.removeRow(row.id) },
                    isLastRow = row == inputRows.last()
                )
            }
            
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealTimeDropdown(
    selectedTime: String,
    onTimeSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = "---- $selectedTime ----",
            onValueChange = {},
            readOnly = true,
            label = { Text("选择用餐时段") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            MealDateDishCrossRef.mealTimeOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                    onClick = {
                        onTimeSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateRowItem(
    state: GenerateRowState,
    maxCount: Int,
    onTypeSelected: (String) -> Unit,
    onCountSelected: (Int) -> Unit,
    onRemove: () -> Unit,
    isLastRow: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 菜式类型选择 (改为不可输入 + 图标 👈)
        var typeExpanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = typeExpanded,
            onExpandedChange = { typeExpanded = it },
            modifier = Modifier.weight(0.55f)
        ) {
            OutlinedTextField(
                value = "${getDishTypeEmoji(state.type)} ${state.type}",
                onValueChange = {},
                readOnly = true,
                label = { Text("类型") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                modifier = Modifier
                    .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                DishEntity.typeOptions.forEach { type ->
                    DropdownMenuItem(
                        text = { Text("${getDishTypeEmoji(type)} $type") },
                        onClick = {
                            onTypeSelected(type)
                            typeExpanded = false
                        }
                    )
                }
            }
        }

        var countExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = countExpanded,
            onExpandedChange = { if (maxCount > 0) countExpanded = it },
            modifier = Modifier.weight(0.35f)
        ) {
            OutlinedTextField(
                value = if (state.count > 0) state.count.toString() else "",
                onValueChange = {},
                readOnly = true,
                label = { Text("数量") },
                placeholder = { Text("0") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = countExpanded) },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth(),
                enabled = maxCount > 0
            )
            ExposedDropdownMenu(expanded = countExpanded, onDismissRequest = { countExpanded = false }) {
                (1..maxCount).forEach { i ->
                    DropdownMenuItem(
                        text = { Text(i.toString()) },
                        onClick = {
                            onCountSelected(i)
                            countExpanded = false
                        }
                    )
                }
            }
        }

        if (!isLastRow) {
            IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                Icon(
                    imageVector = Icons.Default.RemoveCircleOutline,
                    contentDescription = "删除行",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        } else {
            Spacer(modifier = Modifier.size(24.dp))
        }
    }
}
