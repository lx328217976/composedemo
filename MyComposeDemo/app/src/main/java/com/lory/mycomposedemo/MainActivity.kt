package com.lory.mycomposedemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lory.mycomposedemo.demos.*
import com.lory.mycomposedemo.ui.theme.MyComposeDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyComposeDemoTheme {
                AppNavHost()
            }
        }
    }
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = NavRoutes.HOME) {
        composable(NavRoutes.HOME) {
            HomeScreen(onNavigate = { navController.navigate(it) })
        }
        composable(NavRoutes.MODIFIER) {
            ModifierDemoScreen(onBack = { navController.popBackStack() })
        }
        composable(NavRoutes.BASIC) {
            BasicComponentsDemoScreen(onBack = { navController.popBackStack() })
        }
        composable(NavRoutes.LAYOUT) {
            LayoutDemoScreen(onBack = { navController.popBackStack() })
        }
        composable(NavRoutes.LIST) {
            ListDemoScreen(onBack = { navController.popBackStack() })
        }
        composable(NavRoutes.STATE) {
            StateDemoScreen(onBack = { navController.popBackStack() })
        }
        composable(NavRoutes.RENDER) {
            RenderDemoScreen(onBack = { navController.popBackStack() })
        }
        composable(NavRoutes.ANIMATION) {
            AnimationDemoScreen(onBack = { navController.popBackStack() })
        }
        composable(NavRoutes.GESTURE) {
            GestureDemoScreen(onBack = { navController.popBackStack() })
        }
        composable(NavRoutes.NAVIGATION) {
            NavigationDemoScreen(onBack = { navController.popBackStack() })
        }
        composable(NavRoutes.THIRD_PARTY) {
            ThirdPartyDemoScreen(onBack = { navController.popBackStack() })
        }
    }
}