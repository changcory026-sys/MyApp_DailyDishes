package com.jetpackcomposeexecise.dishinventory.ui.screen.ingredientlist.addingredient

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jetpackcomposeexecise.dishinventory.R
import com.jetpackcomposeexecise.dishinventory.data.local.entity.IngredientEntity
import com.jetpackcomposeexecise.dishinventory.ui.screen.ingredientlist.editingredient.EditIngredientViewModel
import com.jetpackcomposeexecise.dishinventory.ui.theme.DishInventoryTheme

// 添加食材界面
@Composable
fun AddIngredientScreen(
    modifier: Modifier = Modifier,
    viewModel: AddIngredientViewModel = hiltViewModel(),
    navigateUp: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AddIngredientContent(
        modifier = modifier,
        title = stringResource(R.string.title_add_ingredient),
        navigateUp = navigateUp,
        uiState = uiState,
        onSaveBtnClick = { viewModel.addIngredientItem(onSaveSuccess) },
        onNameChanged = viewModel::updateName,
        onPriceChanged = viewModel::updatePrice,
        onTypeChanged = viewModel::updateType,
        onMedicineChanged = viewModel::updateMedicine,
        onWomanPeriodChanged = viewModel::updateWomanPeriod
    )
}

// 编辑食材界面
@Composable
fun EditIngredientScreen(
    modifier: Modifier = Modifier,
    viewModel: EditIngredientViewModel = hiltViewModel(),
    navigateUp: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AddIngredientContent(
        modifier = modifier,
        title = stringResource(R.string.title_edit_ingredient),
        navigateUp = navigateUp,
        uiState = uiState,
        onSaveBtnClick = { viewModel.updateIngredientItem(onSaveSuccess) },
        onNameChanged = viewModel::updateName,
        onPriceChanged = viewModel::updatePrice,
        onTypeChanged = viewModel::updateType,
        onMedicineChanged = viewModel::updateMedicine,
        onWomanPeriodChanged = viewModel::updateWomanPeriod
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IngredientDropdownField(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = MaterialTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            modifier = Modifier
                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth()
        )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIngredientContent(
    modifier: Modifier = Modifier,
    title: String,
    navigateUp: () -> Unit,
    uiState: AddIngredientUiState,
    onSaveBtnClick: () -> Unit,
    onNameChanged: (String) -> Unit,
    onPriceChanged: (String) -> Unit,
    onTypeChanged: (String) -> Unit,
    onMedicineChanged: (String) -> Unit,
    onWomanPeriodChanged: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(horizontal = dimensionResource(R.dimen.padding_medium))
                .verticalScroll(rememberScrollState()), // 👈 适配横屏滑动
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(R.dimen.padding_large)
            )
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
            
            // Name
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.name,
                onValueChange = onNameChanged,
                label = { Text(text = stringResource(R.string.ingredient_name)) },
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
            // Price
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.price,
                onValueChange = onPriceChanged,
                label = { Text(text = stringResource(R.string.ingredient_price)) },
                suffix = { Text(text = stringResource(R.string.price_suffix2)) },
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )
            // Type
            IngredientDropdownField(
                label = stringResource(R.string.ingredient_type),
                options = IngredientEntity.typeOptions,
                selectedOption = uiState.type,
                onOptionSelected = onTypeChanged,
                modifier = Modifier.fillMaxWidth()
            )
            // Medicine
            IngredientDropdownField(
                label = stringResource(R.string.ingredient_medicine),
                options = IngredientEntity.medicineOptions,
                selectedOption = uiState.medicine,
                onOptionSelected = onMedicineChanged,
                modifier = Modifier.fillMaxWidth()
            )
            // WomanPeriod
            IngredientDropdownField(
                label = stringResource(R.string.ingredient_womam_period),
                options = IngredientEntity.womanPeriodOptions,
                selectedOption = uiState.womanPeriod,
                onOptionSelected = onWomanPeriodChanged,
                modifier = Modifier.fillMaxWidth()
            )

            Text(text = stringResource(id = R.string.required_fields))
            
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                onClick = onSaveBtnClick,
                enabled = uiState.isSaveEnabled,
                shape = MaterialTheme.shapes.small,
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(text = stringResource(id = R.string.save_action))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddIngredientContentPreview() {
    DishInventoryTheme {
        AddIngredientContent(
            modifier = Modifier.fillMaxSize(),
            title = "Add Ingredient",
            navigateUp = {},
            uiState = AddIngredientUiState(),
            onSaveBtnClick = {},
            onNameChanged = {},
            onPriceChanged = {},
            onTypeChanged = {},
            onMedicineChanged = {},
            onWomanPeriodChanged = {}
        )
    }
}
