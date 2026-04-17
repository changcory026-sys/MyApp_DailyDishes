package com.jetpackcomposeexecise.dishinventory.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.jetpackcomposeexecise.dishinventory.room.DishItem
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
        onDayTimeChanged = viewModel::updateDayTime,
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
        onDayTimeChanged = viewModel::updateDayTime,
        onWomanPeriodChanged = viewModel::updateWomanPeriod,
    )
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
    onDayTimeChanged: (String) -> Unit,
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
            OutlinedTextField(//name
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
            OutlinedTextField( //time
                modifier = Modifier.fillMaxWidth(),
                value = uiState.time,
                onValueChange = {onPriceChanged(it) },
                label = {Text(text = stringResource(R.string.dish_price))},
                suffix = {Text(text = stringResource(R.string.price_suffix))},
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    // 获取焦点时的背景色
                    focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    // 未获取焦点时的背景色
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            ) //type
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.type,
                onValueChange = {onTypeChanged(it)},
                label = {Text(text = stringResource(R.string.dish_type))},
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
            ) //type
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.medicine,
                onValueChange = {onMedicineChanged(it)},
                label = {Text(text = stringResource(R.string.dish_medicine))},
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
            )//medicine
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.dayTime,
                onValueChange = {onDayTimeChanged(it)},
                label = {Text(text = stringResource(R.string.dish_day_time))},
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
            ) //dayTime
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.womanPeriod,
                onValueChange = {onWomanPeriodChanged(it)},
                label = {Text(text = stringResource(R.string.dish_womam_period))},
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    // 获取焦点时的背景色
                    focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    // 未获取焦点时的背景色
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                )
            ) //womamPeriod
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
            onDayTimeChanged = {  },
            onWomanPeriodChanged = {  },
        )
    }
}