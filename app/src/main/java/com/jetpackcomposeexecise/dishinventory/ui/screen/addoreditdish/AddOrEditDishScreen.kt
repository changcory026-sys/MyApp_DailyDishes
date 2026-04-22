package com.jetpackcomposeexecise.dishinventory.ui.screen.addoreditdish

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import com.jetpackcomposeexecise.dishinventory.data.local.entity.DishEntity
import com.jetpackcomposeexecise.dishinventory.ui.screen.adddish.AddDishViewModel
import com.jetpackcomposeexecise.dishinventory.ui.screen.editdish.EditDishViewModel
import com.jetpackcomposeexecise.dishinventory.ui.theme.DishInventoryTheme

//AddDish界面
@Composable
fun AddDishScreen(
    modifier: Modifier = Modifier,
    viewModel: AddDishViewModel = hiltViewModel(),
    navigateUp: () -> Unit, //顶部返回icon的回调
    onSaveBtnClick: () -> Unit, //【Save】按钮回调
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AddOrEditDishContent(
        modifier = modifier,
        title = stringResource(R.string.title_add_dish),
        navigateUp = navigateUp,
        uiState = uiState,
        onSaveBtnClick = onSaveBtnClick,
        onNameChanged = viewModel::updateName,
        onPriceChanged = viewModel::updatePrice,
        onTypeChanged = viewModel::updateType,
        onMedicineChanged = viewModel::updateMedicine,
        onWomanPeriodChanged = viewModel::updateWomanPeriod,
    )
}
//EditDish界面
@Composable
fun EditDishScreen(
    modifier: Modifier = Modifier,
    viewModel: EditDishViewModel = hiltViewModel(),
    navigateUp: () -> Unit, //顶部返回icon的回调
    onSaveBtnClick: () -> Unit, //【Save】按钮回调
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AddOrEditDishContent(
        modifier = modifier,
        title = stringResource(id = R.string.title_edit_dish),
        navigateUp = navigateUp,
        uiState = uiState,
        onSaveBtnClick = onSaveBtnClick,
        onNameChanged = viewModel::updateName,
        onPriceChanged = viewModel::updatePrice,
        onTypeChanged = viewModel::updateType,
        onMedicineChanged = viewModel::updateMedicine,
        onWomanPeriodChanged = viewModel::updateWomanPeriod,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DishDropdownField(
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

//通用界面
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrEditDishContent(
    modifier: Modifier = Modifier,
    title: String, //顶部标题文本
    navigateUp: () -> Unit, //顶部返回icon的回调
    uiState: AddOrEditDishUiState,
    onSaveBtnClick: () -> Unit, //【Save】按钮回调
    onNameChanged: (String) -> Unit, //Name输入框的回调
    onPriceChanged: (String) -> Unit, //Price输入框的回调
    onTypeChanged: (String) -> Unit, //Type输入框的回调
    onMedicineChanged: (String) -> Unit, //Medicine输入框的回调
    onWomanPeriodChanged: (String) -> Unit,
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
    ){innerpadding ->
        Column(
            modifier = modifier
                .padding(innerpadding)
                .padding(
                    top = dimensionResource(R.dimen.padding_large),
                    start = dimensionResource(R.dimen.padding_medium),
                    end = dimensionResource(R.dimen.padding_medium)
                ),
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(R.dimen.padding_large)
            )
        ) {
            //name
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.name,
                onValueChange = { onNameChanged(it) },
                label = {Text(text = stringResource(R.string.dish_name))},
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    // 获取焦点时的背景色
                    focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    // 未获取焦点时的背景色
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )
            //costTime
            DishDropdownField(
                label = stringResource(R.string.dish_price),
                options = DishEntity.timeOptions,
                selectedOption = uiState.time,
                onOptionSelected = onPriceChanged,
                modifier = Modifier.fillMaxWidth()
            )
            //type
            DishDropdownField(
                label = stringResource(R.string.dish_type),
                options = DishEntity.typeOptions,
                selectedOption = uiState.type,
                onOptionSelected = onTypeChanged,
                modifier = Modifier.fillMaxWidth()
            )
            //medicine
            DishDropdownField(
                label = stringResource(R.string.dish_medicine),
                options = DishEntity.medicineOptions,
                selectedOption = uiState.medicine,
                onOptionSelected = onMedicineChanged,
                modifier = Modifier.fillMaxWidth()
            )
            //womamPeriod
            DishDropdownField(
                label = stringResource(R.string.dish_womam_period),
                options = DishEntity.womanPeriodOptions,
                selectedOption = uiState.womanPeriod,
                onOptionSelected = onWomanPeriodChanged,
                modifier = Modifier.fillMaxWidth()
            )
            //说明文本
            Text(text = stringResource(id = R.string.required_fields))
            Spacer(modifier = Modifier.weight(1.0f))
            Button(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                onClick = {
                    onSaveBtnClick()
                },
                enabled = uiState.isSaveEnabled,
                shape = MaterialTheme.shapes.small,
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {Text(text = stringResource(id = R.string.save_action)) }
        }
    }
}

//UI测试
@Preview(showBackground = true)
@Composable
fun AddOrEditDishContentPreview() {
    DishInventoryTheme {
        AddOrEditDishContent(
            modifier = Modifier.fillMaxSize(),
            title = "Test",
            navigateUp = {  },
            uiState = AddOrEditDishUiState(),
            onSaveBtnClick = {  },
            onNameChanged = {  },
            onPriceChanged = {  },
            onTypeChanged = {  },
            onMedicineChanged = {  },
            onWomanPeriodChanged = {  },
        )
    }
}
