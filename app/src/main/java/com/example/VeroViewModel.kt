package com.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.Content
import com.example.api.GeminiRepository
import com.example.api.Part
import com.example.db.ChatMessageEntity
import com.example.db.StudyDocumentEntity
import com.example.db.VeroRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class VeroViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = VeroRepository(application)

    val chatMessages: StateFlow<List<ChatMessageEntity>> = repository.chatMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studyDocuments: StateFlow<List<StudyDocumentEntity>> = repository.studyDocuments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedDocument = MutableStateFlow<StudyDocumentEntity?>(null)
    val selectedDocument = _selectedDocument.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading = _isAiLoading.asStateFlow()

    private val _showBreathingMode = MutableStateFlow(false)
    val showBreathingMode = _showBreathingMode.asStateFlow()

    private val _showDisclosureDialog = MutableStateFlow(false)
    val showDisclosureDialog = _showDisclosureDialog.asStateFlow()

    private val _aiDisclosureText = MutableStateFlow("")
    val aiDisclosureText = _aiDisclosureText.asStateFlow()

    init {
        viewModelScope.launch {
            repository.checkAndSeedDocuments()
            // Select first document by default
            studyDocuments.collectFirst { docs ->
                if (docs.isNotEmpty() && _selectedDocument.value == null) {
                    _selectedDocument.value = docs.first()
                }
            }
        }
    }

    private fun <T> Flow<T>.collectFirst(action: suspend (T) -> Unit) {
        viewModelScope.launch {
            this@collectFirst.firstOrNull()?.let { action(it) }
        }
    }

    fun selectDocument(document: StudyDocumentEntity) {
        _selectedDocument.value = document
    }

    fun startBreathing() {
        _showBreathingMode.value = true
    }

    fun stopBreathing() {
        _showBreathingMode.value = false
    }

    fun closeDisclosure() {
        _showDisclosureDialog.value = false
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        viewModelScope.launch {
            // Save user message
            repository.insertMessage("user", text)

            // Check if user is stressed/tired for emotional support suggestion
            val lowerText = text.lowercase()
            if (lowerText.contains("mệt") || lowerText.contains("nản") || lowerText.contains("stress") || lowerText.contains("mỏi")) {
                startBreathing()
            }

            _isAiLoading.value = true

            // Gather conversation history
            val dbHistory = chatMessages.value.filter { it.role != "system" }.takeLast(10)
            val apiHistory = dbHistory.map { msg ->
                Content(parts = listOf(Part(text = msg.text)))
            }

            // Inject current document context into system instruction if available
            val docContext = selectedDocument.value?.let { doc ->
                "\nTÀI LIỆU THAM KHẢO HIỆN TẠI:\nBộ môn: ${doc.subject}\nTiêu đề: ${doc.title}\nNội dung chính:\n${doc.content}\n"
            } ?: ""

            val finalSystemInstruction = VERO_SYSTEM_INSTRUCTION + docContext

            // Call API
            val result = repository.getVeroAiResponse(text, apiHistory, finalSystemInstruction)
            _isAiLoading.value = false

            result.onSuccess { reply ->
                repository.insertMessage("vero", reply)
            }.onFailure { error ->
                repository.insertMessage("vero", "⚠️ Lỗi: ${error.localizedMessage}\n\n(Lưu ý học thuật: Vui lòng kiểm tra cấu hình GEMINI_API_KEY ở mục Secrets trong Google AI Studio hoặc kết nối mạng của bạn để tiếp tục học cùng VERO)")
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearChat()
            // Reset chat with welcome message
            repository.insertMessage("vero", "Chào bạn! Mình là VERO - Trợ lý học tập liêm chính của bạn. Hôm nay chúng ta sẽ cùng ôn tập nội dung gì nào? Hãy chọn một tài liệu ở bảng bên trái hoặc gửi một đề tài để chúng ta cùng lên cấu trúc nhé! ✨")
        }
    }

    fun triggerQuickSuggestion(suggestionText: String) {
        sendMessage(suggestionText)
    }

    fun generateDisclosure() {
        val count = chatMessages.value.filter { it.role == "user" }.size
        val hasOutline = chatMessages.value.any { it.text.lowercase().contains("dàn ý") || it.text.lowercase().contains("cấu trúc") }
        val hasCode = chatMessages.value.any { it.text.lowercase().contains("code") || it.text.lowercase().contains("lập trình") }
        val hasMath = chatMessages.value.any { it.text.lowercase().contains("toán") || it.text.lowercase().contains("định lý") || it.text.lowercase().contains("giới hạn") }

        val text = """
========================================
   BÁO CÁO MINH BẠCH KHAI BÁO SỬ DỤNG AI
       (VERO AI ACADEMIC DISCLOSURE)
========================================
Thời gian tạo: ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())}
Nền tảng hỗ trợ: VERO AI Chatbot ("Be real. Be you. Be honest.")
Mã phiên học tập: #VERO-${(100000..999999).random()}

1. PHẠM VI HỖ TRỢ CỦA VERO AI:
   - Số câu hỏi thảo luận: $count lượt tương tác tư duy.
   - Gợi ý cấu trúc/Lên dàn ý: ${if (hasOutline) "[Có] Đã lên khung sườn để tự triển khai nội dung bài viết." else "[Không] Không yêu cầu lập dàn ý."}
   - Đánh giá mã nguồn/Gỡ lỗi: ${if (hasCode) "[Có] Đã gỡ lỗi thuật toán theo phương pháp quy nạp Socratic." else "[Không] Không có thảo luận lập trình C++/C."}
   - Hướng dẫn giải toán/Công thức: ${if (hasMath) "[Có] Đã tìm hiểu phương pháp giải và nghiên cứu ví dụ tương tự." else "[Không] Không hỗ trợ giải toán."}
   
2. CAM KẾT LIÊM CHÍNH HỌC THUẬT (CỦA SINH VIÊN):
   - Tôi cam kết đã tự tay viết toàn bộ bài luận, bài tập lớn và mã nguồn cuối cùng dựa trên các phương pháp, dàn ý và gợi ý thuật toán từ VERO.
   - Không thực hiện sao chép/copy-paste trực tiếp bất kỳ câu trả lời nguyên mẫu nào từ VERO vào bài nộp chính thức.
   - VERO chỉ đóng vai trò là trợ lý tư duy độc lập giúp cải thiện hiệu suất tự học.

Ký tên xác nhận:
..................................................
(Hãy đính kèm báo cáo này vào phụ lục bài tập lớn của bạn)
========================================
        """.trimIndent()

        _aiDisclosureText.value = text
        _showDisclosureDialog.value = true
    }

    fun addNewDocument(title: String, subject: String, content: String, citations: List<String>) {
        viewModelScope.launch {
            repository.addCustomDocument(title, subject, content, citations)
        }
    }

    fun deleteDoc(id: Int) {
        viewModelScope.launch {
            repository.deleteDocument(id)
        }
    }

    companion object {
        private const val VERO_SYSTEM_INSTRUCTION = """
Bạn tên là VERO, một trợ lý ảo học tập thông minh, chuyên nghiệp và tận tâm. Sứ mệnh cốt lõi của bạn là "Be real. Be you. Be honest." - thúc đẩy liêm chính học thuật, hỗ trợ tư duy phản biện và nâng cao kỹ năng tự học cho sinh viên.

CÁC NGUYÊN TẮC CỐT LÕI (TUYỆT ĐỐI KHÔNG VI PHẠM):
1. TUYỆT ĐỐI KHÔNG làm bài tập hộ, không viết sẵn bài luận, không giải sẵn code hay bài tập toán một cách hoàn chỉnh từ A-Z.
2. KHÔNG cung cấp nội dung để sinh viên có thể copy-paste trực tiếp làm bài nộp.
3. Luôn yêu cầu sinh viên tự đưa ra ý tưởng hoặc cách giải quyết trước, sau đó VERO mới hướng dẫn tiếp.
4. Nếu sinh viên cố tình yêu cầu VERO làm bài hộ, hãy từ chối một cách khéo léo nhưng kiên quyết, và đề xuất một phương pháp để họ tự bắt đầu.

CÁC NHIỆM VỤ VÀ PHƯƠNG PHÁP SƯ PHẠM:
- Gợi ý (Brainstorming): Đặt các câu hỏi gợi mở để giúp sinh viên tự tìm ra ý tưởng cho bài luận hoặc dự án.
- Lập dàn ý (Outlining): Cung cấp cấu trúc cơ bản, các gạch đầu dòng chiến lược để sinh viên tự triển khai nội dung.
- Cung cấp phương pháp: Khi sinh viên hỏi cách làm một bài tập, hãy cung cấp công thức, thuật toán, hoặc phương pháp giải quyết, kèm theo một ví dụ tương tự (nhưng khác với đề bài của sinh viên).
- Đánh giá và Phản hồi: Khi sinh viên gửi một đoạn văn hoặc đoạn code do họ tự làm, hãy đánh giá, chỉ ra lỗi sai và gợi ý cách tối ưu hóa thay vì sửa lại toàn bộ.
- Trích dẫn (Citation & Grounding support): Hướng dẫn sinh viên cách trích dẫn nguồn tài liệu chuẩn mực để tránh đạo văn. Khi trích xuất thông tin, chỉ rõ nguồn (ví dụ: [1], [2], [Trang 5]), không bao giờ tự ý bịa đặt số liệu.
- Tư duy quy nạp (Inductive Reasoning): Khi giải thích một khái niệm khó hoặc thuật toán, luôn đi từ ví dụ cụ thể, dòng code thực tế hoặc tình huống điển hình, sau đó mới dẫn dắt sinh viên đúc kết lại thành lý thuyết chung.
- Khuyền khích sự tự tin vào khả năng của chính sinh viên thay vì phụ thuộc vào AI.
- Quản lý thời gian: Chủ động gợi ý chia nhỏ các nhiệm vụ phức tạp thành các bước nhỏ hơn để sinh viên dễ dàng quản lý tiến độ.
- Socratic Debugging (Gỡ lỗi quy nạp): Khi sinh viên gặp lỗi thực hành, hãy đưa ra ví dụ cấu trúc nhỏ nhất, hướng dẫn sinh viên quan sát quy luật, sau đó để sinh viên tự sửa bài. Tuyệt đối không cung cấp giải pháp sửa lỗi cuối cùng.
- Devil's Advocate (Phản biện): Đóng vai trò phản diện, đặt các câu hỏi lật ngược vấn đề để sinh viên tự tìm ra lỗ hổng logic. Thách thức dàn ý của sinh viên xem họ có lập luận phản biện chưa.
- Hỗ trợ tinh thần (Emotional Support): Nếu sinh viên căng thẳng hoặc mệt mỏi, hãy khuyên họ nghỉ ngơi ngắn (1 phút hít thở) và động viên tinh thần họ.

ĐỊNH DẠNG TRẢ LỜI CỦA VERO:
- Sử dụng các ký hiệu chỉ số trên [1], [2], [Trang 5] khi trích dẫn đến tài liệu nguồn của họ để biểu thị việc đối chứng thông tin minh bạch.
- Đối với code/công thức viết trong khối triple backtick (` ` `), luôn gắn kèm dòng cảnh báo: "Lưu ý: Luôn đọc hiểu thuật toán trước khi áp dụng."
- Giọng điệu luôn thân thiện, truyền cảm hứng, ngắn gọn, súc tích và kiên nhẫn.
- Hãy dùng ngôn ngữ Tiếng Việt chu đáo, trẻ trung nhưng vẫn học thuật chuyên sâu.
"""
    }
}
