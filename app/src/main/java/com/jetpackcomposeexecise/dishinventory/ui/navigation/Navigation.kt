package com.jetpackcomposeexecise.dishinventory.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
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
import com.jetpackcomposeexecise.dishinventory.ui.screen.adddailydish.AddDailyDish
import com.jetpackcomposeexecise.dishinventory.ui.screen.adddailydish.AddDailyDishViewModel
import com.jetpackcomposeexecise.dishinventory.ui.screen.addoreditdish.AddDishScreen
import com.jetpackcomposeexecise.dishinventory.ui.screen.adddish.AddDishViewModel
import com.jetpackcomposeexecise.dishinventory.ui.screen.dailydish.DailyDishScreen
import com.jetpackcomposeexecise.dishinventory.ui.screen.dailydish.DailyDishViewModel
import com.jetpackcomposeexecise.dishinventory.ui.screen.dishdetails.DishDetailsScreen
import com.jetpackcomposeexecise.dishinventory.ui.screen.dishdetails.DishDetailsViewModel
import com.jetpackcomposeexecise.dishinventory.ui.screen.dishlist.DishListScreen
import com.jetpackcomposeexecise.dishinventory.ui.screen.dishlist.DishListViewModel
import com.jetpackcomposeexecise.dishinventory.ui.screen.addoreditdish.EditDishScreen
import com.jetpackcomposeexecise.dishinventory.ui.screen.editdish.EditDishViewModel
import com.jetpackcomposeexecise.dishinventory.ui.screen.profile.ProfileScreen
import kotlinx.serialization.Serializable

//1.定义页面 Route
@Serializable
object DailyDishesRoute

@Serializable
object DishListRoute

@Serializable
object ProfileRoute

@Serializable
object AddDishRoute

@Serializable
data class AddDailyDishRoute(val mealDate: String)

@Serializable
data class EditDishRoute(val dishId: Long)

@Serializable
data class DishDetailsRoute(val dishId: Long)

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
    //创建底部bottom
    val bottomTabItems = listOf(
        BottomTabItem(stringResource(R.string.bottom_dailydish), Icons.Default.Restaurant, DailyDishesRoute),
        BottomTabItem(stringResource(R.string.bottom_dishes), Icons.Default.MenuBook, DishListRoute),
        BottomTabItem(stringResource(R.string.bottom_ingredient), Icons.Default.ShoppingBasket, ProfileRoute)
    )
    //创建 NavController
    val navController = rememberNavController()
    //获取当前页面信息
    val navBackStackEntry by navController.currentBackStackEntryAsState()//监听当前界面
    val currentDestination = navBackStackEntry?.destination //获取当前页面所有信息
    //当前页面是否显示BottomBar
    val showBottomBar =
        currentDestination?.hasRoute(DailyDishesRoute::class) ?: false ||
                currentDestination?.hasRoute(DishListRoute::class) ?: false ||
                currentDestination?.hasRoute(ProfileRoute::class) ?: false
    //当前页面是否可以返回
    //val canNavigateBack = navController.previousBackStackEntry != null

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) { //是否显示BottomBar
                NavigationBar {
                    bottomTabItems.forEach { item ->
                        val isSelected =  //当前tab是否被选中
                            currentDestination?.hasRoute(item.route::class) ?: false
                        NavigationBarItem( //定义BottomBar中的每个Tab
                            selected = isSelected,
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(text = item.title) },
                            onClick = { //点击后跳转
                                navController.navigate(item.route) {
                                    //配置1：清空返回栈起始页外的界面，防止堆叠
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true //保存当前页状态
                                    }
                                    launchSingleTop = true //配置2：防止重复点击
                                    restoreState = true //配置3：从其他页返回后恢复状态
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
            //Home界面：DailyDishes
            composable<DailyDishesRoute> {
                val viewModel: DailyDishViewModel = hiltViewModel()
                DailyDishScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding()),
                    viewModel = viewModel,
                    onNaviToAddDailyDishScreen = { mealDate ->
                         navController.navigate(AddDailyDishRoute(mealDate)) },
                    onNaviToDishDetailsScreen = { dishId ->
                        navController.navigate(DishDetailsRoute(dishId))
                    }
                )
            }
            //Dishes界面：DishList
            composable<DishListRoute> {
                val viewModel: DishListViewModel = hiltViewModel()
                DishListScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding()),
                    viewModel = viewModel,
                    onNaviToAddDishScreen = { navController.navigate(AddDishRoute) },
                    onNaviToDishDetailsScreen = { dishId ->
                        navController.navigate(DishDetailsRoute(dishId))
                    }
                )
            }
            //Profile界面：Profile
            composable<ProfileRoute> {
                ProfileScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding())
                )
            }
            // 新增：AddDailyDish 界面定义
            composable<AddDailyDishRoute> {
                AddDailyDish(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding()),
                    navigateUp = { navController.navigateUp() },
                )
            }

// ... 其他 composable 定义
            //AddDish界面：AddDish
            composable<AddDishRoute> {
                val viewModel: AddDishViewModel = hiltViewModel()

                AddDishScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    viewModel = viewModel,
                    navigateUp = { navController.navigateUp() },
                    onSaveBtnClick = { viewModel::addDishItem { navController.popBackStack() } }
                )
            }
            //DishDetails界面：DishDetails
            composable<DishDetailsRoute> {
                val viewModel: DishDetailsViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val dishId = viewModel.dishId

                DishDetailsScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    uiState = uiState,
                    dishId = dishId,
                    navigateUp = { navController.navigateUp() },
                    onNavToItemEditScreen = { dishId ->
                        navController.navigate(EditDishRoute(dishId))
                    },
                    onDeleteBtnClick = {
                        viewModel::deleteDish{ navController.popBackStack() }
                    }
                )
            }
            //EditDish界面：EditDish
            composable<EditDishRoute> {
                val viewModel: EditDishViewModel = hiltViewModel()
                EditDishScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    viewModel = viewModel,
                    navigateUp = { navController.navigateUp() },
                    onSaveBtnClick = { viewModel::updateDishItem { navController.popBackStack() } }
                )
            }
        }
    }

}