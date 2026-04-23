package com.jetpackcomposeexecise.dishinventory.ui.screen.dishlist.addoreditdish

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jetpackcomposeexecise.dishinventory.R
import com.jetpackcomposeexecise.dishinventory.data.local.entity.DishEntity
import com.jetpackcomposeexecise.dishinventory.data.local.entity.IngredientEntity
import com.jetpackcomposeexecise.dishinventory.ui.screen.dishlist.adddish.AddDishViewModel
import com.jetpackcomposeexecise.dishinventory.ui.screen.dishlist.editdish.EditDishViewModel

//AddDish界面
@Composable
fun AddDishScreen(
    modifier: Modifier = Modifier,
    viewModel: AddDishViewModel = hiltViewModel(),
    navigateUp: () -> Unit,
    onSaveBtnClick: () -> Unit,
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allIngredients by viewModel.allIngredients.collectAsStateWithLifecycle()

    AddOrEditDishContent(
        modifier = modifier,
        title = stringResource(R.string.title_add_dish),
        navigateUp = navigateUp,
        uiState = uiState,
        allIngredients = allIngredients,
        onSaveBtnClick = onSaveBtnClick,
        onNameChanged = viewModel::updateName,
        onPriceChanged = viewModel::updatePrice,
        onTypeChanged = viewModel::updateType,
        onMedicineChanged = viewModel::updateMedicine,
        onWomanPeriodChanged = viewModel::updateWomanPeriod,
        onIngredientSelected = viewModel::onIngredientSelected,
        onIngredientRemoved = viewModel::removeIngredient,
        onIngredientUpdated = viewModel::updateIngredient
    )
}

//EditDish界面
@Composable
fun EditDishScreen(
    modifier: Modifier = Modifier,
    viewModel: EditDishViewModel = hiltViewModel(),
    navigateUp: () -> Unit,
    onSaveBtnClick: () -> Unit,
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allIngredients by viewModel.allIngredients.collectAsStateWithLifecycle()

    AddOrEditDishContent(
        modifier = modifier,
        title = stringResource(id = R.string.title_edit_dish),
        navigateUp = navigateUp,
        uiState = uiState,
        allIngredients = allIngredients,
        onSaveBtnClick = onSaveBtnClick,
        onNameChanged = viewModel::updateName,
        onPriceChanged = viewModel::updatePrice,
        onTypeChanged = viewModel::updateType,
        onMedicineChanged = viewModel::updateMedicine,
        onWomanPeriodChanged = viewModel::updateWomanPeriod,
        onIngredientSelected = viewModel::onIngredientSelected,
        onIngredientRemoved = viewModel::removeIngredient,
        onIngredientUpdated = viewModel::updateIngredient
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DishDropdownField(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (!readOnly) expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { if (!readOnly) ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = MaterialTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            modifier = Modifier
                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = !readOnly)
                .fillMaxWidth()
        )
        if (!readOnly) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

/**
 * 👈 新增：带搜索匹配功能的食材下拉框
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchableIngredientDropdown(
    label: String,
    allIngredients: List<IngredientEntity>,
    selectedIngredientName: String,
    onIngredientSelected: (IngredientEntity) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = ""
) {
    var expanded by remember { mutableStateOf(false) }
    // 维护输入框的搜索文本状态，当选中项改变时自动更新
    var searchText by remember(selectedIngredientName) { mutableStateOf(selectedIngredientName) }

    // 根据搜索文本过滤食材
    val filteredIngredients = remember(searchText, allIngredients) {
        if (searchText.isBlank() || searchText == selectedIngredientName) {
            allIngredients
        } else {
            allIngredients.filter { it.name.contains(searchText, ignoreCase = true) }
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = {
                searchText = it
                expanded = true // 输入时自动展开菜单
            },
            label = { Text(label) },
            placeholder = { if (placeholder.isNotEmpty()) Text(placeholder) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = MaterialTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            modifier = Modifier
                .menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { 
                expanded = false
                // 如果没有选中新项，且输入框被清空或修改，则重置为之前选中的名字
                if (searchText != selectedIngredientName) {
                    searchText = selectedIngredientName
                }
            }
        ) {
            if (filteredIngredients.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("没有匹配的食材") },
                    onClick = { expanded = false },
                    enabled = false
                )
            } else {
                filteredIngredients.forEach { ingredient ->
                    DropdownMenuItem(
                        text = { Text(ingredient.name) },
                        onClick = {
                            onIngredientSelected(ingredient)
                            searchText = ingredient.name
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

//通用界面
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrEditDishContent(
    modifier: Modifier = Modifier,
    title: String,
    navigateUp: () -> Unit,
    uiState: AddOrEditDishUiState,
    allIngredients: List<IngredientEntity>,
    onSaveBtnClick: () -> Unit,
    onNameChanged: (String) -> Unit,
    onPriceChanged: (String) -> Unit,
    onTypeChanged: (String) -> Unit,
    onMedicineChanged: (String) -> Unit,
    onWomanPeriodChanged: (String) -> Unit,
    onIngredientSelected: (IngredientEntity) -> Unit,
    onIngredientRemoved: (IngredientEntity) -> Unit,
    onIngredientUpdated: (Int, IngredientEntity) -> Unit
){
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    IconButton( onClick = navigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                }
            )
        }
    ){ innerPadding ->
        LazyColumn(
            modifier = modifier
                .padding(innerPadding)
                .padding(horizontal = dimensionResource(R.dimen.padding_medium))
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_large))
        ) {
            item { Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small))) }
            
            // 基础属性
            item {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.name,
                    onValueChange = onNameChanged,
                    label = {Text(text = stringResource(R.string.dish_name))},
                    shape = MaterialTheme.shapes.medium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )
            }
            
            item {
                DishDropdownField(
                    label = stringResource(R.string.dish_price),
                    options = DishEntity.timeOptions,
                    selectedOption = uiState.time,
                    onOptionSelected = onPriceChanged,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            item {
                DishDropdownField(
                    label = stringResource(R.string.dish_type),
                    options = DishEntity.typeOptions,
                    selectedOption = uiState.type,
                    onOptionSelected = onTypeChanged,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            item {
                DishDropdownField(
                    label = stringResource(R.string.dish_medicine),
                    options = DishEntity.medicineOptions,
                    selectedOption = uiState.medicine,
                    onOptionSelected = onMedicineChanged,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            item {
                DishDropdownField(
                    label = stringResource(R.string.dish_womam_period),
                    options = DishEntity.womanPeriodOptions,
                    selectedOption = uiState.womanPeriod,
                    onOptionSelected = onWomanPeriodChanged,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 分割线
            item {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                Text(
                    text = "食材管理",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // 已选食材列表 (支持搜索匹配 👈)
            itemsIndexed(
                items = uiState.selectedIngredients,
                key = { index, item -> "${item.ingredientId}_$index" }
            ) { index, ingredient ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SearchableIngredientDropdown(
                        label = "已选食材",
                        allIngredients = allIngredients,
                        selectedIngredientName = ingredient.name,
                        onIngredientSelected = { newIng ->
                            onIngredientUpdated(index, newIng)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { onIngredientRemoved(ingredient) }) {
                        Icon(
                            imageVector = Icons.Default.RemoveCircleOutline,
                            contentDescription = "移除食材",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // 新增食材入口 (同样支持搜索匹配 👈)
            item {
                SearchableIngredientDropdown(
                    label = "点击选择食材",
                    allIngredients = allIngredients.filter { it !in uiState.selectedIngredients },
                    selectedIngredientName = "",
                    onIngredientSelected = onIngredientSelected,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Text(
                    text = stringResource(id = R.string.required_fields),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            item {
                Button(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    onClick = onSaveBtnClick,
                    enabled = uiState.isSaveEnabled,
                    shape = MaterialTheme.shapes.small,
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) { Text(text = stringResource(id = R.string.save_action)) }
            }
        }
    }
}
