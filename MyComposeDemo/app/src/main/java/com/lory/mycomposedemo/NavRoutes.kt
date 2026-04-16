package com.lory.mycomposedemo

object NavRoutes {
    const val HOME = "home"
    const val MODIFIER = "modifier"
    const val BASIC = "basic"
    const val LAYOUT = "layout"
    const val LIST = "list"
    const val STATE = "state"
    const val RENDER = "render"
    const val ANIMATION = "animation"
    const val GESTURE = "gesture"
    const val NAVIGATION = "navigation"
    const val THIRD_PARTY = "third_party"
}

data class DemoItem(
    val route: String,
    val title: String,
    val description: String,
    val emoji: String
)

val demoItems = listOf(
    DemoItem(NavRoutes.MODIFIER, "Modifier", "尺寸、间距、背景、边框、点击等修饰符详解", "🎨"),
    DemoItem(NavRoutes.BASIC, "基础组件", "Text、Button、Image、TextField、Switch 等基础 UI 组件", "🧱"),
    DemoItem(NavRoutes.LAYOUT, "常用布局", "Column、Row、Box、ConstraintLayout 等布局容器", "📐"),
    DemoItem(NavRoutes.LIST, "列表", "LazyColumn、LazyRow、LazyGrid 高性能列表", "📋"),
    DemoItem(NavRoutes.STATE, "状态管理与重组", "remember、State、ViewModel、派生状态与副作用", "🔄"),
    DemoItem(NavRoutes.RENDER, "渲染流程讲解", "Composition → Layout → Drawing 三阶段原理", "⚙️"),
    DemoItem(NavRoutes.ANIMATION, "动画", "animateDpAsState、AnimatedVisibility、Transition 等动画 API", "✨"),
    DemoItem(NavRoutes.GESTURE, "手势处理", "点击、拖拽、缩放、滑动等手势交互", "👆"),
    DemoItem(NavRoutes.NAVIGATION, "页面导航", "NavController、参数传递、深链接、嵌套导航", "🗺️"),
    DemoItem(NavRoutes.THIRD_PARTY, "第三方组件库", "Accompanist、Lottie、Coil 常用库展示", "📦"),
)

