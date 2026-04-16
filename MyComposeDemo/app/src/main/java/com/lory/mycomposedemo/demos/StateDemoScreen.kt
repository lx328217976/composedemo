package com.lory.mycomposedemo.demos

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lory.mycomposedemo.DemoBox
import com.lory.mycomposedemo.DemoScaffold
import com.lory.mycomposedemo.ExplainCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// ViewModel for state demo
class CounterViewModel : ViewModel() {
    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> = _count.asStateFlow()

    fun increment() { _count.value++ }
    fun decrement() { _count.value-- }
    fun reset() { _count.value = 0 }
}

private object StateCode {
    val remember = """
// remember：重组期间保持状态，配置变化（旋转）后丢失
var count by remember { mutableIntStateOf(0) }

// rememberSaveable：保存到 Bundle，旋转屏幕后也能恢复
var count by rememberSaveable { mutableIntStateOf(0) }

// 使用
Button(onClick = { count++ }) { Text("点击 +1") }
Text("当前：${'$'}count")
    """.trimIndent()

    val hoisting = """
// ❌ 不推荐：状态耦合在子组件内
@Composable
fun StatefulTextField() {
    var text by remember { mutableStateOf("") }
    TextField(value = text, onValueChange = { text = it })
}

// ✅ 推荐：状态提升到父组件
@Composable
fun StatelessTextField(value: String, onValueChange: (String) -> Unit) {
    TextField(value = value, onValueChange = onValueChange)
}

// 父组件持有状态
@Composable
fun Parent() {
    var text by remember { mutableStateOf("") }
    StatelessTextField(value = text, onValueChange = { text = it })
    Text("父组件读取：${'$'}text")
}
    """.trimIndent()

    val viewModel = """
// ViewModel（旋转屏幕后状态不丢失）
class CounterViewModel : ViewModel() {
    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> = _count.asStateFlow()

    fun increment() { _count.value++ }
    fun decrement() { _count.value-- }
}

@Composable
fun CounterScreen(vm: CounterViewModel = viewModel()) {
    val count by vm.count.collectAsState()

    Text("${'$'}count", style = MaterialTheme.typography.headlineMedium)
    Button(onClick = { vm.increment() }) { Text("+1") }
}
    """.trimIndent()

    val derived = """
var input by remember { mutableStateOf("") }

// derivedStateOf：缓存派生计算结果，避免不必要的重组
// 只有 input 变化时才重新计算 isEven
val isEven by remember {
    derivedStateOf {
        input.toIntOrNull()?.let { it % 2 == 0 }
    }
}

OutlinedTextField(value = input, onValueChange = { input = it })
when (isEven) {
    true  -> Text("✅ 偶数")
    false -> Text("⚠️ 奇数")
    null  -> Text("请输入数字")
}
    """.trimIndent()

    val sideEffects = """
// LaunchedEffect：key 变化时取消旧协程并重新启动
var isRunning by remember { mutableStateOf(false) }
var seconds by remember { mutableIntStateOf(0) }

LaunchedEffect(isRunning) {
    if (isRunning) {
        while (true) {
            delay(1000L)
            seconds++
        }
    }
}

// SideEffect：每次成功重组后同步执行（无协程）
SideEffect {
    // 同步非 Compose 状态（如 Firebase analytics 等）
    analytics.setScreenName("HomeScreen")
}

// DisposableEffect：有清理逻辑的副作用
DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { ... }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose {
        lifecycleOwner.lifecycle.removeObserver(observer) // 离开时清理
    }
}

// rememberCoroutineScope：事件驱动的协程（如按钮点击）
val scope = rememberCoroutineScope()
Button(onClick = {
    scope.launch { /* 在点击事件中启动协程 */ }
})
    """.trimIndent()
}

@Composable
fun StateDemoScreen(onBack: () -> Unit) {
    DemoScaffold(title = "🔄 状态管理与重组", onBack = onBack) {

        ExplainCard(
            title = "remember vs rememberSaveable",
            content = "• remember：在重组（recomposition）期间保持状态，但旋转屏幕/Activity 重建后丢失\n" +
                    "• rememberSaveable：会自动保存到 Bundle，旋转屏幕后也能恢复\n" +
                    "• 基础类型（Int、String 等）自动支持；自定义类型需要实现 Saver"
        )

        var rememberCount by remember { mutableIntStateOf(0) }
        var saveableCount by rememberSaveable { mutableIntStateOf(0) }
        DemoBox("remember vs rememberSaveable（旋转屏幕后对比差异）", code = StateCode.remember) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("remember", style = MaterialTheme.typography.labelMedium)
                    Text("$rememberCount", style = MaterialTheme.typography.headlineMedium)
                    Button(onClick = { rememberCount++ }) { Text("+1") }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("rememberSaveable", style = MaterialTheme.typography.labelMedium)
                    Text("$saveableCount", style = MaterialTheme.typography.headlineMedium)
                    Button(onClick = { saveableCount++ }) { Text("+1") }
                }
            }
        }

        ExplainCard(
            title = "状态提升（State Hoisting）",
            content = "将状态从子组件提升到父组件，子组件只接收 value 和 onValueChange。\n" +
                    "好处：① 单一数据源 ② 父组件可拦截事件 ③ 子组件无状态，更易测试复用\n\n" +
                    "规律：状态应放在使用它的所有组件的最低公共父节点。"
        )
        var hoistedText by remember { mutableStateOf("") }
        DemoBox("状态提升示例：输入框状态由父组件持有", code = StateCode.hoisting) {
            OutlinedTextField(
                value = hoistedText,
                onValueChange = { hoistedText = it },
                label = { Text("子组件（无状态）") },
                modifier = Modifier.fillMaxWidth()
            )
            Text("父组件读取到的值：「$hoistedText」", style = MaterialTheme.typography.bodySmall)
        }

        ExplainCard(
            title = "ViewModel + StateFlow",
            content = "ViewModel 在配置变化（旋转等）时存活，适合存放 UI 状态和业务逻辑。\n" +
                    "推荐使用 StateFlow / LiveData 暴露状态，在 Compose 中用 collectAsState() 订阅。"
        )
        val counterVm: CounterViewModel = viewModel()
        val vmCount by counterVm.count.collectAsState()
        DemoBox("ViewModel 计数器（旋转屏幕后不丢失）", code = StateCode.viewModel) {
            Text("ViewModel count: $vmCount", style = MaterialTheme.typography.headlineMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { counterVm.decrement() }) { Text("-1") }
                Button(onClick = { counterVm.increment() }) { Text("+1") }
                OutlinedButton(onClick = { counterVm.reset() }) { Text("重置") }
            }
        }

        ExplainCard(
            title = "derivedStateOf — 派生状态",
            content = "当某个状态需要通过计算从其他状态派生出来时，使用 derivedStateOf。\n" +
                    "它会缓存计算结果，只有依赖的状态变化时才重新计算，避免不必要的重组。"
        )
        var inputNumber by remember { mutableStateOf("") }
        val isEven by remember { derivedStateOf { inputNumber.toIntOrNull()?.let { it % 2 == 0 } } }
        DemoBox("derivedStateOf：判断输入数字奇偶", code = StateCode.derived) {
            OutlinedTextField(
                value = inputNumber,
                onValueChange = { inputNumber = it },
                label = { Text("输入数字") },
                modifier = Modifier.fillMaxWidth()
            )
            when (isEven) {
                true -> Text("✅ 偶数", color = MaterialTheme.colorScheme.primary)
                false -> Text("⚠️ 奇数", color = MaterialTheme.colorScheme.error)
                null -> Text("请输入有效数字", style = MaterialTheme.typography.bodySmall)
            }
        }

        ExplainCard(
            title = "副作用（Side Effects）",
            content = "• LaunchedEffect(key)：在 Composition 中启动协程，key 变化时重新执行\n" +
                    "• SideEffect：每次重组后同步执行，用于同步非 Compose 状态\n" +
                    "• DisposableEffect(key)：有清理逻辑的副作用，离开时执行 onDispose\n" +
                    "• rememberCoroutineScope：获取绑定到组合生命周期的协程 Scope"
        )
        var timerSeconds by remember { mutableIntStateOf(0) }
        var timerRunning by remember { mutableStateOf(false) }
        LaunchedEffect(timerRunning) {
            if (timerRunning) { while (true) { delay(1000L); timerSeconds++ } }
        }
        DemoBox("LaunchedEffect 计时器", code = StateCode.sideEffects) {
            Text("已计时：${timerSeconds} 秒", style = MaterialTheme.typography.headlineSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { timerRunning = !timerRunning }) { Text(if (timerRunning) "暂停" else "开始") }
                OutlinedButton(onClick = { timerRunning = false; timerSeconds = 0 }) { Text("重置") }
            }
            Text("key=timerRunning 变化时 LaunchedEffect 重新启动", style = MaterialTheme.typography.bodySmall)
        }

        ExplainCard(
            title = "重组（Recomposition）优化建议",
            content = "① 状态读取尽量推迟到最近的使用位置，减小重组范围\n" +
                    "② 使用 key() 给可重排 item 提供稳定标识\n" +
                    "③ 避免在 Composable 函数体中做耗时运算，用 remember 缓存\n" +
                    "④ @Stable、@Immutable 注解帮助 Compose 跳过不必要的重组"
        )
    }
}

@Composable
private fun HoistedTextField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("子组件（无状态）") },
        modifier = Modifier.fillMaxWidth()
    )
}
