package com.example.db

import android.content.Context
import com.example.api.Content
import com.example.api.GeminiRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class VeroRepository(private val context: Context) {
    private val database = VeroDatabase.getDatabase(context)
    private val dao = database.veroDao()

    val chatMessages: Flow<List<ChatMessageEntity>> = dao.getAllMessages()
    val studyDocuments: Flow<List<StudyDocumentEntity>> = dao.getAllDocuments()

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val listStringAdapter = moshi.adapter<List<String>>(
        Types.newParameterizedType(List::class.java, String::class.java)
    )

    suspend fun checkAndSeedDocuments() {
        val currentDocs = dao.getAllDocuments().first()
        if (currentDocs.isEmpty()) {
            val defaults = listOf(
                StudyDocumentEntity(
                    title = "Triết học Mác-Lênin: Quy luật Mâu thuẫn",
                    subject = "Triết học",
                    content = """Quy luật thống nhất và đấu tranh của các mặt đối lập (quy luật mâu thuẫn) chỉ ra nguồn gốc, động lực bên trong của mọi sự vận động và phát triển. 

Một mâu thuẫn biện chứng bao gồm hai mặt đối lập. Chúng tồn tại trong mối liên hệ hữu cơ, là tiền đề tồn tại cho nhau [1]. Đồng thời, các mặt đối lập luôn bài trừ, phủ định và đấu tranh chống lại nhau [2].

Phương pháp luận: Cần phải chủ động phát hiện mâu thuẫn, phân tích cụ thể các mặt đối lập để tìm giải pháp giải quyết bằng đấu tranh thực tiễn, tuyệt đối không được điều hòa mâu thuẫn [3] hay trốn tránh khó khăn.""",
                    citationSource = listStringAdapter.toJson(listOf(
                        "Mặt đối lập biện chứng là những khuynh hướng trái ngược nhau nhưng là tiền đề tồn tại cho nhau, thiếu một mặt thì mặt kia không thể tồn tại độc lập.",
                        "Đấu tranh của các mặt đối lập là nguồn gốc và động lực thực sự của sự tự thân vận động, phát triển, thúc đẩy cái mới chiến thắng cái cũ.",
                        "Trong hoạt động nhận thức và thực tiễn, tuyệt đối không được che giấu mâu thuẫn hay thỏa hiệp/điều hòa mâu thuẫn một cách khiên cưỡng."
                    ))
                ),
                StudyDocumentEntity(
                    title = "Giải tích 1: Giới hạn dãy số & Định lý Kẹp",
                    subject = "Toán cao cấp",
                    content = """Định lý kẹp (Squeeze Theorem) là một công cụ mạnh mẽ dùng để tìm giới hạn của một dãy số hoặc hàm số khi trực tiếp tính toán gặp khó khăn do tính tuần hoàn hoặc dao động.

Định lý phát biểu: Nếu ta có ba dãy số thỏa mãn g(n) ≤ f(n) ≤ h(n) với mọi số nguyên n đủ lớn [1], và nếu giới hạn của g(n) và h(n) đều tiến về cùng một giá trị L khi n tiến tới vô cùng, thì giới hạn của f(n) cũng bắt buộc phải bằng L [2].

Một ví dụ toán học điển hình là tính giới hạn của biểu thức f(n) = (cos(n)) / n khi n tiến tới vô cùng [3]. Vì hàm cosin dao động nên việc tìm giới hạn trực tiếp là không thể, nhưng ta dễ dàng kẹp nó.""",
                    citationSource = listStringAdapter.toJson(listOf(
                        "Định lý đòi hỏi mối quan hệ so sánh g(n) <= f(n) <= h(n) phải được giữ vững với mọi n lớn hơn một ngưỡng N_0 nào đó.",
                        "Nếu lim g(n) = L và lim h(n) = L thì theo nguyên lý kẹp, lim f(n) = L. Đây còn gọi là nguyên lý hai cảnh sát.",
                        "Ví dụ kẹp chi tiết: Vì -1 <= cos(n) <= 1, chia cho n dương ta được -1/n <= cos(n)/n <= 1/n. Vì lim(-1/n) = 0 và lim(1/n) = 0, suy ra lim cos(n)/n = 0."
                    ))
                ),
                StudyDocumentEntity(
                    title = "Cấu trúc dữ liệu: QuickSort (C++)",
                    subject = "Tin học",
                    content = """Thuật toán Sắp xếp nhanh (QuickSort) là giải thuật chia để trị cực kỳ phổ biến trong lập trình thực tiễn để tối ưu hóa hiệu suất sắp xếp dữ liệu.

Thuật toán hoạt động theo nguyên lý chia mảng: Chọn một phần tử làm 'chốt' (pivot). Định vị lại mảng sao cho các phần tử nhỏ hơn chốt ở bên trái, phần tử lớn hơn chốt ở bên phải [1]. Bước này gọi là Partition.

Độ phức tạp thời gian trung bình của thuật toán cực kỳ tối ưu là O(N log N) [2]. Tuy nhiên, trong trường hợp xấu nhất như mảng đã được sắp xếp sẵn và chọn chốt không tốt, độ phức tạp có thể giảm xuống O(N²) [3].""",
                    citationSource = listStringAdapter.toJson(listOf(
                        "Hàm Partition nhận mảng, chỉ số đầu và cuối, dịch chuyển các phần tử nhỏ hơn pivot sang trái, lớn hơn pivot sang phải rồi trả về chỉ số của chốt.",
                        "Độ phức tạp O(N log N) đạt được nhờ việc chia đôi mảng đều đặn ở mỗi bước đệ quy, giảm thiểu số lượng so sánh tối đa.",
                        "Trường hợp xấu nhất O(N^2) xảy ra khi mỗi bước chia pivot chỉ tách được 1 phần tử so với N-1 phần tử còn lại, thường xử lý bằng cách chọn pivot ngẫu nhiên."
                    ))
                )
            )
            for (doc in defaults) {
                dao.insertDocument(doc)
            }
        }
    }

    suspend fun insertMessage(role: String, text: String): ChatMessageEntity {
        val entity = ChatMessageEntity(role = role, text = text)
        dao.insertMessage(entity)
        return entity
    }

    suspend fun clearChat() {
        dao.clearChat()
    }

    suspend fun addCustomDocument(title: String, subject: String, content: String, citations: List<String>) {
        val entity = StudyDocumentEntity(
            title = title,
            subject = subject,
            content = content,
            isCustom = true,
            citationSource = listStringAdapter.toJson(citations)
        )
        dao.insertDocument(entity)
    }

    suspend fun deleteDocument(id: Int) {
        dao.deleteDocument(id)
    }

    // Call external Gemini API via repository
    suspend fun getVeroAiResponse(prompt: String, history: List<Content>, systemInstruction: String): Result<String> {
        return GeminiRepository.generateResponse(prompt, history, systemInstruction)
    }
}
