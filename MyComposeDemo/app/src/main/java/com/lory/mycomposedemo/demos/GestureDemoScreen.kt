package com.lory.mycomposedemo.demos

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.lory.mycomposedemo.DemoBox
import com.lory.mycomposedemo.DemoScaffold
import com.lory.mycomposedemo.ExplainCard
import kotlin.math.roundToInt

@Composable
fun GestureDemoScreen(onBack: () -> Unit) {
    DemoScaffold(title = "👆 手势处理", onBack = onBack) {

        ExplainCard(
            title = "Compose 手势处理概览",
            content = "Compose 提供两层手势 API：\n" +
                    "① 高级手势 Modifier（推荐）：clickable、combinedClickable、draggable、scrollable、zoomable 等\n" +
                    "② 底层 pointerInput + detectXxxGestures：可检测任意复杂手势，灵活性最高\n\n" +
                    "手势事件在组合树中自上而下传递，子节点默认优先消费。\n" +
                    "需要协调父子手势时使用 NestedScrollConnection。"
        )

        // Tap gestures
        var tapMsg by remember { mutableStateOf("等待手势...") }
        DemoBox("detectTapGestures — 各种点击手势", code = """
Box(
    Modifier
        .fillMaxWidth().height(80.dp)
        .pointerInput(Unit) {
            detectTapGestures(
                onTap       = { /* 单击 */ },
                onDoubleTap = { /* 双击 */ },
                onLongPress = { /* 长按 */ },
                onPress     = { /* 按下瞬间 */ }
            )
        }
)
        """.trimIndent()) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(Color(0xFF7986CB), RoundedCornerShape(12.dp))
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { tapMsg = "单击 tap" },
                            onDoubleTap = { tapMsg = "双击 double tap！" },
                            onLongPress = { tapMsg = "长按 long press！" },
                            onPress = { tapMsg = "按下 (press)..." }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(tapMsg, color = Color.White)
            }
            Text("支持：onTap / onDoubleTap / onLongPress / onPress", style = MaterialTheme.typography.bodySmall)
        }

        // Drag
        var offsetX by remember { mutableFloatStateOf(0f) }
        var offsetY by remember { mutableFloatStateOf(0f) }
        DemoBox("detectDragGestures — 自由拖拽", code = """
var offsetX by remember { mutableFloatStateOf(0f) }
var offsetY by remember { mutableFloatStateOf(0f) }

Box(
    Modifier
        .size(60.dp)
        .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
        .pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
                change.consume()           // 消费事件，阻止继续传递
                offsetX += dragAmount.x
                offsetY += dragAmount.y
            }
        }
)
        """.trimIndent()) {
            Box(
                Modifier.fillMaxWidth().height(160.dp)
                    .background(Color(0xFFE8EAF6), RoundedCornerShape(12.dp))
            ) {
                Box(
                    Modifier
                        .size(60.dp)
                        .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                        .background(Color(0xFF5C6BC0), CircleShape)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                offsetX = (offsetX + dragAmount.x).coerceIn(0f, size.width.toFloat() - 60.dp.toPx())
                                offsetY = (offsetY + dragAmount.y).coerceIn(0f, size.height.toFloat() - 60.dp.toPx())
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("拖我", color = Color.White, style = MaterialTheme.typography.labelSmall)
                }
            }
            Text("偏移量：(${offsetX.roundToInt()}, ${offsetY.roundToInt()}) px", style = MaterialTheme.typography.bodySmall)
            OutlinedButton(onClick = { offsetX = 0f; offsetY = 0f }) { Text("重置位置") }
        }

        // Transform (pinch zoom + rotation)
        var zoom by remember { mutableFloatStateOf(1f) }
        var rotation by remember { mutableFloatStateOf(0f) }
        var panOffset by remember { mutableStateOf(Offset.Zero) }
        DemoBox("detectTransformGestures — 双指缩放 & 旋转 & 平移", code = """
var zoom by remember { mutableFloatStateOf(1f) }
var rotation by remember { mutableFloatStateOf(0f) }
var panOffset by remember { mutableStateOf(Offset.Zero) }

Box(
    Modifier.pointerInput(Unit) {
        detectTransformGestures { _, pan, gestureZoom, gestureRotation ->
            zoom *= gestureZoom
            rotation += gestureRotation
            panOffset += pan
        }
    }
) {
    Box(
        Modifier.graphicsLayer {
            scaleX = zoom; scaleY = zoom
            rotationZ = rotation
            translationX = panOffset.x; translationY = panOffset.y
        }
    )
}
        """.trimIndent()) {
            ExplainCard(
                title = "detectTransformGestures",
                content = "同时处理双指缩放（pinch）、旋转和平移，在图片查看、地图等场景常用。"
            )
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(Color(0xFFFFF9C4), RoundedCornerShape(12.dp))
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, gestureZoom, gestureRotation ->
                            zoom *= gestureZoom
                            rotation += gestureRotation
                            panOffset += pan
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    Modifier
                        .size(80.dp)
                        .graphicsLayer {
                            scaleX = zoom
                            scaleY = zoom
                            rotationZ = rotation
                            translationX = panOffset.x
                            translationY = panOffset.y
                        }
                        .background(Color(0xFFF57F17), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("双指缩放/旋转", color = Color.White, style = MaterialTheme.typography.labelSmall)
                }
            }
            Text("缩放：${"%.2f".format(zoom)}x | 旋转：${"%.0f".format(rotation)}°", style = MaterialTheme.typography.bodySmall)
            OutlinedButton(onClick = { zoom = 1f; rotation = 0f; panOffset = Offset.Zero }) { Text("重置") }
        }

        // Swipe to dismiss
        ExplainCard(
            title = "SwipeToDismiss",
            content = "Material3 提供了 SwipeToDismissBox 组件，可以实现左滑删除等常见交互。\n" +
                    "使用 rememberSwipeToDismissBoxState() 管理滑动状态，配合 confirmValueChange 回调处理业务逻辑。"
        )
        var swipeItems by remember { mutableStateOf(listOf("条目 A", "条目 B", "条目 C")) }
        DemoBox("SwipeToDismissBox — 左滑删除", code = """
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeDeleteItem(label: String, onDelete: () -> Unit) {
    val state = rememberSwipeToDismissBoxState()

    // 监听滑动到 EndToStart（从右向左）时触发删除
    LaunchedEffect(state.currentValue) {
        if (state.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onDelete()
        }
    }

    SwipeToDismissBox(
        state = state,
        backgroundContent = {
            // 背景：显示"删除"操作区域
            Box(
                Modifier.fillMaxSize().background(Color.Red).padding(end = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) { Text("删除", color = Color.White) }
        }
    ) {
        // 前景：正常的列表条目
        Card(Modifier.fillMaxWidth()) {
            Text(label, Modifier.padding(16.dp))
        }
    }
}
        """.trimIndent()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                swipeItems.forEach { item ->
                    SwipeDeleteItem(label = item, onDelete = { swipeItems = swipeItems - item })
                }
                if (swipeItems.isEmpty()) {
                    Text("所有条目已删除", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                    Button(onClick = { swipeItems = listOf("条目 A", "条目 B", "条目 C") }) { Text("恢复") }
                }
            }
        }

        ExplainCard(
            title = "手势事件传播与消费",
            content = "• change.consume()：消费指针事件，阻止它继续传递给其他手势处理器\n" +
                    "• PointerInputScope 中可以通过 awaitPointerEventScope 自定义底层事件流\n" +
                    "• 父子手势冲突时，使用 NestedScrollConnection 协调嵌套滚动\n" +
                    "• Modifier.pointerInteropFilter 可以与传统 View 的触摸事件互操作"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeDeleteItem(label: String, onDelete: () -> Unit) {
    val state = rememberSwipeToDismissBoxState()
    LaunchedEffect(state.currentValue) {
        if (state.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onDelete()
        }
    }
    SwipeToDismissBox(
        state = state,
        backgroundContent = {
            Box(
                Modifier.fillMaxSize().background(Color(0xFFEF5350), RoundedCornerShape(8.dp)).padding(end = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) { Text("删除", color = Color.White) }
        }
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Box(Modifier.fillMaxWidth().padding(16.dp)) {
                Text(label)
            }
        }
    }
}

