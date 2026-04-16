package com.lory.mycomposedemo.demos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lory.mycomposedemo.DemoBox
import com.lory.mycomposedemo.DemoScaffold
import com.lory.mycomposedemo.ExplainCard

@Composable
fun NavigationDemoScreen(onBack: () -> Unit) {
    DemoScaffold(title = "🗺️ 页面导航", onBack = onBack) {

        ExplainCard(
            title = "Navigation Compose 核心概念",
            content = "Navigation Compose 是官方导航框架，核心组件：\n" +
                    "• NavController：导航控制器，调用 navigate() / popBackStack() 控制跳转\n" +
                    "• NavHost：定义导航图，声明每个路由对应的 Composable\n" +
                    "• BackStack：回退栈，navigate 压栈，popBackStack 出栈\n\n" +
                    "路由（Route）就是字符串，类似 URL 路径。"
        )

        // 嵌套 NavController 演示
        ExplainCard(
            title = "⚠️ 演示说明",
            content = "下方是一个内嵌的小型导航示例，用 NavHost 嵌套在当前页面内，演示 Navigation 核心用法。" +
                    "真实项目中 NavHost 通常在 MainActivity 的顶层。"
        )

        // Mini Nav Demo
        DemoBox("NavHost / NavController 基础导航", code = """
// 1. 在顶层创建 NavController
val navController = rememberNavController()

// 2. NavHost 定义路由图
NavHost(navController = navController, startDestination = "home") {
    composable("home") {
        HomeScreen(
            onGoDetail = { id -> navController.navigate("detail/${'$'}id") }
        )
    }
    // 路径参数：{id}，通过 navArgument 声明类型
    composable(
        route = "detail/{id}",
        arguments = listOf(navArgument("id") { type = NavType.IntType })
    ) { backStackEntry ->
        val id = backStackEntry.arguments?.getInt("id") ?: 0
        DetailScreen(id = id, onBack = { navController.popBackStack() })
    }
}

// 3. 监听当前路由
val currentEntry by navController.currentBackStackEntryAsState()
val currentRoute = currentEntry?.destination?.route
        """.trimIndent()) {
            MiniNavDemo()
        }

        ExplainCard(
            title = "参数传递",
            content = "路由可以携带参数，格式类似 URL：\n" +
                    "• 路径参数：\"detail/{id}\"，通过 navArgument(\"id\") 声明类型\n" +
                    "• 查询参数：\"search?query={q}\"\n" +
                    "在目标 Composable 中通过 backStackEntry.arguments?.getString(\"id\") 获取参数。"
        )

        ExplainCard(
            title = "Bottom Navigation（底部导航栏）",
            content = "配合 NavigationBar + NavigationBarItem 组件：\n" +
                    "① 用 rememberNavController() 创建控制器\n" +
                    "② NavHost 定义各 Tab 对应的 composable\n" +
                    "③ 点击底部 Tab 时调用 navController.navigate(route) { launchSingleTop = true }\n" +
                    "④ 用 navController.currentBackStackEntryAsState() 获取当前路由，高亮对应 Tab"
        )

        DemoBox("Bottom Navigation 底部导航栏", code = """
val navController = rememberNavController()
val currentEntry by navController.currentBackStackEntryAsState()
val currentRoute = currentEntry?.destination?.route

Scaffold(
    bottomBar = {
        NavigationBar {
            tabs.forEach { tab ->
                NavigationBarItem(
                    selected = currentRoute == tab.route,
                    onClick = {
                        navController.navigate(tab.route) {
                            // 避免多次压栈：弹回到起始目的地后再导航
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true   // 保存被弹出页面的状态
                            }
                            launchSingleTop = true  // 避免重复创建同一目的地
                            restoreState = true     // 重新进入时恢复状态
                        }
                    },
                    label = { Text(tab.label) },
                    icon = { Icon(tab.icon, null) }
                )
            }
        }
    }
) { padding ->
    NavHost(navController, startDestination = tabs[0].route) {
        tabs.forEach { tab ->
            composable(tab.route) { tab.content() }
        }
    }
}
        """.trimIndent()) {
            BottomNavPreview()
        }

        ExplainCard(
            title = "深链接（Deep Link）",
            content = "Navigation Compose 支持 URI 形式的深链接，在 composable() 中添加：\n" +
                    "deepLinks = listOf(navDeepLink { uriPattern = \"myapp://detail/{id}\" })\n\n" +
                    "需要在 AndroidManifest 中添加对应的 intent-filter，即可从浏览器/通知等处直达对应页面。"
        )

        ExplainCard(
            title = "嵌套导航图（Nested NavGraph）",
            content = "可以将相关路由组织成嵌套导航图，类似模块化路由管理：\n" +
                    "navigation(startDestination = \"list\", route = \"products\") {\n" +
                    "    composable(\"list\") { ProductListScreen() }\n" +
                    "    composable(\"detail/{id}\") { DetailScreen() }\n" +
                    "}\n\n" +
                    "嵌套图有独立的 BackStack，navigate(\"products/list\") 进入整个子图。"
        )
    }
}

// ———— Mini 导航 Demo ————

private object MiniRoute {
    const val HOME = "mini_home"
    const val DETAIL = "mini_detail/{itemId}"
    fun detail(id: Int) = "mini_detail/$id"
}

@Composable
private fun MiniNavDemo() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Route indicator
            Box(
                Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primaryContainer).padding(8.dp)
            ) {
                Text("当前路由：$currentRoute", style = MaterialTheme.typography.labelSmall)
            }
            NavHost(
                navController = navController,
                startDestination = MiniRoute.HOME,
                modifier = Modifier.fillMaxWidth().height(200.dp)
            ) {
                composable(MiniRoute.HOME) {
                    MiniHomeScreen(onNavigate = { navController.navigate(MiniRoute.detail(it)) })
                }
                composable(
                    MiniRoute.DETAIL,
                    arguments = listOf(navArgument("itemId") { type = NavType.IntType })
                ) { entry ->
                    val id = entry.arguments?.getInt("itemId") ?: 0
                    MiniDetailScreen(id = id, onBack = { navController.popBackStack() })
                }
            }
        }
    }
}

@Composable
private fun MiniHomeScreen(onNavigate: (Int) -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("列表页（Home）", fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            (1..3).forEach { id ->
                Button(onClick = { onNavigate(id) }) { Text("详情 #$id") }
            }
        }
        Text("点击按钮导航到详情页，并传递参数 id", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun MiniDetailScreen(id: Int, onBack: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("详情页（Detail）", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("接收到的参数 id = $id", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(8.dp))
        Button(onClick = onBack) { Text("← 返回") }
        Text("popBackStack() 回退到上一个路由", style = MaterialTheme.typography.bodySmall)
    }
}

// ———— Bottom Nav Preview ————

private data class TabItem(val route: String, val label: String, val emoji: String)

private val tabs = listOf(
    TabItem("tab_home", "首页", "🏠"),
    TabItem("tab_search", "搜索", "🔍"),
    TabItem("tab_profile", "我的", "👤"),
)

@Composable
private fun BottomNavPreview() {
    val navController = rememberNavController()
    val currentEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentEntry?.destination?.route

    Card(modifier = Modifier.fillMaxWidth()) {
        Column {
            NavHost(
                navController = navController,
                startDestination = tabs[0].route,
                modifier = Modifier.fillMaxWidth().height(80.dp)
            ) {
                tabs.forEach { tab ->
                    composable(tab.route) {
                        Box(
                            Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${tab.emoji} ${tab.label} 页面内容", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
            NavigationBar {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = currentRoute == tab.route,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        label = { Text(tab.label) },
                        icon = { Text(tab.emoji) }
                    )
                }
            }
        }
    }
}

