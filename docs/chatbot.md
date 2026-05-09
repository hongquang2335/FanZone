# Chatbot Documentation

## 10/05/2026 - Cập nhật Giao diện Chatbot & Fix Lỗi

### Tóm tắt công việc
*   **Triển khai UI Chatbot**: Xây dựng màn hình chatbot hoàn chỉnh với bong bóng chat (User bên phải, Bot bên trái), tích hợp gợi ý trả lời nhanh (interactive chips).
*   **Hiệu ứng AI Typing**: Cài đặt hiệu ứng hiện từng từ (typing animation) cho Bot, điều chỉnh tốc độ `30ms/word` để mang lại cảm giác tự nhiên.
*   **Định dạng Markdown**: Xây dựng bộ parse Markdown tùy chỉnh hỗ trợ in đậm (`**bold**`) với màu xanh thương hiệu và danh sách liệt kê (`• bullet points`).
*   **Fix lỗi Animation khi Scroll**: Chuyển từ `LazyColumn` sang `Column + verticalScroll` để giữ trạng thái tin nhắn, ngăn chặn việc chạy lại hiệu ứng typing khi người dùng cuộn trang.

### Chi tiết thay đổi InputBar
Thanh nhập liệu được tinh chỉnh lại toàn diện để đạt chuẩn UI/UX:
*   **Loại bỏ Shadow**: Xóa bỏ đổ bóng thô ở đáy màn hình, thay bằng `HorizontalDivider` siêu mỏng (1.dp) phía trên thanh chat để ngăn cách tinh tế.
*   **Căn chỉnh Layout**: 
    *   Sử dụng `navigationBarsPadding()` bên trong `Surface` màu trắng để thanh chat bao phủ toàn bộ đáy màn hình (Edge-to-Edge).
    *   Tinh chỉnh kích thước nút Gửi (`44.dp`) và nút Thêm (`28.dp`) để tạo sự cân đối.
*   **Tăng tính ổn định**: Cấu hình `singleLine = false` và `maxLines = 4` kết hợp với `KeyboardOptions` để bộ gõ Android hoạt động ổn định nhất, không bị xung đột tiêu điểm.
