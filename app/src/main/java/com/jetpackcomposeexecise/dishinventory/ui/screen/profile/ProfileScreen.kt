package com.jetpackcomposeexecise.dishinventory.ui.screen.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jetpackcomposeexecise.dishinventory.R
import com.jetpackcomposeexecise.dishinventory.ui.theme.DishInventoryTheme

@Composable
fun ProfileScreen(modifier: Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ){

        Icon(
            imageVector = Icons.Filled.PermIdentity,
            contentDescription = "null",
            modifier = Modifier.size(138.dp),
            tint = Color.LightGray,
        )
        Text(
            text = stringResource(R.string.profile_text),
            fontSize = 24.sp,
            modifier = Modifier.padding(start = 40.dp, end = 40.dp, bottom = 300.dp)
        )
    }
}

//UI测试
@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    DishInventoryTheme {
        ProfileScreen(modifier = Modifier.fillMaxSize())
    }
}