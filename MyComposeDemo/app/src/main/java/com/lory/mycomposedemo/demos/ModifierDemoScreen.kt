package com.lory.mycomposedemo.demos

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lory.mycomposedemo.DemoBox
import com.lory.mycomposedemo.DemoScaffold
import com.lory.mycomposedemo.ExplainCard

private object ModifierCode {
    val size = """
// 固定尺寸
Box(Modifier.size(80.dp).background(Color.Purple))

// 指定宽高
Box(Modifier.width(120.dp).height(40.dp).background(Color.Teal))

// 撑满父容器宽度
Box(Modifier.fillMaxWidth().height(20.dp).background(Color.Orange))

// 撑满父容器全部
Box(Modifier.fillMaxSize().background(Color.Gray))
    """.trimIndent()

    val padding = """
// padding 在 background 之前 → 类似 margin（背景不含边距区域）
Box(
    Modifier
        .padding(8.dp)       // 先 padding
        .background(Color.Red)
        .size(60.dp)
)

// padding 在 background 之后 → 真正的内边距（背景包含全部区域）
Box(
    Modifier
        .background(Color.Blue)
        .padding(8.dp)       // 后 padding
        .size(60.dp)
)
    """.trimIndent()

    val backgroundBorder = """
Box(
    Modifier
        .size(80.dp)
        .shadow(8.dp, RoundedCornerShape(12.dp))
        .background(Color.Yellow, RoundedCornerShape(12.dp))
        .border(2.dp, Color.Gold, RoundedCornerShape(12.dp))
)

// 圆形裁切
Box(
    Modifier
        .size(80.dp)
        .clip(CircleShape)
        .background(Color.Teal)
)
    """.trimIndent()

    val transform = """
// 旋转 30 度
Box(Modifier.size(60.dp).rotate(30f).background(Color.Red))

// 缩放 1.3 倍
Box(Modifier.size(60.dp).scale(1.3f).background(Color.Green))

// 透明度 40%
Box(Modifier.size(60.dp).alpha(0.4f).background(Color.Blue))
    """.trimIndent()

    val click = """
var count by remember { mutableIntStateOf(0) }

Box(
    Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
        .clickable { count++ }   // 自动添加水波纹效果
        .padding(16.dp),
    contentAlignment = Alignment.Center
) {
    Text("点我！已点击 ${'$'}count 次", color = Color.White)
}
    """.trimIndent()

    val offset = """
Box(Modifier.size(100.dp).background(Color.LightGray)) {
    Box(
        Modifier
            .size(40.dp)
            .offset(20.dp, 20.dp)   // 相对父容器偏移
            .background(Color.Purple)
    )
}
    """.trimIndent()
}

@Composable
fun ModifierDemoScreen(onBack: () -> Unit) {
    DemoScaffold(title = "🎨 Modifier", onBack = onBack) {

        ExplainCard(
            title = "什么是 Modifier？",
            content = "Modifier 是 Compose 中对 UI 元素进行装饰和配置的核心机制。它采用链式调用方式，" +
                    "顺序很重要——每个修饰符会影响后续修饰符的行为。Modifier 可以控制尺寸、位置、" +
                    "背景、点击事件、绘制顺序等几乎所有视觉与交互属性。"
        )

        // Size
        DemoBox("📏 尺寸修饰符：size / width / height / fillMaxWidth", code = ModifierCode.size) {
            Box(Modifier.size(80.dp).background(Color(0xFF6200EE))) { }
            Box(Modifier.width(120.dp).height(40.dp).background(Color(0xFF03DAC5))) { }
            Box(Modifier.fillMaxWidth().height(20.dp).background(Color(0xFFFF6D00))) { }
            Text("fillMaxWidth 会撑满父容器宽度", style = MaterialTheme.typography.bodySmall)
        }

        // Padding vs Margin
        DemoBox("📦 padding 与布局间距", code = ModifierCode.padding) {
            ExplainCard(
                title = "Compose 没有 margin！",
                content = "在 Compose 中没有独立的 margin 属性。通常用外部 padding 或 Arrangement.spacedBy() 实现间距。\n" +
                        "padding() 加在 background() 之前 → 像 margin（内容缩进但背景不缩）\n" +
                        "padding() 加在 background() 之后 → 像 padding（背景覆盖全部，内容缩进）"
            )
            // padding before bg → margin effect
            Box(Modifier.padding(8.dp).background(Color.Red).size(60.dp))
            // padding after bg → padding effect
            Box(Modifier.background(Color.Blue).padding(8.dp).size(60.dp))
        }

        // Background & Border
        DemoBox("🖌️ background / border / clip / shadow", code = ModifierCode.backgroundBorder) {
            Box(
                Modifier.size(80.dp).shadow(8.dp, RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFF9C4), RoundedCornerShape(12.dp))
                    .border(2.dp, Color(0xFFFFD600), RoundedCornerShape(12.dp))
            ) {
                Text("阴影+圆角", Modifier.align(Alignment.Center), style = MaterialTheme.typography.labelSmall)
            }
            Spacer(Modifier.width(8.dp))
            Box(Modifier.size(80.dp).clip(CircleShape).background(Color(0xFF80CBC4))) {
                Text("圆形裁切", Modifier.align(Alignment.Center), style = MaterialTheme.typography.labelSmall)
            }
        }

        // Transform
        DemoBox("🔄 变换：rotate / scale / alpha", code = ModifierCode.transform) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(60.dp).rotate(30f).background(Color(0xFFEF9A9A)))
                Box(Modifier.size(60.dp).scale(1.3f).background(Color(0xFFA5D6A7)))
                Box(Modifier.size(60.dp).alpha(0.4f).background(Color(0xFF90CAF9)))
            }
            Text("依次为：rotate(30°) / scale(1.3) / alpha(0.4)", style = MaterialTheme.typography.bodySmall)
        }

        // Click
        var clickCount by remember { mutableIntStateOf(0) }
        DemoBox("👆 clickable 点击事件", code = ModifierCode.click) {
            Box(
                Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                    .clickable { clickCount++ }.padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("点我！已点击 $clickCount 次", color = Color.White)
            }
            Text("注意：clickable 会自动添加水波纹效果", style = MaterialTheme.typography.bodySmall)
        }

        // Offset
        DemoBox("📍 offset 偏移", code = ModifierCode.offset) {
            Box(Modifier.size(100.dp).background(Color.LightGray)) {
                Box(Modifier.size(40.dp).offset(20.dp, 20.dp).background(Color(0xFFCE93D8)))
            }
            Text("子元素相对父元素偏移了 (20dp, 20dp)", style = MaterialTheme.typography.bodySmall)
        }

        ExplainCard(
            title = "修饰符顺序的重要性",
            content = "Modifier 的链式调用顺序会影响最终效果。\n" +
                    "例如：.padding(10dp).background(Red) 与 .background(Red).padding(10dp) 效果完全不同。\n" +
                    "前者：背景不包含 padding 区域（类似 margin）\n" +
                    "后者：背景包含 padding 区域（真正的内边距）"
        )
    }
}
