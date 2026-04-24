package com.jetpackcomposeexecise.dishinventory.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jetpackcomposeexecise.dishinventory.R
import com.jetpackcomposeexecise.dishinventory.ui.screen.dailydish.adddailydish.AddDailyDish
import com.jetpackcomposeexecise.dishinventory.ui.screen.dishlist.addoreditdish.AddDishScreen
import com.jetpackcomposeexecise.dishinventory.ui.screen.dishlist.adddish.AddDishViewModel
import com.jetpackcomposeexecise.dishinventory.ui.screen.dailydish.dailydish.DailyDishScreen
import com.jetpackcomposeexecise.dishinventory.ui.screen.dishlist.dishdetails.DishDetailsScreen
import com.jetpackcomposeexecise.dishinventory.ui.screen.dishlist.dishdetails.DishDetailsViewModel
import com.jetpackcomposeexecise.dishinventory.ui.screen.dishlist.dishlist.DishListScreen
import com.jetpackcomposeexecise.dishinventory.ui.screen.dishlist.addoreditdish.EditDishScreen
import com.jetpackcomposeexecise.dishinventory.ui.screen.dishlist.editdish.EditDishViewModel
import com.jetpackcomposeexecise.dishinventory.ui.screen.ingredientlist.addingredient.AddIngredientScreen
import com.jetpackcomposeexecise.dishinventory.ui.screen.ingredientlist.addingredient.AddIngredientViewModel
import com.jetpackcomposeexecise.dishinventory.ui.screen.ingredientlist.ingredientdetails.IngredientDetailsScreen
import com.jetpackcomposeexecise.dishinventory.ui.screen.ingredientlist.ingredientdetails.IngredientDetailsViewModel
import com.jetpackcomposeexecise.dishinventory.ui.screen.ingredientlist.ingredientlist.IngredientListScreen
import com.jetpackcomposeexecise.dishinventory.ui.screen.ingredientlist.editingredient.EditIngredientScreen
import com.jetpackcomposeexecise.dishinventory.ui.screen.ingredientlist.editingredient.EditIngredientViewModel
import com.jetpackcomposeexecise.dishinventory.ui.screen.dailydish.todayingredientlist.TodayIngredientListScreen // 👈 准备导入
import kotlinx.serialization.Serializable

//1.定义页面 Route
@Serializable
object DailyDishesRoute

@Serializable
object DishListRoute

@Serializable
object IngredientListRoute

@Serializable
object AddDishRoute

@Serializable
data class AddDailyDishRoute(val mealDate: String)

@Serializable
data class EditDishRoute(val dishId: Long)

@Serializable
data class DishDetailsRoute(val dishId: Long)

@Serializable
object AddIngredientRoute

@Serializable
data class EditIngredientRoute(val ingredientId: Long)

@Serializable
data class IngredientDetailsRoute(val ingredientId: Long)

@Serializable
data class TodayIngredientListRoute(val mealDate: String) // 👈 新增路由

//2. 定义主页底部Tab的数据结构
data class BottomTabItem<T : Any>(
    val title: String,
    val icon: ImageVector,
    val route: T
)

//3. 创建Navigation
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDailyDishApp() {
    val bottomTabItems = listOf(
        BottomTabItem(stringResource(R.string.bottom_dailydish), Icons.Default.Restaurant, DailyDishesRoute),
        BottomTabItem(stringResource(R.string.bottom_dishes), Icons.Default.MenuBook, DishListRoute),
        BottomTabItem(stringResource(R.string.bottom_ingredient), Icons.Default.ShoppingBasket, IngredientListRoute)
    )
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar =
        currentDestination?.hasRoute(DailyDishesRoute::class) ?: false ||
                currentDestination?.hasRoute(DishListRoute::class) ?: false ||
                currentDestination?.hasRoute(IngredientListRoute::class) ?: false

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomTabItems.forEach { item ->
                        val isSelected = currentDestination?.hasRoute(item.route::class) ?: false
                        NavigationBarItem(
                            selected = isSelected,
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(text = item.title) },
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = DailyDishesRoute
        ) {
            // DailyDishes
            composable<DailyDishesRoute> {
                DailyDishScreen(
                    modifier = Modifier.fillMaxSize().padding(bottom = innerPadding.calculateBottomPadding()),
                    onNaviToAddDailyDishScreen = { mealDate -> navController.navigate(AddDailyDishRoute(mealDate)) },
                    onNaviToDishDetailsScreen = { dishId -> navController.navigate(DishDetailsRoute(dishId)) },
                    onNaviToTodayIngredientListScreen = { mealDate -> navController.navigate(TodayIngredientListRoute(mealDate)) } // 👈 对接
                )
            }
            // DishList
            composable<DishListRoute> {
                DishListScreen(
                    modifier = Modifier.fillMaxSize().padding(bottom = innerPadding.calculateBottomPadding()),
                    onNaviToAddDishScreen = { navController.navigate(AddDishRoute) },
                    onNaviToDishDetailsScreen = { dishId -> navController.navigate(DishDetailsRoute(dishId)) }
                )
            }
            // IngredientList
            composable<IngredientListRoute> {
                IngredientListScreen(
                    modifier = Modifier.fillMaxSize().padding(bottom = innerPadding.calculateBottomPadding()),
                    onNaviToAddIngredientScreen = { navController.navigate(AddIngredientRoute) },
                    onNaviToIngredientDetailsScreen = { ingredientId -> navController.navigate(IngredientDetailsRoute(ingredientId)) }
                )
            }
            // AddDailyDish
            composable<AddDailyDishRoute> {
                AddDailyDish(
                    modifier = Modifier.fillMaxSize().padding(bottom = innerPadding.calculateBottomPadding()),
                    navigateUp = { navController.navigateUp() },
                )
            }
            // AddDish
            composable<AddDishRoute> {
                val viewModel: AddDishViewModel = hiltViewModel()
                AddDishScreen(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    viewModel = viewModel,
                    navigateUp = { navController.navigateUp() },
                    onSaveBtnClick = { viewModel.addDishItem { navController.popBackStack() } }
                )
            }
            // DishDetails
            composable<DishDetailsRoute> {
                val viewModel: DishDetailsViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                DishDetailsScreen(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    uiState = uiState,
                    dishId = viewModel.dishId,
                    navigateUp = { navController.navigateUp() },
                    onNavToItemEditScreen = { dishId -> navController.navigate(EditDishRoute(dishId)) },
                    onDeleteBtnClick = { viewModel.deleteDish { navController.popBackStack() } }
                )
            }
            // EditDish
            composable<EditDishRoute> {
                val viewModel: EditDishViewModel = hiltViewModel()
                EditDishScreen(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    viewModel = viewModel,
                    navigateUp = { navController.navigateUp() },
                    onSaveBtnClick = { viewModel.updateDishItem { navController.popBackStack() } }
                )
            }
            // AddIngredient
            composable<AddIngredientRoute> {
                val viewModel: AddIngredientViewModel = hiltViewModel()
                AddIngredientScreen(
                    modifier = Modifier.fillMaxSize(),
                    viewModel = viewModel,
                    navigateUp = { navController.navigateUp() },
                    onSaveSuccess = { navController.popBackStack() }
                )
            }
            // IngredientDetails
            composable<IngredientDetailsRoute> {
                val viewModel: IngredientDetailsViewModel = hiltViewModel()
                IngredientDetailsScreen(
                    modifier = Modifier.fillMaxSize(),
                    viewModel = viewModel,
                    navigateUp = { navController.navigateUp() },
                    onNavToIngredientEditScreen = { ingredientId ->
                        navController.navigate(EditIngredientRoute(ingredientId))
                    }
                )
            }
            // EditIngredient
            composable<EditIngredientRoute> {
                val viewModel: EditIngredientViewModel = hiltViewModel()
                EditIngredientScreen(
                    modifier = Modifier.fillMaxSize(),
                    viewModel = viewModel,
                    navigateUp = { navController.navigateUp() },
                    onSaveSuccess = { navController.popBackStack() }
                )
            }
            // 今日食材清单页 👈 新增
            composable<TodayIngredientListRoute> {
                TodayIngredientListScreen(
                    modifier = Modifier.fillMaxSize(),
                    navigateUp = { navController.navigateUp() }
                )
            }
        }
    }
}