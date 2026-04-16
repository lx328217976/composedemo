package com.lory.mycomposedemo.demos

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.lory.mycomposedemo.DemoBox
import com.lory.mycomposedemo.DemoScaffold
import com.lory.mycomposedemo.ExplainCard

private object AnimCode {
    val animateAsState = """
// 用弹簧曲线做尺寸动画
var expanded by remember { mutableStateOf(false) }
val boxSize by animateDpAsState(
    targetValue = if (expanded) 120.dp else 60.dp,
    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
    label = "boxSize"
)

Box(Modifier.size(boxSize).background(Color.Purple))
Button(onClick = { expanded = !expanded }) { Text("切换") }

// 其他常用 animate*AsState：
// animateColorAsState / animateFloatAsState
// animateIntAsState / animateOffsetAsState
    """.trimIndent()

    val visibility = """
var visible by remember { mutableStateOf(true) }

AnimatedVisibility(
    visible = visible,
    enter = fadeIn() + expandVertically(),   // 组合进入动画
    exit  = fadeOut() + shrinkVertically()   // 组合退出动画
) {
    Box(Modifier.fillMaxWidth().height(60.dp).background(Color.Teal))
}

// 其他 enter/exit 动画：
// slideInVertically { -it }  从上方滑入
// slideInHorizontally { it } 从右方滑入
// expandHorizontally()       水平展开
    """.trimIndent()

    val animatedContent = """
var count by remember { mutableIntStateOf(0) }

AnimatedContent(
    targetState = count,
    transitionSpec = {
        // 数字增大时向上滑入/滑出
        if (targetState > initialState) {
            slideInVertically { -it } + fadeIn() togetherWith
            slideOutVertically { it } + fadeOut()
        } else {
            slideInVertically { it } + fadeIn() togetherWith
            slideOutVertically { -it } + fadeOut()
        }
    },
    label = "counter"
) { targetCount ->
    Text("${'$'}targetCount", style = MaterialTheme.typography.displaySmall)
}
    """.trimIndent()

    val contentSize = """
// 给容器加上 animateContentSize，内容大小变化时自动播放动画
var expanded by remember { mutableStateOf(false) }

Box(
    Modifier
        .fillMaxWidth()
        .animateContentSize(  // 关键修饰符
            animationSpec = spring(stiffness = Spring.StiffnessMedium)
        )
        .background(Color.Yellow, RoundedCornerShape(8.dp))
        .padding(12.dp)
) {
    Text(
        if (expanded) "完整内容..." else "点击展开..."
    )
}
Button(onClick = { expanded = !expanded }) { Text("展开/收起") }
    """.trimIndent()

    val infinite = """
// 无限循环动画
val infiniteTransition = rememberInfiniteTransition(label = "infinite")

// 旋转
val rotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing)),
    label = "rotation"
)

// 脉冲缩放（来回）
val scale by infiniteTransition.animateFloat(
    initialValue = 0.8f,
    targetValue = 1.2f,
    animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
    label = "scale"
)

Box(Modifier.size(60.dp).rotate(rotation).background(Color.Purple))
Box(Modifier.size(60.dp).scale(scale).background(Color.Red, CircleShape))
    """.trimIndent()

    val transition = """
// updateTransition：多属性协调动画，确保同步
var selected by remember { mutableStateOf(false) }
val transition = updateTransition(selected, label = "card")

val bgColor by transition.animateColor(label = "bg") { s ->
    if (s) Color.Blue else Color.LightGray
}
val scale by transition.animateFloat(label = "scale") { s ->
    if (s) 1.05f else 1f
}
val elevation by transition.animateDp(label = "elevation") { s ->
    if (s) 8.dp else 2.dp
}

Card(
    onClick = { selected = !selected },
    modifier = Modifier.graphicsLayer { scaleX = scale; scaleY = scale },
    colors = CardDefaults.cardColors(containerColor = bgColor),
    elevation = CardDefaults.cardElevation(defaultElevation = elevation)
) { ... }
    """.trimIndent()

    val crossfade = """
var currentTab by remember { mutableIntStateOf(0) }

// Crossfade：内容切换时淡入淡出
Crossfade(targetState = currentTab, label = "tab") { tab ->
    when (tab) {
        0 -> HomeContent()
        1 -> SearchContent()
        2 -> ProfileContent()
    }
}
    """.trimIndent()
}

@Composable
fun AnimationDemoScreen(onBack: () -> Unit) {
    DemoScaffold(title = "✨ 动画", onBack = onBack) {

        ExplainCard(
            title = "Compose 动画体系概览",
            content = "Compose 动画 API 分三个层次：\n" +
                    "• 高级 API（推荐）：AnimatedVisibility、AnimatedContent、animateContentSize、Crossfade\n" +
                    "• 基于值的 API：animateDpAsState、animateColorAsState、animateFloatAsState 等\n" +
                    "• 低级 API：Animatable、updateTransition、rememberInfiniteTransition\n\n" +
                    "大多数场景优先用高级 API，需要精细控制时使用低级 API。"
        )

        // animateDpAsState
        var expanded by remember { mutableStateOf(false) }
        val boxSize by animateDpAsState(
            targetValue = if (expanded) 120.dp else 60.dp,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
            label = "boxSize"
        )
        DemoBox("animateDpAsState — 弹性弹簧动画", code = AnimCode.animateAsState) {
            ExplainCard(
                title = "animateXxxAsState",
                content = "将状态变化转为动画值。只需改变目标值，Compose 自动补间动画。\n" +
                        "animationSpec 控制动画曲线：spring（弹簧）、tween（缓动）、keyframes（关键帧）等。"
            )
            Box(
                Modifier.size(boxSize).background(Color(0xFF7986CB), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) { Text("Box", color = Color.White) }
            Button(onClick = { expanded = !expanded }) { Text(if (expanded) "缩小" else "放大") }
        }

        // AnimatedVisibility
        var visible by remember { mutableStateOf(true) }
        DemoBox("AnimatedVisibility — 显示/隐藏动画", code = AnimCode.visibility) {
            ExplainCard(
                title = "AnimatedVisibility",
                content = "包裹内容，自动在显示/隐藏时播放进入/退出动画。\n" +
                        "enter/exit 参数可组合：fadeIn + slideInVertically、expandHorizontally 等。"
            )
            Button(onClick = { visible = !visible }) { Text(if (visible) "隐藏内容" else "显示内容") }
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Box(
                    Modifier.fillMaxWidth().height(60.dp).background(Color(0xFF80CBC4), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) { Text("我会动画出现/消失！", color = Color.White) }
            }
        }

        // AnimatedContent
        var count by remember { mutableIntStateOf(0) }
        DemoBox("AnimatedContent — 内容切换动画", code = AnimCode.animatedContent) {
            ExplainCard(
                title = "AnimatedContent",
                content = "当目标状态变化时，AnimatedContent 会动画地切换新旧内容。\n" +
                        "可以自定义 transitionSpec 控制进出方向，例如数字增大时向上滑出、向上滑入。"
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = { count-- }) { Text("-") }
                AnimatedContent(
                    targetState = count,
                    transitionSpec = {
                        if (targetState > initialState) {
                            slideInVertically { -it } + fadeIn() togetherWith slideOutVertically { it } + fadeOut()
                        } else {
                            slideInVertically { it } + fadeIn() togetherWith slideOutVertically { -it } + fadeOut()
                        }
                    },
                    label = "counter"
                ) { targetCount ->
                    Text("$targetCount", style = MaterialTheme.typography.displaySmall)
                }
                Button(onClick = { count++ }) { Text("+") }
            }
        }

        // animateContentSize
        var textExpanded by remember { mutableStateOf(false) }
        DemoBox("animateContentSize — 尺寸自动动画", code = AnimCode.contentSize) {
            ExplainCard(
                title = "animateContentSize",
                content = "给容器添加 Modifier.animateContentSize()，当内容大小变化时自动播放尺寸变化动画，无需手动处理。"
            )
            Box(
                Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .background(Color(0xFFFFF9C4), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text(
                    if (textExpanded)
                        "这是展开后的完整文本内容。animateContentSize 会平滑地动画过渡容器高度变化，使用体验非常自然流畅，非常适合展开收起的卡片场景。"
                    else "点击按钮展开更多内容…"
                )
            }
            Button(onClick = { textExpanded = !textExpanded }) { Text(if (textExpanded) "收起" else "展开") }
        }

        // Infinite animation
        val infiniteTransition = rememberInfiniteTransition(label = "infinite")
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f, targetValue = 360f,
            animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing)),
            label = "rotation"
        )
        val pulseScale by infiniteTransition.animateFloat(
            initialValue = 0.8f, targetValue = 1.2f,
            animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
            label = "pulse"
        )
        val colorValue by infiniteTransition.animateFloat(
            initialValue = 0f, targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
            label = "color"
        )
        DemoBox("rememberInfiniteTransition — 无限循环动画", code = AnimCode.infinite) {
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(60.dp).rotate(rotation).background(Color(0xFF7986CB), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) { Text("旋转", color = Color.White, style = MaterialTheme.typography.labelSmall) }
                Box(
                    Modifier.size(60.dp).scale(pulseScale).background(Color(0xFFEF5350), CircleShape),
                    contentAlignment = Alignment.Center
                ) { Text("脉冲", color = Color.White, style = MaterialTheme.typography.labelSmall) }
                Box(
                    Modifier.size(60.dp).background(
                        Color(colorValue, 0.5f, 1f - colorValue),
                        RoundedCornerShape(8.dp)
                    ),
                    contentAlignment = Alignment.Center
                ) { Text("变色", color = Color.White, style = MaterialTheme.typography.labelSmall) }
            }
        }

        // updateTransition
        ExplainCard(
            title = "updateTransition — 多属性协调动画",
            content = "updateTransition 用于协调多个属性同时基于同一状态变化的动画，确保它们同步开始和结束。\n" +
                    "适合按钮按压效果、卡片翻转等需要多属性同步的场景。"
        )
        var selected by remember { mutableStateOf(false) }
        val transition = updateTransition(selected, label = "card")
        val cardBgColor by transition.animateColor(label = "bg") { s ->
            if (s) Color(0xFF7986CB) else Color(0xFFECEFF1)
        }
        val cardScale by transition.animateFloat(label = "scale") { s -> if (s) 1.05f else 1f }
        val cardElevation by transition.animateDp(label = "elevation") { s -> if (s) 8.dp else 2.dp }
        DemoBox("updateTransition 卡片选中效果", code = AnimCode.transition) {
            Card(
                onClick = { selected = !selected },
                modifier = Modifier.fillMaxWidth().graphicsLayer { scaleX = cardScale; scaleY = cardScale },
                colors = CardDefaults.cardColors(containerColor = cardBgColor),
                elevation = CardDefaults.cardElevation(defaultElevation = cardElevation)
            ) {
                Box(Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                    Text(
                        if (selected) "✅ 已选中（点击取消）" else "点击选中此卡片",
                        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Crossfade
        var currentTab by remember { mutableIntStateOf(0) }
        DemoBox("Crossfade — 淡入淡出切换", code = AnimCode.crossfade) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(3) { i ->
                    Button(
                        onClick = { currentTab = i },
                        colors = if (currentTab == i) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors()
                    ) { Text("Tab ${i + 1}") }
                }
            }
            Crossfade(targetState = currentTab, label = "tab") { tab ->
                Box(
                    Modifier.fillMaxWidth().height(60.dp)
                        .background(
                            listOf(Color(0xFFFFCDD2), Color(0xFFDCEDC8), Color(0xFFBBDEFB))[tab],
                            RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) { Text("Tab ${tab + 1} 的内容") }
            }
        }
    }
}

