package com.lory.mycomposedemo.demos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lory.mycomposedemo.DemoScaffold
import com.lory.mycomposedemo.DemoBox
import com.lory.mycomposedemo.ExplainCard
import kotlinx.coroutines.launch

data class ListItem(val id: Int, val title: String, val subtitle: String, val color: Color)

private val sampleItems = (1..30).map {
    ListItem(
        id = it,
        title = "条目 #$it",
        subtitle = "这是第 $it 个列表项的描述文字",
        color = Color(
            red = (50 + it * 7) % 200 + 50,
            green = (100 + it * 13) % 200 + 30,
            blue = (150 + it * 5) % 200 + 50
        )
    )
}

private object ListCode {
    val lazyColumn = """
data class Item(val id: Int, val title: String)
val items = (1..100).map { Item(it, "条目 #${'$'}it") }

LazyColumn(
    modifier = Modifier.fillMaxWidth().height(300.dp),
    contentPadding = PaddingValues(8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
) {
    // 提供 key 优化重组，避免错误的复用
    items(items = items, key = { it.id }) { item ->
        Card(Modifier.fillMaxWidth()) {
            Text(item.title, Modifier.padding(16.dp))
        }
    }
    // 单个固定条目（如 Header/Footer）
    item { Text("列表底部") }
}
    """.trimIndent()

    val lazyRow = """
LazyRow(
    contentPadding = PaddingValues(horizontal = 4.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
) {
    items(dataList, key = { it.id }) { item ->
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier.size(60.dp).background(item.color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("${'$'}{item.id}", color = Color.White)
            }
            Text(item.title, style = MaterialTheme.typography.labelSmall)
        }
    }
}
    """.trimIndent()

    val lazyGrid = """
// 固定列数网格
LazyVerticalGrid(
    columns = GridCells.Fixed(3),      // 固定 3 列
    modifier = Modifier.fillMaxWidth().height(240.dp),
    contentPadding = PaddingValues(4.dp),
    horizontalArrangement = Arrangement.spacedBy(6.dp),
    verticalArrangement = Arrangement.spacedBy(6.dp)
) {
    items(dataList, key = { it.id }) { item ->
        Box(
            Modifier.aspectRatio(1f)    // 保持正方形
                .background(item.color, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("${'$'}{item.id}", color = Color.White)
        }
    }
}

// 自适应列宽网格（每列至少 80dp）
LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 80.dp)) { ... }
    """.trimIndent()

    val stickyHeader = """
LazyColumn {
    // stickyHeader：粘性标题，滚动时固定在顶部
    stickyHeader {
        Box(Modifier.fillMaxWidth().background(Color.LightGray).padding(8.dp)) {
            Text("A 组", fontWeight = FontWeight.Bold)
        }
    }
    items(groupAItems) { item ->
        Text(item.name, Modifier.padding(16.dp))
    }

    stickyHeader {
        Box(Modifier.fillMaxWidth().background(Color.LightGray).padding(8.dp)) {
            Text("B 组", fontWeight = FontWeight.Bold)
        }
    }
    items(groupBItems) { item ->
        Text(item.name, Modifier.padding(16.dp))
    }
}
    """.trimIndent()

    val scrollState = """
// 获取滚动状态
val listState = rememberLazyListState()
val coroutineScope = rememberCoroutineScope()

// 程序控制滚动
Button(onClick = {
    coroutineScope.launch {
        listState.animateScrollToItem(0)      // 带动画滚到顶部
        // listState.scrollToItem(0)           // 直接跳转（无动画）
    }
})

// 监听滚动位置（用 derivedStateOf 避免频繁重组）
val firstVisible by remember {
    derivedStateOf { listState.firstVisibleItemIndex }
}

LazyColumn(state = listState) {
    items(dataList) { ... }
}
    """.trimIndent()
}

@Composable
fun ListDemoScreen(onBack: () -> Unit) {
    DemoScaffold(title = "📋 列表", onBack = onBack) {

        ExplainCard(
            title = "为什么要用 LazyColumn / LazyRow？",
            content = "普通 Column 会一次性渲染所有子元素，列表很长时会严重浪费性能甚至 OOM。\n" +
                    "LazyColumn / LazyRow 只渲染当前可见区域的 item，类似 RecyclerView 的回收复用机制，是 Compose 中处理长列表的标准方式。"
        )

        DemoBox("LazyColumn（垂直列表，前 8 项）", code = ListCode.lazyColumn) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().height(280.dp),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items = sampleItems.take(8), key = { it.id }) { item ->
                    ListItemCard(item)
                }
            }
        }

        ExplainCard(
            title = "key 参数的作用",
            content = "给 items() 提供 key lambda，Compose 可以精确追踪每个 item 的标识，" +
                    "在列表增删/重排时避免不必要的重组，保留 item 的动画和滚动位置，性能更好。"
        )

        DemoBox("LazyRow（水平列表）", code = ListCode.lazyRow) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sampleItems.take(10), key = { it.id }) { item ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            Modifier.size(60.dp).background(item.color, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${item.id}", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Text(item.title, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        ExplainCard(
            title = "LazyVerticalGrid — 网格列表",
            content = "LazyVerticalGrid 支持固定列数（GridCells.Fixed）或自适应列宽（GridCells.Adaptive）。\n" +
                    "注意：LazyVerticalGrid 不能直接嵌套在可滚动容器中，需要设置固定高度或使用 weight。"
        )

        DemoBox("LazyVerticalGrid（固定 3 列，前 9 项）", code = ListCode.lazyGrid) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxWidth().height(240.dp),
                contentPadding = PaddingValues(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(sampleItems.take(9), key = { it.id }) { item ->
                    Box(
                        Modifier.aspectRatio(1f).background(item.color, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${item.id}", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        ExplainCard(
            title = "分组 / 带标题的列表（stickyHeader）",
            content = "LazyColumn 支持 stickyHeader { } DSL，实现粘性标题（类似通讯录字母索引）。\n" +
                    "分组可以通过 item { Header() } + items { ... } 混合使用来实现。"
        )

        DemoBox("分组列表（带 stickyHeader）", code = ListCode.stickyHeader) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().height(240.dp),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (group in listOf("A 组", "B 组")) {
                    stickyHeader {
                        Box(
                            Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(group, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                    itemsIndexed(sampleItems.take(4)) { idx, item ->
                        ListItemCard(item.copy(title = "$group - 条目 ${idx + 1}"))
                    }
                }
            }
        }

        val listState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()

        ExplainCard(
            title = "LazyListState — 滚动状态与程序控制",
            content = "通过 rememberLazyListState() 获取滚动状态，可以读取首个可见 item 索引，" +
                    "也可以通过 coroutine 调用 animateScrollToItem() 或 scrollToItem() 程序控制滚动。"
        )

        DemoBox("程序控制滚动（共 ${sampleItems.size} 项）", code = ListCode.scrollState) {
            val firstVisibleIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
            Text("当前首个可见 item: #${firstVisibleIndex + 1}", style = MaterialTheme.typography.bodySmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { coroutineScope.launch { listState.animateScrollToItem(0) } }) { Text("回到顶部") }
                Button(onClick = { coroutineScope.launch { listState.animateScrollToItem(sampleItems.lastIndex) } }) { Text("跳到底部") }
            }
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sampleItems, key = { it.id }) { item -> ListItemCard(item) }
            }
        }
    }
}

@Composable
private fun ListItemCard(item: ListItem) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp).background(item.color, CircleShape), contentAlignment = Alignment.Center) {
                Text("${item.id}", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(item.title, fontWeight = FontWeight.SemiBold)
                Text(item.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
