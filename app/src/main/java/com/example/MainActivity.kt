package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.db.ChatMessageEntity
import com.example.db.StudyDocumentEntity
import com.example.ui.theme.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    VeroMainScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun VeroMainScreen(modifier: Modifier = Modifier) {
    val viewModel: VeroViewModel = viewModel()
    var onboardingCompleted by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = DeepBlack
    ) {
        if (!onboardingCompleted) {
            OnboardingScreen { onboardingCompleted = true }
        } else {
            WorkspaceScreen(viewModel)
        }
    }
}

// --- SCREEN 1: ONBOARDING SCREEN ---
@Composable
fun OnboardingScreen(onStartClicked: () -> Unit) {
    val scrollState = rememberScrollState()

    // Breathing light grid effect background
    val infiniteTransition = rememberInfiniteTransition(label = "Background pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulse alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DeepBlack, DarkPurpleBg, DeepBlack)
                )
            )
            .drawBehind {
                // Draw decorative grid
                val gridSize = 60.dp.toPx()
                val gridColor = Color(0xFF1E103E).copy(alpha = pulseAlpha)
                // Draw vertical lines
                var x = 0f
                while (x < size.width) {
                    drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
                    x += gridSize
                }
                // Draw horizontal lines
                var y = 0f
                while (y < size.height) {
                    drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
                    y += gridSize
                }
            }
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Pulsing Cyber Logo Container
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .drawBehind {
                        // Drawing circles as a customized modern technical icon
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(BrightPurple.copy(alpha = 0.4f), Color.Transparent)
                            ),
                            radius = size.width * 0.7f
                        )
                        drawCircle(
                            color = NeonPurple,
                            radius = size.width * 0.32f,
                            style = Stroke(width = 4.dp.toPx())
                        )
                        drawCircle(
                            color = CyberTeal,
                            radius = size.width * 0.22f,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                    .wrapContentSize(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.gemini_generated_image_k8w3edk8w3edk8w3),
                    contentDescription = "Academic Integrity Logo",
                    modifier = Modifier.size(68.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App Name with neon highlight
            Text(
                text = "VERO",
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                color = TextWhite,
                letterSpacing = 4.sp,
                modifier = Modifier.testTag("app_logo_title")
            )

            // Slogan
            Text(
                text = "Be real. Be you. Be honest.",
                fontSize = 18.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                color = NeonPurple,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Text(
                text = "Trợ lý ảo học tập thông minh thúc đẩy liêm chính học thuật",
                fontSize = 14.sp,
                color = TextGray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .widthIn(max = 340.dp)
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // core rule cards
            CoreRuleCard(
                icon = Icons.Default.Block,
                title = "KHÔNG LÀM BÀI HỘ",
                description = "VERO tuyệt đối không viết sẵn bài luận, không giải sẵn code hay toán hoàn tất từ A-Z để bạn chép trực tiếp. Bạn học thật, thi thật!"
            )

            CoreRuleCard(
                icon = Icons.Default.Quiz,
                title = "PHƯƠNG PHÁP QUY NẠP (SOCRATIC)",
                description = "Khi bạn gặp lỗi lập trình hay bài toán khó, VERO sẽ gỡ lỗi từng bước bằng ví dụ tương đương và hướng dẫn bạn tự rút ra nguyên lý."
            )

            CoreRuleCard(
                icon = Icons.Default.Campaign,
                title = "PHẢN BIỆN CHUYÊN SÂU",
                description = "Đóng vai trò 'Devil's Advocate' phản biện lại các lập luận trong dàn ý của bạn để tìm ra kẽ hở logic, rèn luyện tư duy độc lập."
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Ethical vs Cheating simulator bar
            EthicalSimulatorBar()

            Spacer(modifier = Modifier.height(32.dp))

            // Get Started Button
            Button(
                onClick = onStartClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .widthIn(max = 320.dp)
                    .testTag("start_button")
                    .border(1.dp, CyberTeal, RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrightPurple
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "BẮT ĐẦU HỌC TẬP",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Forward Icon",
                        tint = TextWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun CoreRuleCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, description: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 480.dp)
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceDarkPurple.copy(alpha = 0.8f)
        ),
        border = BorderStroke(1.dp, BrightPurple.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(NeonPurple.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = NeonPurple,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberTeal,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = TextGray,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun EthicalSimulatorBar() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 480.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceDarkPurple)
            .border(1.dp, Color(0xFF2C1E4A), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "HỌC TẬP HIỆU QUẢ CÙNG AI",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Process bar 1: Cheat
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Đạo văn / Để AI làm hộ", fontSize = 11.sp, color = TextGray)
            Text(text = "RỦI RO ĐÁNH TRƯỢT 100%", fontSize = 11.sp, color = AccentPink, fontWeight = FontWeight.Bold)
        }
        LinearProgressIndicator(
            progress = { 1f },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .height(4.dp),
            color = AccentPink,
            trackColor = Color(0xFF251322)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Process bar 2: Self learn
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Dùng VERO nâng cao tư duy", fontSize = 11.sp, color = TextGray)
            Text(text = "TỰ TIN TỰ HỌC 100%", fontSize = 11.sp, color = CyberTeal, fontWeight = FontWeight.Bold)
        }
        LinearProgressIndicator(
            progress = { 1f },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .height(4.dp),
            color = CyberTeal,
            trackColor = Color(0xFF13252E)
        )
    }
}


// --- SCREEN 2: MAIN WORKSPACE SCREEN (Adaptive Split-Screen) ---
@Composable
fun WorkspaceScreen(viewModel: VeroViewModel) {
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    val studyDocs by viewModel.studyDocuments.collectAsState()
    val chatMsgs by viewModel.chatMessages.collectAsState()
    val selectedDoc by viewModel.selectedDocument.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    val showBreathing by viewModel.showBreathingMode.collectAsState()
    val showDisclosure by viewModel.showDisclosureDialog.collectAsState()
    val disclosureText by viewModel.aiDisclosureText.collectAsState()

    var activeTabMobile by remember { mutableIntStateOf(0) } // 0: Study Hub, 1: Chat Hub
    var showAddDocDialog by remember { mutableStateOf(false) }

    // Selected citation pop-up state
    var selectedCitationText by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().background(DeepBlack)) {
            // Sleek Interface Top Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF130E26), DeepBlack)
                        )
                    )
                    .drawBehind {
                        drawLine(
                            color = Color.White.copy(alpha = 0.05f),
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "VERO",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = TextWhite,
                        modifier = Modifier.testTag("app_header_logo_title")
                    )
                    Text(
                        text = "BE REAL • BE YOU • BE HONEST",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekPurple,
                        letterSpacing = 1.5.sp
                    )
                }
                
                // Round glowing logo container for uploaded avatar asset
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .border(BorderStroke(1.dp, SleekPurple.copy(alpha = 0.3f)), CircleShape)
                        .background(Color(0x991E1E23)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = androidx.compose.ui.res.painterResource(id = R.drawable.gemini_generated_image_k8w3edk8w3edk8w3),
                        contentDescription = "VERO AI Logo",
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                if (isTablet) {
                    // Tablet Side-by-Side Split layout: 58% Left Study Panel, 42% Right Chat panel
                    Row(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.weight(0.58f)) {
                            StudyHubPanel(
                                documents = studyDocs,
                                selectedDoc = selectedDoc,
                                onDocSelect = { viewModel.selectDocument(it) },
                                onAddDocClicked = { showAddDocDialog = true },
                                onDeleteDoc = { viewModel.deleteDoc(it) }
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(1.dp)
                                .background(Color(0xFF1E1E24))
                        )
                        Box(modifier = Modifier.weight(0.42f)) {
                            ChatHubPanel(
                                messages = chatMsgs,
                                selectedDoc = selectedDoc,
                                isAiLoading = isAiLoading,
                                onSendMessage = { viewModel.sendMessage(it) },
                                onQuickSuggest = { viewModel.triggerQuickSuggestion(it) },
                                onTriggerDisclosure = { viewModel.generateDisclosure() },
                                onTriggerBreathing = { viewModel.startBreathing() },
                                onClearChat = { viewModel.clearHistory() },
                                onCitationTapped = { text -> selectedCitationText = text }
                        )
                    }
                }
            } else {
                // Mobile Tabbed Layout
                Column(modifier = Modifier.fillMaxSize()) {
                    // Glow Neon Tabs Header
                    TabRow(
                        selectedTabIndex = activeTabMobile,
                        containerColor = SurfaceDarkPurple,
                        contentColor = SleekPurple,
                        indicator = { tabPositions ->
                            if (activeTabMobile < tabPositions.size) {
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeTabMobile]),
                                    color = CyberTeal
                                )
                            }
                        }
                    ) {
                        Tab(
                            selected = activeTabMobile == 0,
                            onClick = { activeTabMobile = 0 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Tài liệu", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        )
                        Tab(
                            selected = activeTabMobile == 1,
                            onClick = { activeTabMobile = 1 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ChatBubble, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Vero Chat", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        if (activeTabMobile == 0) {
                            StudyHubPanel(
                                documents = studyDocs,
                                selectedDoc = selectedDoc,
                                onDocSelect = { viewModel.selectDocument(it) },
                                onAddDocClicked = { showAddDocDialog = true },
                                onDeleteDoc = { viewModel.deleteDoc(it) }
                            )
                        } else {
                            ChatHubPanel(
                                messages = chatMsgs,
                                selectedDoc = selectedDoc,
                                isAiLoading = isAiLoading,
                                onSendMessage = { viewModel.sendMessage(it) },
                                onQuickSuggest = { viewModel.triggerQuickSuggestion(it) },
                                onTriggerDisclosure = { viewModel.generateDisclosure() },
                                onTriggerBreathing = { viewModel.startBreathing() },
                                onClearChat = { viewModel.clearHistory() },
                                onCitationTapped = { text -> selectedCitationText = text }
                            )
                        }
                    }
                }
            }
        }
    }

    // --- SUB-SCREEN OVERLAYS ---

    // 1. Interactive 1-Minute Breathing Support
    if (showBreathing) {
        BreathingOverlay { viewModel.stopBreathing() }
    }

    // 2. AI Disclosure Transparency Dialog
    if (showDisclosure) {
        DisclosureDialog(
            text = disclosureText,
            onDismiss = { viewModel.closeDisclosure() }
        )
    }

    // 3. New Study Material Upload Drawer/Dialog
    if (showAddDocDialog) {
        AddDocumentDialog(
            onSave = { title, subject, content, citations ->
                viewModel.addNewDocument(title, subject, content, citations)
                showAddDocDialog = false
            },
            onDismiss = { showAddDocDialog = false }
        )
    }

    // 4. Citation source Hover Popup Modal
    selectedCitationText?.let { citation ->
        CitationPopupDialog(
            text = citation,
            onDismiss = { selectedCitationText = null }
        )
    }
}
}

// --- SUB-PANEL 1: STUDY HUB (Kênh Tài Liệu) ---
@Composable
fun StudyHubPanel(
    documents: List<StudyDocumentEntity>,
    selectedDoc: StudyDocumentEntity?,
    onDocSelect: (StudyDocumentEntity) -> Unit,
    onAddDocClicked: () -> Unit,
    onDeleteDoc: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    tint = CyberTeal,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "KÊNH TÀI LIỆU VERO",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    letterSpacing = 1.sp
                )
            }
            IconButton(
                onClick = onAddDocClicked,
                modifier = Modifier
                    .background(BrightPurple.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Thêm tài liệu",
                    tint = NeonPurple,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Document Selector Tabs
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(documents) { doc ->
                val isSelected = doc.id == selectedDoc?.id
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSelected) BrightPurple.copy(alpha = 0.25f) else SurfaceSlate.copy(
                                alpha = 0.6f
                            )
                        )
                        .border(
                            1.dp,
                            if (isSelected) CyberTeal else Color(0xFF2C1E4A),
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onDocSelect(doc) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "[${doc.subject}] ${doc.title}",
                            fontSize = 11.sp,
                            color = if (isSelected) TextWhite else TextGray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.widthIn(max = 180.dp)
                        )
                        if (doc.isCustom) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Xóa lý thuyết",
                                tint = AccentPink,
                                modifier = Modifier
                                    .size(13.dp)
                                    .clickable { onDeleteDoc(doc.id) }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main Document Content Viewer
        if (selectedDoc != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = SurfaceDarkPurple.copy(alpha = 0.7f)
                ),
                border = BorderStroke(1.dp, NeonPurple.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedDoc.subject.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberTeal,
                            letterSpacing = 1.sp
                        )
                        if (selectedDoc.isCustom) {
                            Badge(containerColor = BrightPurple) {
                                Text(
                                    "TÀI LIỆU KHOA",
                                    fontSize = 9.sp,
                                    color = TextWhite,
                                    modifier = Modifier.padding(2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = selectedDoc.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextWhite
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFF2C1E4A))
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Document text with highlighted citations
                    Text(
                        text = selectedDoc.content,
                        fontSize = 13.sp,
                        color = TextGray,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Quick references indicators guide
                    Text(
                        text = "DANH SÁCH CHỈ MỤC ĐỐI CHỨNG CHUẨN XÁC:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonPurple,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                    val adapter = moshi.adapter<List<String>>(
                        Types.newParameterizedType(List::class.java, String::class.java)
                    )
                    val citations = try {
                        adapter.fromJson(selectedDoc.citationSource) ?: emptyList()
                    } catch (e: Exception) {
                        emptyList()
                    }

                    citations.forEachIndexed { index, source ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(SurfaceSlate.copy(alpha = 0.5f))
                                .border(1.dp, Color(0xFF2C1E4A), RoundedCornerShape(6.dp))
                                .padding(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.Top) {
                                Badge(
                                    containerColor = CyberTeal.copy(alpha = 0.15f),
                                    contentColor = CyberTeal
                                ) {
                                    Text(
                                        "[${index + 1}]",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = source,
                                    fontSize = 11.sp,
                                    color = TextGray,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = TextMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Vui lòng chọn hoặc thêm tài liệu tự học!",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                }
            }
        }
    }
}


// --- SUB-PANEL 2: CHAT HUB (Hộp Chat VERO) ---
@Composable
fun ChatHubPanel(
    messages: List<ChatMessageEntity>,
    selectedDoc: StudyDocumentEntity?,
    isAiLoading: Boolean,
    onSendMessage: (String) -> Unit,
    onQuickSuggest: (String) -> Unit,
    onTriggerDisclosure: () -> Unit,
    onTriggerBreathing: () -> Unit,
    onClearChat: () -> Unit,
    onCitationTapped: (String) -> Unit
) {
    var rawInputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    var showAttachOptions by remember { mutableStateOf(false) }
    var mockSelectedFileName by remember { mutableStateOf<String?>(null) }

    // Scroll to bottom on new message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
            .padding(12.dp)
    ) {
        // Chat Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Customized VERO glowing chatbot avatar
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                colors = listOf(BrightPurple, CyberTeal, BrightPurple)
                            )
                        )
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(DeepBlack),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = androidx.compose.ui.res.painterResource(id = R.drawable.gemini_generated_image_k8w3edk8w3edk8w3),
                        contentDescription = "VERO AI",
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "VERO ASSISTANT",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextWhite,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Be real. Be you. Be honest.",
                        fontSize = 9.sp,
                        color = CyberTeal,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            IconButton(
                onClick = onClearChat,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Xóa hội thoại",
                    tint = TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Chat Dialog Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (messages.isEmpty()) {
                // Friendly Empty State Hello VERO Panel
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = NeonPurple.copy(alpha = 0.4f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Học Tập Minh Bạch & Đạo Đức",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "VERO giúp bạn tự lập dàn ý, tìm công thức, phương pháp và gỡ lỗi lập trình. Trò chuyện ngay để nhận hỗ trợ học thuật chuẩn mực!",
                            fontSize = 12.sp,
                            color = TextGray,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(messages) { message ->
                        ChatBubbleRow(message, selectedDoc, onCitationTapped)
                    }

                    if (isAiLoading) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = NeonPurple,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "VERO đang soạn tư vấn học thuật...",
                                    fontSize = 11.sp,
                                    color = TextMuted,
                                    fontStyle = FontStyle.Italic
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Suggestion Socratic Quick Pills Category Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val categorisedPills = listOf(
                "Lập dàn ý bài" to "Chào VERO, mình đang tìm hiểu tài liệu này. Giúp mình lập dàn bài chi tiết để viết nghị luận học thuật được không?",
                "Gợi ý giải bài" to "Làm sao tìm được giới hạn trong bài toán này? Đừng giải ra đáp số cuối cùng nhé, hãy gợi ý cho mình phương pháp và ví dụ tương tự thôi.",
                "Gỡ lỗi Code" to "Mình đang code bài này bị lỗi logic, VERO hãy đóng vai Socratic gợi mở cho mình từng lỗi nhỏ thay vì đưa giải pháp đầy đủ nhé.",
                "Phản biện bài" to "Đóng vai phản biện (Devil's Advocate) vạch lá tìm sâu giúp mình 3 điểm hạn chế nhất trong cấu trúc suy luận của mình.",
                "Xem trích dẫn" to "Vero ơi, hướng dẫn mình ghi chú trích dẫn APA chuẩn xác phòng tránh đạo văn tài liệu này."
            )
            items(categorisedPills) { pair ->
                val label = pair.first
                val prompt = pair.second
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(50.dp))
                        .clickable { onQuickSuggest(prompt) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(text = label, fontSize = 11.sp, color = TextGray, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Document attachment mock panel
        mockSelectedFileName?.let { name ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF20133A))
                    .border(1.dp, CyberTeal.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(14.dp), tint = CyberTeal)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Đã kèm: $name (Sẵn sàng gửi)",
                        fontSize = 11.sp,
                        color = TextWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Bỏ tệp đính kèm",
                    tint = AccentPink,
                    modifier = Modifier
                        .size(14.dp)
                        .clickable { mockSelectedFileName = null }
                )
            }
        }

        // Expanded Attachment bar options
        if (showAttachOptions) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 8.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceDarkPurple)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                MockAttachItem(
                    icon = Icons.Default.Folder,
                    label = "Tải File máy và PDF",
                    onClick = {
                        mockSelectedFileName = "BaiTapLon_KhaiCuoc.pdf"
                        showAttachOptions = false
                    }
                )
                MockAttachItem(
                    icon = Icons.Default.CloudQueue,
                    label = "Google Drive Link",
                    onClick = {
                        mockSelectedFileName = "Tailieu_LuanVan_Drive.docx"
                        showAttachOptions = false
                    }
                )
                MockAttachItem(
                    icon = Icons.Default.Language,
                    label = "Bài Báo Khoa học URL",
                    onClick = {
                        mockSelectedFileName = "https://scholar.google.com/art1204"
                        showAttachOptions = false
                    }
                )
            }
        }

        // Prompt user Input bar - Styled in Sleek Interface glassmorphic design
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.05f))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { showAttachOptions = !showAttachOptions },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AttachFile,
                    contentDescription = "Đính kèm tệp học thuật",
                    tint = if (showAttachOptions) CyberTeal else TextGray,
                    modifier = Modifier.size(20.dp)
                )
            }

            TextField(
                value = rawInputText,
                onValueChange = { rawInputText = it },
                modifier = Modifier
                    .weight(1f)
                    .testTag("prompt_input"),
                placeholder = {
                    Text(
                        "Đặt câu hỏi hoặc chia sẻ ý tưởng giải...",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                ),
                maxLines = 3,
                textStyle = TextStyle(fontSize = 13.sp)
            )

            IconButton(
                onClick = {
                    if (rawInputText.isNotBlank()) {
                        val toSend = if (mockSelectedFileName != null) {
                            " [Kèm tệp: $mockSelectedFileName]\n$rawInputText"
                        } else {
                            rawInputText
                        }
                        onSendMessage(toSend)
                        rawInputText = ""
                        mockSelectedFileName = null
                    }
                },
                modifier = Modifier
                    .size(40.dp)
                    .background(SleekPurple, RoundedCornerShape(12.dp))
                    .testTag("send_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = "Gửi tin học tập",
                    tint = TextWhite,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Academic bottom quick utility belt
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Transparency disclosure creator
            Button(
                onClick = onTriggerDisclosure,
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceDarkPurple),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, CyberTeal.copy(alpha = 0.5f)),
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .padding(end = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = CyberTeal
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Khai báo minh bạch AI", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                }
            }

            // Stressed breath short rest suggesting
            Button(
                onClick = onTriggerBreathing,
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceDarkPurple),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, NeonPurple.copy(alpha = 0.5f)),
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .padding(start = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SelfImprovement,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = NeonPurple
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Nghỉ ngắn 1 phút", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                }
            }
        }
    }
}

@Composable
fun MockAttachItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = CyberTeal)
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, fontSize = 10.sp, color = TextWhite)
    }
}


// --- ATOMS: CHAT BUBBLES ---
@Composable
fun ChatBubbleRow(
    message: ChatMessageEntity,
    selectedDoc: StudyDocumentEntity?,
    onCitationTapped: (String) -> Unit
) {
    val isUser = message.role == "user"

    val moshi = remember { Moshi.Builder().add(KotlinJsonAdapterFactory()).build() }
    val adapter = remember {
        moshi.adapter<List<String>>(
            Types.newParameterizedType(List::class.java, String::class.java)
        )
    }

    val citations = remember(selectedDoc) {
        try {
            selectedDoc?.citationSource?.let { adapter.fromJson(it) } ?: emptyList()
        } catch (e: Exception) {
            emptyList<String>()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            // VERO Avatar marker
            Box(
                modifier = Modifier
                    .padding(end = 6.dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(NeonPurple.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.gemini_generated_image_k8w3edk8w3edk8w3),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }
        }

        // Bubble shape & colors with Sleek Interface spec
        val shape = if (isUser) {
            RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp)
        } else {
            RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)
        }

        val border = if (isUser) {
            BorderStroke(1.dp, Color(0x33818CF8)) // border-indigo-500/20 in Tailwind
        } else {
            BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)) // border-white/5 in Tailwind
        }

        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(shape)
                .then(
                    if (isUser) {
                        Modifier.background(Color(0x334F46E5)) // bg-indigo-600/20 in Tailwind
                    } else {
                        Modifier.background(
                            Brush.linearGradient(
                                colors = listOf(ChatGradientStart, ChatGradientEnd)
                            )
                        )
                    }
                )
                .border(border, shape)
                .padding(14.dp)
        ) {
            Column {
                if (isUser) {
                    Text(
                        text = message.text,
                        fontSize = 13.sp,
                        color = TextWhite,
                        lineHeight = 18.sp
                    )
                } else {
                    // Render VERO response with parsed interactive citations as superscript
                    VeroSmartText(
                        text = message.text,
                        citations = citations,
                        onCitationTapped = onCitationTapped
                    )
                }
            }
        }
    }
}

// Custom parser to handle citations or superscript markers and show dialog popups
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VeroSmartText(text: String, citations: List<String>, onCitationTapped: (String) -> Unit) {
    // Regex splits text into plain texts, bracket indexes (e.g. [1], [2]), and code blocks
    val segments = remember(text) {
        val parts = mutableListOf<TextSegment>()
        var lastIdx = 0
        val regex = Regex("""(\[\d+\]|```[\s\S]*?```)""")
        val matches = regex.findAll(text)

        for (match in matches) {
            if (match.range.first > lastIdx) {
                parts.add(TextSegment.Plain(text.substring(lastIdx, match.range.first)))
            }
            val matchValue = match.value
            if (matchValue.startsWith("```")) {
                parts.add(TextSegment.Code(matchValue))
            } else {
                val num = matchValue.trim('[', ']').toIntOrNull()
                parts.add(TextSegment.Citation(matchValue, num))
            }
            lastIdx = match.range.last + 1
        }
        if (lastIdx < text.length) {
            parts.add(TextSegment.Plain(text.substring(lastIdx)))
        }
        parts
    }

    FlowRow(
        modifier = Modifier.fillMaxWidth()
    ) {
        segments.forEach { segment ->
            when (segment) {
                is TextSegment.Plain -> {
                    Text(
                        text = segment.content,
                        fontSize = 13.sp,
                        color = TextWhite,
                        lineHeight = 18.sp
                    )
                }
                is TextSegment.Code -> {
                    val codeContent = segment.content.trim('`').removePrefix("cpp").removePrefix("c").trim()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DeepBlack)
                            .border(1.dp, BrightPurple.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("C++ Code", fontSize = 10.sp, color = CyberTeal, fontWeight = FontWeight.Bold)
                            val clipboard = LocalClipboardManager.current
                            val context = LocalContext.current
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy code",
                                tint = TextGray,
                                modifier = Modifier
                                    .size(14.dp)
                                    .clickable {
                                        clipboard.setText(AnnotatedString(codeContent))
                                        Toast.makeText(context, "Đã sao chép thuật toán!", Toast.LENGTH_SHORT).show()
                                    }
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = codeContent,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = NeonPurple,
                            lineHeight = 15.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "⚠️ Socratic Reminder: Hãy đảm bảo bạn đã thấu hiểu giải thuật trước khi áp dụng.",
                            fontSize = 9.sp,
                            color = AccentPink,
                            fontStyle = FontStyle.Italic,
                            lineHeight = 12.sp
                        )
                    }
                }
                is TextSegment.Citation -> {
                    val citeNum = segment.index
                    val citeContent = if (citeNum != null && citations.size >= citeNum) {
                        citations[citeNum - 1]
                    } else {
                        "Nguồn tham khảo dữ liệu thực chứng từ tài liệu học tập giảng đường."
                    }

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(CyberTeal.copy(alpha = 0.15f))
                            .clickable { onCitationTapped(citeContent) }
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = segment.label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberTeal
                        )
                    }
                }
            }
        }
    }
}

sealed class TextSegment {
    data class Plain(val content: String) : TextSegment()
    data class Code(val content: String) : TextSegment()
    data class Citation(val label: String, val index: Int?) : TextSegment()
}


// --- DIALOGS & OVERLAYS ---

// 1. Interactive Overlaid 1-Minute Rest Breathing Orb Support
@Composable
fun BreathingOverlay(onDismiss: () -> Unit) {
    var breathingPhase by remember { mutableIntStateOf(0) } // 0: Inhale, 1: Hold, 2: Exhale
    var secondsLeft by remember { mutableIntStateOf(60) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            while (secondsLeft > 0) {
                delay(1000)
                secondsLeft--
                // Rotate rules every 4 seconds cycle
                val cyclePos = (60 - secondsLeft) % 12
                breathingPhase = when {
                    cyclePos < 4 -> 0
                    cyclePos < 8 -> 1
                    else -> 2
                }
            }
            onDismiss()
        }
    }

    // Interactive Orb Scale Pulse Animation
    val orbScale by animateFloatAsState(
        targetValue = when (breathingPhase) {
            0 -> 1.35f
            1 -> 1.35f
            else -> 0.85f
        },
        animationSpec = tween(4000, easing = EaseInOutSine),
        label = "Orb pulse scaling"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.92f))
            .clickable { /* Block gestures */ }
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "HỖ TRỢ TINH THẦN ĐỘC LẬP",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = NeonPurple,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Thời gian nghỉ dưỡng não bộ: ${secondsLeft}s",
                fontSize = 14.sp,
                color = TextWhite
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Glowing Pulsing Orb Visual Space
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .drawBehind {
                        // Ambient outer neon shadows
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    if (breathingPhase == 0) CyberTeal.copy(alpha = 0.5f) else BrightPurple.copy(alpha = 0.5f),
                                    Color.Transparent
                                )
                            ),
                            radius = size.width * orbScale
                        )

                        // Core Solid Orb
                        drawCircle(
                            color = if (breathingPhase == 0) CyberTeal else BrightPurple,
                            radius = size.width * 0.35f * orbScale
                        )

                        // Outer ring indicator
                        drawCircle(
                            color = TextWhite.copy(alpha = 0.5f),
                            radius = size.width * 0.45f,
                            style = Stroke(width = 1.dp.toPx())
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (breathingPhase) {
                        0 -> "HÍT VÀO\n(Nhẹ nhàng)"
                        1 -> "NÍN THỞ\n(Tĩnh tâm)"
                        else -> "THỞ RA\n(Giải tỏa)"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "💡 'Tự nỗ lực vượt qua thử thách giúp bạn vững vàng hơn. Đừng áp lực việc giải bài lập tức, hít thở và bắt đầu lại nhé!'",
                fontSize = 13.sp,
                color = TextGray,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.widthIn(max = 320.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceDarkPurple),
                border = BorderStroke(1.dp, CyberTeal),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Trở lại học tập VERO", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// 2. AI Disclosure Presentation Dialog
@Composable
fun DisclosureDialog(text: String, onDismiss: () -> Unit) {
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = CyberTeal)
                Spacer(modifier = Modifier.width(8.dp))
                Text("KHAI BÁO MINH BẠCH VERO", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                Text(
                    text = "Hãy sao chép đoạn khai báo phía dưới và dán vào phần Phụ lục (Appendix) bài làm để minh chứng liêm chính khoa học của bạn.",
                    fontSize = 12.sp,
                    color = TextGray,
                    modifier = Modifier.padding(bottom = 12.dp),
                    lineHeight = 16.sp
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(DeepBlack)
                        .border(1.dp, Color(0xFF2C1E4A), RoundedCornerShape(8.dp))
                        .verticalScroll(rememberScrollState())
                        .padding(12.dp)
                ) {
                    Text(
                        text = text,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF4EEF6D),
                        lineHeight = 16.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    clipboard.setText(AnnotatedString(text))
                    Toast.makeText(context, "Đã sao chép báo cáo minh bạch!", Toast.LENGTH_SHORT).show()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrightPurple)
            ) {
                Text("SAO CHÉP & ĐÓNG", color = TextWhite)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("HỦY", color = TextGray)
            }
        },
        containerColor = SurfaceDarkPurple,
        textContentColor = TextWhite,
        titleContentColor = TextWhite
    )
}

// 3. Add Custom Lecture Note Material Dialog
@Composable
fun AddDocumentDialog(
    onSave: (String, String, String, List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    var citation1 by remember { mutableStateOf("") }
    var citation2 by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MenuBook, contentDescription = null, tint = CyberTeal)
                Spacer(modifier = Modifier.width(8.dp))
                Text("THÊM BÀI GIẢNG / TÀI LIỆU", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Tiêu đề bài giảng", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberTeal,
                        unfocusedBorderColor = Color(0xFF2C1E4A),
                        focusedLabelColor = CyberTeal,
                        unfocusedLabelColor = TextGray,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Môn học (Bộ môn, ví dụ: Toán, Lý)", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberTeal,
                        unfocusedBorderColor = Color(0xFF2C1E4A),
                        focusedLabelColor = CyberTeal,
                        unfocusedLabelColor = TextGray,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Nội dung chi tiết tài liệu", fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberTeal,
                        unfocusedBorderColor = Color(0xFF2C1E4A),
                        focusedLabelColor = CyberTeal,
                        unfocusedLabelColor = TextGray,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                Text(
                    text = "Dữ liệu chứng thực đối chiếu (Citations):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonPurple
                )

                OutlinedTextField(
                    value = citation1,
                    onValueChange = { citation1 = it },
                    label = { Text("Trích dẫn dòng [1]", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberTeal,
                        unfocusedBorderColor = Color(0xFF2C1E4A),
                        focusedLabelColor = CyberTeal,
                        unfocusedLabelColor = TextGray,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                OutlinedTextField(
                    value = citation2,
                    onValueChange = { citation2 = it },
                    label = { Text("Trích dẫn dòng [2]", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberTeal,
                        unfocusedBorderColor = Color(0xFF2C1E4A),
                        focusedLabelColor = CyberTeal,
                        unfocusedLabelColor = TextGray,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && subject.isNotBlank() && content.isNotBlank()) {
                        val citations = mutableListOf<String>()
                        if (citation1.isNotBlank()) citations.add(citation1)
                        if (citation2.isNotBlank()) citations.add(citation2)
                        onSave(title, subject, content, citations)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrightPurple)
            ) {
                Text("LƯU TÀI LIỆU", color = TextWhite)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("HỦY", color = TextGray)
            }
        },
        containerColor = SurfaceDarkPurple,
        textContentColor = TextWhite,
        titleContentColor = TextWhite
    )
}

// 4. Citation Content Dialog Pop-up
@Composable
fun CitationPopupDialog(text: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = CyberTeal)
                Spacer(modifier = Modifier.width(8.dp))
                Text("MINH CHỨNG TÀI LIỆU GỐC", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CyberTeal)
            }
        },
        text = {
            Text(
                text = text,
                fontSize = 13.sp,
                color = TextWhite,
                lineHeight = 18.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceSlate)
            ) {
                Text("ĐÃ HIỂU", color = TextWhite)
            }
        },
        containerColor = SurfaceDarkPurple,
        textContentColor = TextWhite,
        titleContentColor = TextWhite
    )
}
