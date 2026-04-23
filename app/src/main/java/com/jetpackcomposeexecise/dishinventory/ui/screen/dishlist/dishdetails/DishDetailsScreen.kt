package com.jetpackcomposeexecise.dishinventory.ui.screen.dishlist.dishdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jetpackcomposeexecise.dishinventory.R
import com.jetpackcomposeexecise.dishinventory.data.local.entity.DishWithIngredients
import com.jetpackcomposeexecise.dishinventory.ui.theme.DishInventoryTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DishDetailsScreen(
    modifier: Modifier = Modifier,
    uiState: DishWithIngredients?,
    dishId: Long,
    navigateUp: () -> Unit,
    onNavToItemEditScreen: (Long) -> Unit,
    onDeleteBtnClick: () -> Unit,
){
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(id = R.string.title_dishes)) },
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
    ) { innerpadding ->
        Column(
            modifier = modifier
                .padding(innerpadding)
                .padding(
                    bottom = dimensionResource(R.dimen.padding_large),
                    start = dimensionResource(R.dimen.padding_medium),
                    end = dimensionResource(R.dimen.padding_medium),
                ),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
        ) {
            if (uiState != null){//确保数据库中有数据才显示
                //展示菜品及食材卡片
                DishItemCard(uiState)
                //占位控件
                Spacer(modifier = Modifier.weight(1.0f))
                //[Edit]按钮
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onNavToItemEditScreen(dishId) },
                    shape = MaterialTheme.shapes.small,
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {Text(text = stringResource(id = R.string.edit_action)) }
                //[Delete]按钮
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onDeleteBtnClick,
                    shape = MaterialTheme.shapes.small,
                ) {Text(text = stringResource(id = R.string.delete)) }
            }else{
                Text(text = stringResource(id = R.string.daily_dishes_default_text))
            }
        }
    }
}

@Composable
fun DishItemCard(uiState: DishWithIngredients) {
    val dish = uiState.dish
    val ingredients = uiState.ingredients

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
            DishInfoRow(
                dishField = stringResource(id = R.string.dish_name),
                dishFieldNum = dish.name,
                modifier = Modifier.fillMaxWidth()
            )
            DishInfoRow(
                dishField = stringResource(id = R.string.dish_price),
                dishFieldNum = dish.time + stringResource(id = R.string.price_suffix),
                modifier = Modifier.fillMaxWidth()
            )
            DishInfoRow(
                dishField = stringResource(id = R.string.dish_type),
                dishFieldNum = dish.type,
                modifier = Modifier.fillMaxWidth()
            )
            DishInfoRow(
                dishField = stringResource(id = R.string.dish_medicine),
                dishFieldNum = dish.medicine,
                modifier = Modifier.fillMaxWidth()
            )
            DishInfoRow(
                dishField = stringResource(id = R.string.dish_womam_period),
                dishFieldNum = dish.womanPeriod,
                modifier = Modifier.fillMaxWidth()
            )

            // 如果有食材，显示分割线和食材列表
            if (ingredients.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "包含食材",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                
                ingredients.forEach { ingredient ->
                    DishInfoRow(
                        dishField = "食材",
                        dishFieldNum = ingredient.name,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun DishInfoRow(
    dishField: String,
    dishFieldNum: String,
    modifier: Modifier = Modifier
){
    Row(//Item行
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(text = dishField)
        Text(
            text = dishFieldNum,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DishInfoRowPreview() {
    DishInventoryTheme {
        DishInfoRow("菜名", "Apple")
    }
}
