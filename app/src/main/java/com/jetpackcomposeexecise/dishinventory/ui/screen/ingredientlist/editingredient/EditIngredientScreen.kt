package com.jetpackcomposeexecise.dishinventory.ui.screen.ingredientlist.editingredient

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jetpackcomposeexecise.dishinventory.R
import com.jetpackcomposeexecise.dishinventory.ui.screen.ingredientlist.addingredient.AddIngredientContent

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
