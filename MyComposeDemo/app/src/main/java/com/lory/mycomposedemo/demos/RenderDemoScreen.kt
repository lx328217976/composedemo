package com.lory.mycomposedemo.demos

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lory.mycomposedemo.DemoBox
import com.lory.mycomposedemo.DemoScaffold
import com.lory.mycomposedemo.ExplainCard

@Composable
fun RenderDemoScreen(onBack: () -> Unit) {
    DemoScaffold(title = "⚙️ Compose 渲染流程", onBack = onBack) {

        ExplainCard(
            title = "Compose 渲染三阶段",
            content = "Compose 的渲染流程分为三个阶段，依次执行：\n\n" +
                    "① Composition（组合）：执行 @Composable 函数，构建 UI 树（Slot Table）\n" +
                    "② Layout（布局）：测量（measure）每个节点的尺寸，计算（place）每个节点的位置\n" +
                    "③ Drawing（绘制）：遍历 UI 树，调用 Canvas API 将内容绘制到屏幕\n\n" +
                    "状态变化 → 触发重组（Recomposition）→ 仅重新 Compose 受影响的子树 → Layout → Draw"
        )

        // Phase diagram
        DemoBox("渲染流程示意图", code = """
// Compose 渲染三阶段，依次执行：
// ① Composition：执行 @Composable 函数，构建 UI 节点树
// ② Layout：Measure（测量尺寸）+ Place（计算位置）
// ③ Drawing：遍历节点树，调用 Canvas API 绘制到屏幕

// 状态变化 → Recomposition（仅重组受影响子树）→ Layout → Draw

// 优化：如果只改变绘制属性（如透明度、位移），可跳过 Composition 和 Layout
// 使用 Modifier.graphicsLayer { } 直接在 Drawing 阶段修改，性能更好
Box(Modifier.graphicsLayer {
    alpha = 0.5f
    translationY = offset
    scaleX = scale
})
        """.trimIndent()) {
            PhaseFlowDiagram()
        }

        // Composition phase
        ExplainCard(
            title = "① Composition 阶段",
            content = "Compose 执行所有 @Composable 函数，构建一棵描述 UI 结构的内部树（称为 Slot Table / Node Tree）。\n" +
                    "关键点：\n" +
                    "• Composable 函数可以多次调用（重组），但 Compose 会智能跳过未发生变化的部分\n" +
                    "• 只有读取了变化状态的 Composable 会被重组，其他保持不变\n" +
                    "• remember { } 使得数据在多次重组间持久化"
        )

        var recompositionCount by remember { mutableIntStateOf(0) }
        var triggerState by remember { mutableIntStateOf(0) }
        DemoBox("重组计数演示", code = """
// SideEffect 在每次成功重组后执行（同步，无协程）
// 可以用来统计重组次数，或同步外部状态
@Composable
fun RecompositionCounter(trigger: Int, onRecompose: () -> Unit) {
    SideEffect {
        onRecompose()  // 每次重组都会调用
    }
    Text("当前 trigger 值：${'$'}trigger")
}

// 父组件
var count by remember { mutableIntStateOf(0) }
var recomposeCount by remember { mutableIntStateOf(0) }

RecompositionCounter(trigger = count, onRecompose = { recomposeCount++ })
Button(onClick = { count++ }) { Text("触发重组") }
Text("已重组 ${'$'}recomposeCount 次")
        """.trimIndent()) {
            // 这个 Text 读取了 recompositionCount，会在 triggerState 变化时重组
            RecompositionCounter(trigger = triggerState, onRecompose = { recompositionCount++ })
            Text("已触发重组次数：$recompositionCount", fontWeight = FontWeight.Bold)
            Button(onClick = { triggerState++ }) { Text("触发状态变化（重组）") }
            Text("注意：只有直接读取变化状态的作用域会重组", style = MaterialTheme.typography.bodySmall)
        }

        // Layout phase
        ExplainCard(
            title = "② Layout 阶段",
            content = "Layout 阶段分两步：\n" +
                    "• Measure（测量）：父节点向子节点传递 Constraints（最小/最大宽高约束），子节点返回自己的尺寸\n" +
                    "• Placement（放置）：父节点根据子节点尺寸决定其 x/y 坐标\n\n" +
                    "重要规则：每个节点只能被测量一次（single-pass measurement），这是 Compose 性能优于传统 View 的关键。\n" +
                    "需要多次测量的场景（如 IntrinsicSize）有专门的 API 支持。"
        )

        var measuredSize by remember { mutableStateOf("未测量") }
        DemoBox("onSizeChanged / onGloballyPositioned 获取布局信息", code = """
// onSizeChanged：Layout 完成后回调，获取像素尺寸
var sizeText by remember { mutableStateOf("") }
Box(
    Modifier
        .fillMaxWidth().height(60.dp)
        .onSizeChanged { size ->
            sizeText = "${'$'}{size.width}px × ${'$'}{size.height}px"
        }
)

// onGloballyPositioned：获取在屏幕上的绝对位置
Box(
    Modifier.onGloballyPositioned { coordinates ->
        val posInWindow = coordinates.positionInWindow()
        val posInRoot = coordinates.positionInRoot()
        // posInWindow.x, posInWindow.y 是相对窗口的像素坐标
    }
)
        """.trimIndent()) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(Color(0xFF80CBC4), RoundedCornerShape(8.dp))
                    .onSizeChanged { size -> measuredSize = "${size.width}px × ${size.height}px" }
                    .onGloballyPositioned { },
                contentAlignment = Alignment.Center
            ) {
                Text("这个 Box 的尺寸：$measuredSize", color = Color.White)
            }
            Text("onSizeChanged 在 Layout 完成后回调，单位是像素（px）", style = MaterialTheme.typography.bodySmall)
        }

        // Custom Layout
        ExplainCard(
            title = "自定义 Layout",
            content = "使用 Layout { } 可以完全自定义布局逻辑，类似自定义 ViewGroup 的 onMeasure / onLayout。\n" +
                    "步骤：\n" +
                    "① 测量所有子节点（measurables.map { it.measure(constraints) }）\n" +
                    "② 用 layout(width, height) { } 设置自身尺寸并放置子节点"
        )
        DemoBox("自定义 Layout：简单横向等分布局", code = """
// 自定义 Layout 完全控制测量和放置逻辑
@Composable
fun CustomEqualRow(content: @Composable () -> Unit) {
    Layout(content = content) { measurables, constraints ->
        // ① 计算每个子项的宽度（等分）
        val itemWidth = constraints.maxWidth / measurables.size

        // ② 测量每个子项
        val placeables = measurables.map { measurable ->
            measurable.measure(Constraints.fixedWidth(itemWidth))
        }

        // ③ 计算容器总高度
        val height = placeables.maxOf { it.height }

        // ④ 放置每个子项
        layout(constraints.maxWidth, height) {
            var x = 0
            placeables.forEach { placeable ->
                placeable.placeRelative(x, 0)
                x += placeable.width
            }
        }
    }
}

// 使用
CustomEqualRow {
    Text("左"); Text("中"); Text("右")
}
        """.trimIndent()) {
            CustomEqualRow {
                repeat(3) { i ->
                    Box(
                        Modifier.height(50.dp).background(
                            listOf(Color(0xFFEF9A9A), Color(0xFFA5D6A7), Color(0xFF90CAF9))[i],
                            RoundedCornerShape(4.dp)
                        ),
                        contentAlignment = Alignment.Center
                    ) { Text("Item ${i + 1}", color = Color.White) }
                }
            }
            Text("3个子项等分宽度，通过自定义 Layout 实现", style = MaterialTheme.typography.bodySmall)
        }

        // Drawing phase
        ExplainCard(
            title = "③ Drawing 阶段",
            content = "绘制阶段遍历 UI 树，按顺序将每个节点绘制到 Canvas 上。\n" +
                    "Canvas 组件提供直接访问 DrawScope 的能力，可以绘制任意自定义图形。\n" +
                    "• drawLine / drawCircle / drawRect / drawPath 等绘制 API\n" +
                    "• Modifier.drawBehind { } / Modifier.drawWithContent { } 可以在现有组件上增加自定义绘制"
        )
        DemoBox("Canvas 自定义绘制", code = """
// Canvas 提供直接绘制 API，在 DrawScope 中操作
Canvas(
    modifier = Modifier.fillMaxWidth().height(120.dp)
) {
    val w = size.width
    val h = size.height

    // 绘制坐标轴
    drawLine(Color.White, Offset(40f, h - 20f), Offset(w - 10f, h - 20f), strokeWidth = 2f)
    drawLine(Color.White, Offset(40f, 10f),     Offset(40f, h - 20f),     strokeWidth = 2f)

    // 绘制折线
    val points = listOf(0.2f, 0.5f, 0.3f, 0.8f, 0.6f)
    val step = (w - 60f) / (points.size - 1)
    for (i in 0 until points.size - 1) {
        drawLine(
            color = Color.Cyan,
            start = Offset(40f + i * step,       h - 20f - points[i] * (h - 40f)),
            end   = Offset(40f + (i + 1) * step, h - 20f - points[i+1] * (h - 40f)),
            strokeWidth = 3f
        )
    }

    // 绘制数据点
    points.forEachIndexed { i, v ->
        drawCircle(Color.Yellow, radius = 6f,
            center = Offset(40f + i * step, h - 20f - v * (h - 40f)))
    }
}

// 其他常用绘制 API：
// drawRect / drawRoundRect / drawOval / drawArc / drawPath
// Modifier.drawBehind { }       在组件背后绘制
// Modifier.drawWithContent { }  在组件内容前后插入绘制
        """.trimIndent()) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color(0xFF1A237E), RoundedCornerShape(8.dp))
            ) {
                drawCustomGraphics()
            }
            Text("使用 Canvas 绘制：折线图、圆形、坐标轴", style = MaterialTheme.typography.bodySmall)
        }

        ExplainCard(
            title = "性能优化建议",
            content = "① 尽量让状态读取发生在 Drawing 阶段（使用 lambda 形式的 Modifier），跳过 Composition 和 Layout\n" +
                    "② 避免在 Composition 中读取频繁变化的状态（如动画值），优先使用 Modifier.graphicsLayer { }\n" +
                    "③ key() 帮助 Compose 正确追踪可组合项身份，避免错误重组\n" +
                    "④ 使用 @Stable、@Immutable 注解让编译器知道数据不会意外变化，启用智能跳过"
        )
    }
}

@Composable
private fun RecompositionCounter(trigger: Int, onRecompose: () -> Unit) {
    SideEffect { onRecompose() }
    Text("当前 trigger 值：$trigger", color = MaterialTheme.colorScheme.primary)
}

@Composable
private fun CustomEqualRow(content: @Composable () -> Unit) {
    Layout(content = content) { measurables, constraints ->
        val itemWidth = constraints.maxWidth / measurables.size
        val placeables = measurables.map { it.measure(Constraints.fixedWidth(itemWidth)) }
        val height = placeables.maxOf { it.height }
        layout(constraints.maxWidth, height) {
            var x = 0
            placeables.forEach { placeable ->
                placeable.placeRelative(x, 0)
                x += placeable.width
            }
        }
    }
}

private fun DrawScope.drawCustomGraphics() {
    val w = size.width
    val h = size.height
    // Axes
    drawLine(Color.White.copy(0.5f), Offset(40f, h - 20f), Offset(w - 10f, h - 20f), strokeWidth = 2f)
    drawLine(Color.White.copy(0.5f), Offset(40f, 10f), Offset(40f, h - 20f), strokeWidth = 2f)
    // Data line
    val points = listOf(0.2f, 0.5f, 0.3f, 0.7f, 0.6f, 0.9f, 0.4f)
    val step = (w - 60f) / (points.size - 1)
    val chartH = h - 40f
    for (i in 0 until points.size - 1) {
        drawLine(
            color = Color(0xFF64FFDA),
            start = Offset(40f + i * step, h - 20f - points[i] * chartH),
            end = Offset(40f + (i + 1) * step, h - 20f - points[i + 1] * chartH),
            strokeWidth = 3f
        )
    }
    // Dots
    points.forEachIndexed { i, v ->
        drawCircle(Color(0xFFFFD740), radius = 6f, center = Offset(40f + i * step, h - 20f - v * chartH))
    }
}

@Composable
private fun PhaseFlowDiagram() {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PhaseBox("① Composition", "执行\n@Composable\n构建 UI 树", Color(0xFF7986CB))
        Text("→", fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        PhaseBox("② Layout", "Measure\n+\nPlace", Color(0xFF4DB6AC))
        Text("→", fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        PhaseBox("③ Drawing", "Canvas\n绘制到\n屏幕", Color(0xFFFF8A65))
    }
}

@Composable
private fun PhaseBox(title: String, desc: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(color, RoundedCornerShape(8.dp))
            .padding(8.dp)
            .width(90.dp)
    ) {
        Text(title, color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text(desc, color = Color.White.copy(0.9f), style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
    }
}

