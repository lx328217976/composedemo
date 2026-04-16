package com.lory.mycomposedemo.demos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lory.mycomposedemo.DemoBox
import com.lory.mycomposedemo.DemoScaffold
import com.lory.mycomposedemo.ExplainCard

private object LayoutCode {
    val column = """
// 基础 Column
Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.SpaceBetween,   // 两端对齐
    horizontalAlignment = Alignment.CenterHorizontally
) {
    Text("Item 1")
    Text("Item 2")
    Text("Item 3")
}

// 常用 Arrangement 值：
// Arrangement.Top / Bottom / Center
// Arrangement.SpaceBetween / SpaceAround / SpaceEvenly
// Arrangement.spacedBy(8.dp)   ← 推荐：统一间距
    """.trimIndent()

    val row = """
// 基础 Row
Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
) {
    Text("Left")
    Text("Right")
}

// weight 按比例分配空间（类似 flexGrow）
Row(Modifier.fillMaxWidth()) {
    Box(Modifier.weight(1f).height(30.dp).background(Color.Red))   // 1 份
    Box(Modifier.weight(2f).height(30.dp).background(Color.Green)) // 2 份
    Box(Modifier.weight(1f).height(30.dp).background(Color.Blue))  // 1 份
}
    """.trimIndent()

    val box = """
// Box 叠加布局，类似 FrameLayout
Box(
    modifier = Modifier.fillMaxWidth().height(120.dp),
    contentAlignment = Alignment.Center  // 所有子元素默认居中
) {
    // 子元素可用 align() 单独指定位置
    Box(Modifier.size(80.dp).background(Color.Blue).align(Alignment.TopStart))
    Box(Modifier.size(60.dp).background(Color.Red).align(Alignment.Center))
    Box(Modifier.size(50.dp).background(Color.Green).align(Alignment.BottomEnd))
}
    """.trimIndent()

    val spacedBy = """
// Arrangement.spacedBy 统一设置子元素间距，推荐代替手动 Spacer
Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    repeat(3) {
        Box(Modifier.fillMaxWidth().height(30.dp).background(Color.Gray))
    }
}

Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
    repeat(3) {
        Box(Modifier.size(40.dp).background(Color.Blue))
    }
}
    """.trimIndent()

    val wrapVsFill = """
// wrapContentSize：子元素大小由内容决定
Box(Modifier.size(80.dp).background(Color.Yellow)) {
    Box(Modifier.wrapContentSize().background(Color.Orange)) {
        Text("wrap", Modifier.padding(4.dp))
    }
}

// fillMaxSize：子元素撑满父容器
Box(Modifier.size(80.dp).background(Color.Purple)) {
    Box(Modifier.fillMaxSize().background(Color.Cyan.copy(alpha = 0.5f)))
}
    """.trimIndent()
}

@Composable
fun LayoutDemoScreen(onBack: () -> Unit) {
    DemoScaffold(title = "📐 常用布局", onBack = onBack) {

        ExplainCard(
            title = "Column — 垂直线性布局",
            content = "Column 将子元素垂直排列，类似 LinearLayout(vertical)。\n" +
                    "• verticalArrangement：子元素在主轴（垂直）的排列方式\n" +
                    "• horizontalAlignment：子元素在交叉轴（水平）的对齐方式"
        )
        DemoBox("Column — verticalArrangement 示例", code = LayoutCode.column) {
            Text("SpaceBetween:", style = MaterialTheme.typography.labelMedium)
            Row(Modifier.fillMaxWidth().height(80.dp).background(Color(0xFFE3F2FD), RoundedCornerShape(8.dp)).padding(4.dp)) {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                    ColorBox(Color.Red, "A"); ColorBox(Color.Green, "B"); ColorBox(Color.Blue, "C")
                }
            }
            Text("SpaceEvenly:", style = MaterialTheme.typography.labelMedium)
            Row(Modifier.fillMaxWidth().height(80.dp).background(Color(0xFFFFF9C4), RoundedCornerShape(8.dp)).padding(4.dp)) {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly) {
                    ColorBox(Color.Red, "A"); ColorBox(Color.Green, "B"); ColorBox(Color.Blue, "C")
                }
            }
        }

        ExplainCard(
            title = "Row — 水平线性布局",
            content = "Row 将子元素水平排列，类似 LinearLayout(horizontal)。\n" +
                    "• horizontalArrangement：子元素在主轴（水平）的排列\n" +
                    "• verticalAlignment：子元素在交叉轴（垂直）的对齐\n" +
                    "• weight() modifier 用于按比例分配空间"
        )
        DemoBox("Row — Arrangement 与 weight", code = LayoutCode.row) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                ColorBox(Color(0xFFEF9A9A), "1"); ColorBox(Color(0xFFA5D6A7), "2"); ColorBox(Color(0xFF90CAF9), "3")
            }
            Row(Modifier.fillMaxWidth()) {
                Box(Modifier.weight(1f).height(30.dp).background(Color(0xFFEF9A9A)), contentAlignment = Alignment.Center) { Text("1份") }
                Box(Modifier.weight(2f).height(30.dp).background(Color(0xFFA5D6A7)), contentAlignment = Alignment.Center) { Text("2份") }
                Box(Modifier.weight(1f).height(30.dp).background(Color(0xFF90CAF9)), contentAlignment = Alignment.Center) { Text("1份") }
            }
            Text("weight 按 1:2:1 分配水平空间", style = MaterialTheme.typography.bodySmall)
        }

        ExplainCard(
            title = "Box — 层叠布局",
            content = "Box 类似 FrameLayout，子元素可以相互叠加。\n" +
                    "通过 contentAlignment 设置默认对齐，也可以对单个子元素用 align() modifier 单独指定位置。"
        )
        DemoBox("Box — 叠加与对齐", code = LayoutCode.box) {
            Box(Modifier.fillMaxWidth().height(120.dp).background(Color(0xFFE8EAF6), RoundedCornerShape(8.dp))) {
                Box(Modifier.size(80.dp).background(Color(0xFF7986CB)).align(Alignment.TopStart).padding(4.dp)) {
                    Text("TopStart", style = MaterialTheme.typography.labelSmall, color = Color.White)
                }
                Box(Modifier.size(60.dp).background(Color(0xFFEF5350)).align(Alignment.Center)) {
                    Text("Center", style = MaterialTheme.typography.labelSmall, color = Color.White, modifier = Modifier.align(Alignment.Center))
                }
                Box(Modifier.size(50.dp).background(Color(0xFF66BB6A)).align(Alignment.BottomEnd)) {
                    Text("BottomEnd", style = MaterialTheme.typography.labelSmall, color = Color.White)
                }
            }
        }

        ExplainCard(
            title = "Scaffold — 页面脚手架",
            content = "Scaffold 提供 Material Design 页面结构，包含：\n" +
                    "• topBar：顶部应用栏\n" +
                    "• bottomBar：底部导航栏\n" +
                    "• floatingActionButton：悬浮操作按钮\n" +
                    "• snackbarHost：Snackbar 容器\n" +
                    "• content：主体内容（提供 innerPadding 避免系统遮挡）"
        )

        DemoBox("Arrangement.spacedBy — 统一间距", code = LayoutCode.spacedBy) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(3) { i ->
                    Box(Modifier.fillMaxWidth().height(30.dp).background(Color(0xFF80CBC4), RoundedCornerShape(4.dp)), contentAlignment = Alignment.Center) {
                        Text("Item ${i + 1}")
                    }
                }
            }
            Text("所有子元素之间自动保持 8dp 间距", style = MaterialTheme.typography.bodySmall)
        }

        DemoBox("wrapContentSize vs fillMaxSize", code = LayoutCode.wrapVsFill) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.size(80.dp).background(Color(0xFFFFCC80), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                    Box(Modifier.wrapContentSize().background(Color(0xFFFF8F00))) { Text("wrap", Modifier.padding(4.dp)) }
                }
                Box(Modifier.size(80.dp).background(Color(0xFFCE93D8), RoundedCornerShape(8.dp))) {
                    Box(Modifier.fillMaxSize().background(Color(0xFF7B1FA2).copy(alpha = 0.5f))) {
                        Text("fill", Modifier.align(Alignment.Center), color = Color.White)
                    }
                }
            }
        }

        ExplainCard(
            title = "布局原则总结",
            content = "• Column / Row 线性布局，选择合适的 Arrangement 控制分布\n" +
                    "• Box 用于叠加，适合头像+角标、背景+前景等场景\n" +
                    "• Scaffold 管理整体页面结构，不要自行处理系统 inset\n" +
                    "• weight() 实现弹性布局，类似 flexGrow\n" +
                    "• 嵌套布局时注意性能，Compose 单次测量效率高"
        )
    }
}

@Composable
private fun ColorBox(color: Color, label: String) {
    Box(Modifier.size(30.dp).background(color, RoundedCornerShape(4.dp)), contentAlignment = Alignment.Center) {
        Text(label, color = Color.White, style = MaterialTheme.typography.labelSmall)
    }
}
