package com.example.myapplication

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.feature.support.ChatViewModel
import com.example.myapplication.domain.model.Participant
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatViewModelTest {

    @Test
    fun testSendMessageAndFunctionCalling() = runBlocking {
        // 1. Khởi tạo ViewModel
        val viewModel = ChatViewModel()

        // 2. Gửi một tin nhắn yêu cầu gọi Tool (ví dụ: tìm sự kiện)
        val userQuery = "Tháng 6 có sự kiện gì ở Hồ Chí Minh không?"
        println("--- Đang gửi câu hỏi: $userQuery ---")
        viewModel.sendMessage(userQuery)

        // 3. Đợi phản hồi (vì là async nên ta cần đợi một chút hoặc check flow)
        // Trong môi trường test, ta có thể đợi đến khi tin nhắn cuối cùng là của Bot và không còn "thinking"
        var attempts = 0
        var lastMessage = ""

        while (attempts < 10) { // Đợi tối đa 10-15 giây cho AI trả lời
            val messages = viewModel.messages.value
            val botResponse = messages.lastOrNull { it.sender == Participant.Bot && !it.isThinking }

            if (botResponse != null && botResponse.content.isNotEmpty()) {
                lastMessage = botResponse.content
                break
            }

            println("... Đang đợi AI phản hồi ...")
            kotlinx.coroutines.delay(2000)
            attempts++
        }

        // 4. In kết quả ra Log để kiểm tra
        println("--- Kết quả từ AI ---")
        println(lastMessage)

        // 5. Kiểm tra kết quả (Assertion)
        assertTrue("AI phải trả lời được nội dung", lastMessage.isNotEmpty())
    }
}