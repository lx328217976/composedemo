package com.lory.mycomposedemo.demos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.airbnb.lottie.compose.*
import com.lory.mycomposedemo.DemoBox
import com.lory.mycomposedemo.DemoScaffold
import com.lory.mycomposedemo.ExplainCard

@Composable
fun ThirdPartyDemoScreen(onBack: () -> Unit) {
    DemoScaffold(title = "📦 第三方组件库", onBack = onBack) {

        // ===== COIL =====
        ExplainCard(
            title = "Coil — 图片加载库",
            content = "Coil（Coroutine Image Loader）是专为 Kotlin/Compose 设计的异步图片加载库。\n" +
                    "核心优势：\n" +
                    "• 完全基于 Kotlin 协程，轻量高效\n" +
                    "• 与 Compose 深度集成，提供 AsyncImage、SubcomposeAsyncImage\n" +
                    "• 内置内存缓存、磁盘缓存、BitmapPool\n" +
                    "• 支持 transformations（圆形、模糊、圆角等）\n\n" +
                    "依赖：implementation(\"io.coil-kt:coil-compose:2.x.x\")"
        )

        DemoBox("AsyncImage — 基础图片加载", code = """
// 依赖：implementation("io.coil-kt:coil-compose:2.x.x")

// 基础用法：直接传 URL 或 Uri
AsyncImage(
    model = "https://example.com/image.jpg",
    contentDescription = "图片描述",
    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)),
    contentScale = ContentScale.Crop    // 裁剪填充
)

// 圆形裁切
AsyncImage(
    model = "https://example.com/avatar.jpg",
    contentDescription = "头像",
    modifier = Modifier.size(60.dp).clip(CircleShape),
    contentScale = ContentScale.Crop
)

// 带 crossfade 淡入动画
AsyncImage(
    model = ImageRequest.Builder(LocalContext.current)
        .data("https://example.com/image.jpg")
        .crossfade(true)     // 加载完成后淡入
        .crossfade(300)      // 自定义淡入时长(ms)
        .build(),
    contentDescription = "图片"
)
        """.trimIndent()) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                // Basic AsyncImage
                AsyncImage(
                    model = "https://picsum.photos/seed/compose1/200/200",
                    contentDescription = "随机图片1",
                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                // Circle crop
                AsyncImage(
                    model = "https://picsum.photos/seed/compose2/200/200",
                    contentDescription = "圆形图片",
                    modifier = Modifier.size(80.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                // With request builder
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("https://picsum.photos/seed/compose3/200/200")
                        .crossfade(true)
                        .build(),
                    contentDescription = "带淡入动画",
                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Text("左：默认 | 中：圆形裁剪 | 右：crossfade 淡入", style = MaterialTheme.typography.bodySmall)
        }

        DemoBox("SubcomposeAsyncImage — 自定义加载/错误状态", code = """
// SubcomposeAsyncImage 可以精细控制各阶段的 UI
SubcomposeAsyncImage(
    model = "https://example.com/image.jpg",
    contentDescription = "图片",
    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)),
    loading = {
        // 加载中：显示进度条
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        }
    },
    error = {
        // 加载失败：显示错误图标
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.BrokenImage, null, tint = Color.Red)
        }
    },
    success = {
        // 可选：自定义成功状态（默认显示图片）
        SubcomposeAsyncImageContent()
    }
)
        """.trimIndent()) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Success
                SubcomposeAsyncImage(
                    model = "https://picsum.photos/seed/compose4/200/200",
                    contentDescription = "成功加载",
                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)),
                    loading = {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    },
                    error = {
                        Box(
                            Modifier.fillMaxSize().padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) { Text("❌ 加载失败", style = MaterialTheme.typography.labelSmall) }
                    }
                )
                // Force error
                SubcomposeAsyncImage(
                    model = "https://invalid.url/not-found.jpg",
                    contentDescription = "加载失败演示",
                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)),
                    loading = {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    },
                    error = {
                        Box(
                            Modifier.fillMaxSize().padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) { Text("❌ 加载失败", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error) }
                    }
                )
            }
            Text("SubcomposeAsyncImage 可以精细控制 loading / error 状态 UI", style = MaterialTheme.typography.bodySmall)
        }

        // ===== LOTTIE =====
        ExplainCard(
            title = "Lottie — 矢量动画库",
            content = "Lottie 是 Airbnb 开源的动画库，将 After Effects 导出的 JSON 动画文件渲染为原生动画。\n" +
                    "核心优势：\n" +
                    "• 设计师直接输出，代码零改动\n" +
                    "• 矢量渲染，无失真\n" +
                    "• 支持循环、速度控制、帧区间播放\n\n" +
                    "Compose API：\n" +
                    "• rememberLottieComposition()：加载动画文件\n" +
                    "• animateLottieCompositionAsState()：控制播放进度\n" +
                    "• LottieAnimation()：渲染动画\n\n" +
                    "依赖：implementation(\"com.airbnb.android:lottie-compose:6.x.x\")\n" +
                    "注意：需要在 assets 目录放入 .json 动画文件，下面演示使用网络 URL 加载。"
        )

        DemoBox("Lottie 动画演示", code = """
// 依赖：implementation("com.airbnb.android:lottie-compose:6.x.x")
// assets 目录放入 .json 文件，或使用网络 URL

// 1. 加载动画文件
val composition by rememberLottieComposition(
    LottieCompositionSpec.Asset("animation.json")   // 从 assets 加载
    // LottieCompositionSpec.Url("https://...")     // 从网络加载
    // LottieCompositionSpec.RawRes(R.raw.anim)    // 从 raw 资源加载
)

// 2. 控制播放进度
var isPlaying by remember { mutableStateOf(true) }
val progress by animateLottieCompositionAsState(
    composition = composition,
    isPlaying = isPlaying,
    speed = 1f,                              // 播放速度
    iterations = LottieConstants.IterateForever  // 无限循环
)

// 3. 渲染动画
LottieAnimation(
    composition = composition,
    progress = { progress },
    modifier = Modifier.size(200.dp)
)

// 播放控制
Button(onClick = { isPlaying = !isPlaying }) { Text("播放/暂停") }
        """.trimIndent()) {
            LottieDemo()
        }

        // ===== ACCOMPANIST =====
        ExplainCard(
            title = "Accompanist — Google 官方扩展库",
            content = "Accompanist 是 Google 提供的 Compose 实验性扩展库集合，提供尚未进入官方库的 API。\n" +
                    "常用模块：\n" +
                    "• accompanist-permissions：运行时权限请求\n" +
                    "• accompanist-systemuicontroller：控制状态栏/导航栏颜色（已逐渐被官方 API 替代）\n" +
                    "• accompanist-pager：HorizontalPager / VerticalPager（已合并入 Compose Foundation）\n" +
                    "• accompanist-placeholder：加载占位符 Shimmer 效果（部分功能已弃用）\n\n" +
                    "注意：部分 Accompanist 模块已正式并入 Compose，使用前先检查官方文档是否已有原生替代。"
        )

        DemoBox("Accompanist Permissions — 权限请求", code = """
// 依赖：implementation("com.google.accompanist:accompanist-permissions:0.x.x")

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPermissionScreen() {
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    val isGranted = cameraPermission.status == PermissionStatus.Granted
    val shouldShowRationale = (cameraPermission.status as? PermissionStatus.Denied)
        ?.shouldShowRationale == true

    when {
        isGranted -> {
            Text("✅ 权限已授予，可以使用相机")
        }
        shouldShowRationale -> {
            // 用户拒绝过一次，需要解释为什么需要权限
            Text("需要相机权限，请授予")
            Button(onClick = { cameraPermission.launchPermissionRequest() }) {
                Text("重新申请")
            }
        }
        else -> {
            Button(onClick = { cameraPermission.launchPermissionRequest() }) {
                Text("申请相机权限")
            }
        }
    }
}

// 多权限同时申请
val multiplePermissions = rememberMultiplePermissionsState(
    listOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
)
        """.trimIndent()) {
            AccompanistPermissionsDemo()
        }

        // HorizontalPager (now in Compose Foundation)
        ExplainCard(
            title = "HorizontalPager（已并入 Compose Foundation）",
            content = "原 accompanist-pager 的 HorizontalPager 已正式并入 androidx.compose.foundation。\n" +
                    "使用方式：\n" +
                    "val pagerState = rememberPagerState { pageCount }\n" +
                    "HorizontalPager(state = pagerState) { page -> ... }\n\n" +
                    "配合 TabRow + LaunchedEffect 实现 Tab + ViewPager 联动。"
        )

        DemoBox("HorizontalPager 轮播示例", code = """
// HorizontalPager 已并入 androidx.compose.foundation
// 不再需要 accompanist-pager 依赖

val pagerState = rememberPagerState { pageCount }

HorizontalPager(
    state = pagerState,
    modifier = Modifier.fillMaxWidth().height(200.dp)
) { page ->
    // 每一页的内容
    Box(
        Modifier.fillMaxSize().background(colors[page]),
        contentAlignment = Alignment.Center
    ) {
        Text("第 ${'$'}{page + 1} 页")
    }
}

// 页码指示器
Row(horizontalArrangement = Arrangement.Center) {
    repeat(pageCount) { i ->
        Box(
            Modifier.size(if (pagerState.currentPage == i) 10.dp else 8.dp)
                .background(
                    if (pagerState.currentPage == i) Color.Primary else Color.Gray,
                    CircleShape
                )
        )
    }
}

// 配合 Tab 联动：点击 Tab 跳转到对应页
val scope = rememberCoroutineScope()
TabRow(selectedTabIndex = pagerState.currentPage) {
    tabs.forEachIndexed { index, tab ->
        Tab(
            selected = pagerState.currentPage == index,
            onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
            text = { Text(tab) }
        )
    }
}
        """.trimIndent()) {
            HorizontalPagerDemo()
        }
    }
}

// ---- Lottie Demo ----

@Composable
private fun LottieDemo() {
    // 使用网络 URL 加载 Lottie 动画（lottiefiles.com 公开动画）
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Url("https://assets5.lottiefiles.com/packages/lf20_jcikwtux.json")
    )
    var isPlaying by remember { mutableStateOf(true) }
    var speed by remember { mutableFloatStateOf(1f) }
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying,
        speed = speed,
        iterations = LottieConstants.IterateForever
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (composition == null) {
            Box(Modifier.size(200.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(8.dp))
                    Text("加载动画中...", style = MaterialTheme.typography.bodySmall)
                }
            }
        } else {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(200.dp)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { isPlaying = !isPlaying }) {
                Text(if (isPlaying) "⏸ 暂停" else "▶ 播放")
            }
            OutlinedButton(onClick = { speed = if (speed == 1f) 2f else 1f }) {
                Text("速度: ${speed}x")
            }
        }
        Text("来源：lottiefiles.com 公开动画（需网络）", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// ---- Accompanist Permissions Demo ----

@OptIn(com.google.accompanist.permissions.ExperimentalPermissionsApi::class)
@Composable
private fun AccompanistPermissionsDemo() {
    val cameraPermissionState = com.google.accompanist.permissions.rememberPermissionState(
        android.Manifest.permission.CAMERA
    )
    val isGranted = cameraPermissionState.status == com.google.accompanist.permissions.PermissionStatus.Granted
    val shouldShowRationale = (cameraPermissionState.status as? com.google.accompanist.permissions.PermissionStatus.Denied)?.shouldShowRationale == true

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        when {
            isGranted -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("✅ 相机权限已授予", color = Color(0xFF388E3C), fontWeight = FontWeight.SemiBold)
                }
            }
            shouldShowRationale -> {
                Text("需要相机权限才能使用此功能")
                Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                    Text("重新申请权限")
                }
            }
            else -> {
                Text("点击按钮申请相机权限（accompanist-permissions）", style = MaterialTheme.typography.bodySmall)
                Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                    Text("申请相机权限")
                }
            }
        }
    }
}

// ---- HorizontalPager Demo ----

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun HorizontalPagerDemo() {
    val pages = listOf(
        Pair("🌅 第 1 页", Color(0xFFFFCDD2)),
        Pair("🌿 第 2 页", Color(0xFFDCEDC8)),
        Pair("🌊 第 3 页", Color(0xFFBBDEFB)),
        Pair("🌙 第 4 页", Color(0xFFE1BEE7)),
    )
    val pagerState = androidx.compose.foundation.pager.rememberPagerState { pages.size }

    Column {
        androidx.compose.foundation.pager.HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().height(100.dp)
        ) { page ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(pages[page].second),
                contentAlignment = Alignment.Center
            ) {
                Text(pages[page].first, style = MaterialTheme.typography.titleLarge)
            }
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            pages.indices.forEach { i ->
                val selected = pagerState.currentPage == i
                Box(
                    Modifier
                        .padding(4.dp)
                        .size(if (selected) 10.dp else 8.dp)
                        .background(
                            if (selected) MaterialTheme.colorScheme.primary else Color.LightGray,
                            CircleShape
                        )
                )
            }
        }
        Text(
            "左右滑动翻页，下方点指示器显示当前页",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

