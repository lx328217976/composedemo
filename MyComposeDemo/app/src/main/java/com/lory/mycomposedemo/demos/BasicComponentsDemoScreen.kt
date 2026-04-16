package com.lory.mycomposedemo.demos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lory.mycomposedemo.DemoBox
import com.lory.mycomposedemo.DemoScaffold
import com.lory.mycomposedemo.ExplainCard

private object BasicCode {
    val text = """
// 普通文本
Text("普通文本")

// 样式
Text("加粗", fontWeight = FontWeight.Bold)
Text("斜体", fontStyle = FontStyle.Italic)
Text("彩色", color = Color(0xFF6200EE), fontSize = 18.sp)
Text("下划线", textDecoration = TextDecoration.Underline)
Text("删除线", textDecoration = TextDecoration.LineThrough)

// 富文本
Text(
    buildAnnotatedString {
        append("普通 + ")
        withStyle(SpanStyle(color = Color.Red, fontWeight = FontWeight.Bold)) {
            append("红色加粗")
        }
        withStyle(SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline)) {
            append("蓝色下划线")
        }
    }
)

// 超长截断
Text(
    "超长文本...",
    maxLines = 1,
    overflow = TextOverflow.Ellipsis
)
    """.trimIndent()

    val button = """
// 实心按钮
Button(onClick = { /* 处理点击 */ }) { Text("Button") }

// 描边按钮
OutlinedButton(onClick = { }) { Text("OutlinedButton") }

// 文字按钮
TextButton(onClick = { }) { Text("TextButton") }

// 阴影按钮
ElevatedButton(onClick = { }) { Text("ElevatedButton") }

// 色调填充按钮
FilledTonalButton(onClick = { }) { Text("FilledTonalButton") }

// 图标按钮
IconButton(onClick = { }) {
    Icon(Icons.Filled.Favorite, contentDescription = "like", tint = Color.Red)
}

// 悬浮按钮
FloatingActionButton(onClick = { }) {
    Icon(Icons.Filled.Favorite, contentDescription = "fab")
}
    """.trimIndent()

    val textField = """
var text by remember { mutableStateOf("") }

// 标准输入框
TextField(
    value = text,
    onValueChange = { text = it },
    label = { Text("标签") },
    modifier = Modifier.fillMaxWidth()
)

// 描边输入框
OutlinedTextField(
    value = text,
    onValueChange = { text = it },
    label = { Text("标签") }
)

// 密码输入框
var password by remember { mutableStateOf("") }
var visible by remember { mutableStateOf(false) }
OutlinedTextField(
    value = password,
    onValueChange = { password = it },
    visualTransformation = if (visible)
        VisualTransformation.None
    else PasswordVisualTransformation(),
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
    trailingIcon = {
        TextButton(onClick = { visible = !visible }) {
            Text(if (visible) "隐藏" else "显示")
        }
    }
)
    """.trimIndent()

    val selection = """
// Checkbox 多选
var checked by remember { mutableStateOf(false) }
Row(verticalAlignment = Alignment.CenterVertically) {
    Checkbox(checked = checked, onCheckedChange = { checked = it })
    Text(if (checked) "已选中" else "未选中")
}

// Switch 开关
var switched by remember { mutableStateOf(true) }
Switch(checked = switched, onCheckedChange = { switched = it })

// RadioButton 单选
var selected by remember { mutableIntStateOf(0) }
Row {
    listOf("A", "B", "C").forEachIndexed { idx, label ->
        RadioButton(selected = selected == idx, onClick = { selected = idx })
        Text(label)
    }
}
    """.trimIndent()

    val slider = """
var value by remember { mutableFloatStateOf(0.5f) }

Slider(
    value = value,
    onValueChange = { value = it }
)
Text("当前值：${'$'}{"%.2f".format(value)}")

// 带步进的 Slider
Slider(
    value = value,
    onValueChange = { value = it },
    steps = 4,        // 将范围分成 5 段
    valueRange = 0f..100f
)
    """.trimIndent()

    val progress = """
// 不确定进度（转圈）
CircularProgressIndicator()

// 确定进度
CircularProgressIndicator(progress = { 0.7f })

// 线性进度条（不确定）
LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

// 线性进度条（确定）
LinearProgressIndicator(
    progress = { 0.6f },
    modifier = Modifier.fillMaxWidth()
)
    """.trimIndent()

    val chip = """
// 辅助 Chip
AssistChip(onClick = {}, label = { Text("AssistChip") })

// 过滤 Chip（可选中）
var selected by remember { mutableStateOf(true) }
FilterChip(
    selected = selected,
    onClick = { selected = !selected },
    label = { Text("FilterChip") }
)

// 输入 Chip
InputChip(
    selected = false,
    onClick = {},
    label = { Text("InputChip") },
    trailingIcon = { Icon(Icons.Default.Close, null) }
)
    """.trimIndent()
}

@Composable
fun BasicComponentsDemoScreen(onBack: () -> Unit) {
    DemoScaffold(title = "🧱 基础组件", onBack = onBack) {

        ExplainCard(
            title = "Text 文本组件",
            content = "Text 是最基础的显示组件，支持富文本（AnnotatedString）、字体样式、行数限制等。"
        )
        DemoBox("Text 样式展示", code = BasicCode.text) {
            Text("普通文本")
            Text("加粗文本", fontWeight = FontWeight.Bold)
            Text("斜体文本", fontStyle = FontStyle.Italic)
            Text("彩色文本", color = Color(0xFF6200EE), fontSize = 18.sp)
            Text("下划线文本", textDecoration = TextDecoration.Underline)
            Text("删除线文本", textDecoration = TextDecoration.LineThrough)
            Text(
                buildAnnotatedString {
                    append("富文本：")
                    withStyle(SpanStyle(color = Color.Red, fontWeight = FontWeight.Bold)) { append("红色加粗") }
                    append(" + ")
                    withStyle(SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline)) { append("蓝色下划线") }
                }
            )
            Text(
                "超长文本会被截断超长文本会被截断超长文本会被截断超长文本会被截断",
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }

        ExplainCard(
            title = "Button 按钮",
            content = "Compose 提供 Button（实心）、OutlinedButton（描边）、TextButton（文字）、" +
                    "ElevatedButton（有阴影）、FilledTonalButton（色调填充）五种按钮风格。"
        )
        var btnMsg by remember { mutableStateOf("点击按钮") }
        DemoBox("Button 变体", code = BasicCode.button) {
            Button(onClick = { btnMsg = "Button 被点击！" }) { Text("Button") }
            OutlinedButton(onClick = { btnMsg = "OutlinedButton 被点击！" }) { Text("OutlinedButton") }
            TextButton(onClick = { btnMsg = "TextButton 被点击！" }) { Text("TextButton") }
            ElevatedButton(onClick = { btnMsg = "ElevatedButton 被点击！" }) { Text("ElevatedButton") }
            FilledTonalButton(onClick = { btnMsg = "FilledTonalButton 被点击！" }) { Text("FilledTonalButton") }
            Text(btnMsg, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = {}) {
                    Icon(Icons.Filled.Favorite, contentDescription = "like", tint = Color.Red)
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Filled.Star, contentDescription = "star", tint = Color(0xFFFFC107))
                }
                FloatingActionButton(onClick = {}) {
                    Icon(Icons.Filled.Favorite, contentDescription = "fab")
                }
            }
        }

        ExplainCard(
            title = "TextField 输入框",
            content = "TextField 是受控组件，需要通过 value 和 onValueChange 管理状态。" +
                    "OutlinedTextField 有描边样式，两者功能一样。"
        )
        var text by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }
        DemoBox("TextField 示例", code = BasicCode.textField) {
            TextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("普通输入框") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("密码输入框") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    TextButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(if (passwordVisible) "隐藏" else "显示")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        ExplainCard(
            title = "选择类组件",
            content = "Checkbox（多选）、Switch（开关）、RadioButton（单选）都是受控组件，需要外部状态驱动。"
        )
        var checked by remember { mutableStateOf(false) }
        var switched by remember { mutableStateOf(true) }
        var selectedOption by remember { mutableIntStateOf(0) }
        DemoBox("选择类组件", code = BasicCode.selection) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = checked, onCheckedChange = { checked = it })
                Spacer(Modifier.width(8.dp))
                Text("Checkbox: ${if (checked) "已选中" else "未选中"}")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(checked = switched, onCheckedChange = { switched = it })
                Spacer(Modifier.width(8.dp))
                Text("Switch: ${if (switched) "开启" else "关闭"}")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                listOf("选项A", "选项B", "选项C").forEachIndexed { idx, label ->
                    RadioButton(selected = selectedOption == idx, onClick = { selectedOption = idx })
                    Text(label)
                    Spacer(Modifier.width(4.dp))
                }
            }
        }

        var sliderValue by remember { mutableFloatStateOf(0.5f) }
        DemoBox("Slider 滑动条", code = BasicCode.slider) {
            Slider(value = sliderValue, onValueChange = { sliderValue = it })
            Text("当前值：${"%.2f".format(sliderValue)}")
        }

        DemoBox("Card 卡片", code = """
Card(
    modifier = Modifier.fillMaxWidth(),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    shape = RoundedCornerShape(12.dp)
) {
    Column(Modifier.padding(16.dp)) {
        Text("卡片标题", style = MaterialTheme.typography.titleMedium)
        Text("卡片内容描述", style = MaterialTheme.typography.bodySmall)
    }
}
        """.trimIndent()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("卡片标题", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text("Card 是常用的容器组件，可以设置圆角和阴影，用于展示相关内容。", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        DemoBox("进度指示器", code = BasicCode.progress) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator()
                CircularProgressIndicator(progress = { 0.7f })
            }
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            LinearProgressIndicator(progress = { 0.6f }, modifier = Modifier.fillMaxWidth())
        }

        DemoBox("Chip 标签", code = BasicCode.chip) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text("AssistChip") })
                FilterChip(selected = true, onClick = {}, label = { Text("FilterChip") })
                InputChip(selected = false, onClick = {}, label = { Text("InputChip") })
            }
        }

        DemoBox("Divider 分割线 & Spacer 空白", code = """
Text("内容上方")
HorizontalDivider()   // 水平分割线
Text("内容下方")
Spacer(Modifier.height(16.dp))  // 固定高度空白
Text("Spacer 之后（间距 16dp）")
        """.trimIndent()) {
            Text("内容上方")
            HorizontalDivider()
            Text("内容下方")
            Spacer(Modifier.height(16.dp))
            Text("Spacer 之后（间距 16dp）")
        }
    }
}
