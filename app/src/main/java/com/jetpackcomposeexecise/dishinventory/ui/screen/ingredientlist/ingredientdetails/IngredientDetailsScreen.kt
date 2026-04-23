package com.jetpackcomposeexecise.dishinventory.ui.screen.ingredientlist.ingredientdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jetpackcomposeexecise.dishinventory.R
import com.jetpackcomposeexecise.dishinventory.data.local.entity.IngredientEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: IngredientDetailsViewModel = hiltViewModel(),
    navigateUp: () -> Unit,
    onNavToIngredientEditScreen: (Long) -> Unit, // 👈 确保参数名与 Navigation.kt 一致
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val ingredientId = viewModel.ingredientId

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(id = R.string.title_ingredient_details)) }, // 👈 修正标题
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
                .padding(
                    bottom = dimensionResource(R.dimen.padding_large),
                    start = dimensionResource(R.dimen.padding_medium),
                    end = dimensionResource(R.dimen.padding_medium),
                ),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
        ) {
            if (uiState != null) {
                IngredientItemCard(uiState!!)
                Spacer(modifier = Modifier.weight(1.0f))
                
                // Edit 按钮
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onNavToIngredientEditScreen(ingredientId) }, // 👈 触发跳转
                    shape = MaterialTheme.shapes.small,
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(text = stringResource(id = R.string.edit_action))
                }
                
                // Delete 按钮
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.deleteIngredient(onSuccess = navigateUp) },
                    shape = MaterialTheme.shapes.small,
                ) {
                    Text(text = stringResource(id = R.string.delete))
                }
            } else {
                // 如果找不到数据
                Text(text = "未找到该食材的信息")
            }
        }
    }
}

@Composable
fun IngredientItemCard(uiState: IngredientEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            IngredientInfoRow(
                field = stringResource(id = R.string.ingredient_name),
                value = uiState.name,
                modifier = Modifier.fillMaxWidth()
            )
            IngredientInfoRow(
                field = stringResource(id = R.string.ingredient_price),
                value = uiState.price.toString() + stringResource(id = R.string.price_suffix2),
                modifier = Modifier.fillMaxWidth()
            )
            IngredientInfoRow(
                field = stringResource(id = R.string.ingredient_type),
                value = uiState.type,
                modifier = Modifier.fillMaxWidth()
            )
            IngredientInfoRow(
                field = stringResource(id = R.string.ingredient_medicine),
                value = uiState.medicine,
                modifier = Modifier.fillMaxWidth()
            )
            IngredientInfoRow(
                field = stringResource(id = R.string.ingredient_womam_period),
                value = uiState.womanPeriod,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun IngredientInfoRow(
    field: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = field)
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
