# FanZone - Nền tảng Cộng đồng & Đặt vé Sự kiện tối ưu

FanZone là một ứng dụng di động Android hiện đại được xây dựng hoàn toàn bằng Kotlin, Jetpack Compose, Material 3 và Firebase. Ứng dụng mô phỏng trải nghiệm tham gia cộng đồng người hâm mộ và đặt vé sự kiện chuyên nghiệp với đầy đủ các tính năng từ tìm kiếm, đặt ghế, thanh toán, đến tương tác mạng xã hội thời gian thực và trợ lý ảo thông minh.

---

## Các Tính năng Nổi bật (Outstanding Features)

### 1. Khám phá & Đặt vé Sự kiện (Event Discovery & Booking)
* **Giao diện Trang chủ hiện đại**: Hiển thị danh sách sự kiện hấp dẫn theo các danh mục như Concert, SportsSoccer, Movie, v.v., kèm theo thanh tìm kiếm thông minh và bộ lọc danh mục trực quan.
* **Chi tiết Sự kiện trực quan**: Cung cấp đầy đủ thông tin về thời gian, địa điểm, nghệ sĩ biểu diễn, ban tổ chức và sơ đồ vé.
* **Sơ đồ Chọn ghế tương tác**: Cho phép người dùng chọn vé theo từng hạng ghế (VIP, Standard, v.v.) trực tiếp trên một grid ghế trực quan.
* **Quy trình Thanh toán & Ví vé an toàn**:
  * Tích hợp cổng thanh toán giả lập với nhiều phương thức linh hoạt.
  * Màn hình Thanh toán thành công (Purchase Success) sinh mã vé chi tiết.
  * **Ví vé (Ticket Wallet)** lưu trữ toàn bộ vé đã mua của người dùng dưới dạng thẻ vé tiện lợi.

### 2. Mạng xã hội Cộng đồng (Event Community Feed)
* **Bảng tin thời gian thực**: Kết nối trực tiếp với Firestore để cập nhật bài viết tức thời.
* **Đăng tải đa phương tiện (Rich Media Posts)**:
  * Đăng bài viết kèm hình ảnh động (sử dụng thư viện **Coil** tải ảnh bất đồng bộ).
  * Phát video trực tiếp trên feed với trình phát video **Google Media3 ExoPlayer** tích hợp điều khiển.
  * Chia sẻ các đoạn ghi âm thoại sinh động.
* **Tương tác xã hội toàn diện**:
  * Thích (Like), Bình luận (Comment) và Chia sẻ bài viết (Share) cùng lời nhắn cá nhân.
  * Hệ thống Theo dõi (Follow/Unfollow) giữa các thành viên để xây dựng vòng kết nối.
  * **Event Hubs**: Mỗi sự kiện lớn có một cộng đồng nhỏ riêng biệt để người hâm mộ của sự kiện đó dễ dàng trao đổi.

### 3. Hệ thống Thông báo Thông minh (Real-time Notification System)
* **Thông báo tức thời**: Tự động gửi thông báo thời gian thực khi có người thích, bình luận, chia sẻ bài viết hoặc theo dõi bạn.
* **Tối ưu hóa ghi dữ liệu hàng loạt**: Sử dụng Coroutines kết hợp Firestore Write Batches (`createNotifications`) để gửi thông báo đến hàng trăm người theo dõi cùng lúc mà không gây đơ/lag giao diện (chạy nền hoàn toàn dưới luồng `Dispatchers.IO + NonCancellable`).
* **Phân biệt trạng thái trực quan**: Thông báo chưa đọc hiển thị nổi bật với bóng đổ 8.dp và nền trắng, trong khi thông báo đã đọc được làm chìm xuống dạng phẳng không bóng đổ trên nền xám nhạt (`VibeSurfaceMuted`).
* **Điều hướng thông minh & An toàn**: Nhấn vào thông báo sẽ tự động mở bài viết gốc hoặc trang cá nhân của người gửi. Ứng dụng tự động kiểm tra và thông báo bằng Toast nếu bài viết liên quan đã bị xóa.

### 4. Trợ lý Hỗ trợ ảo Gemini (Gemini AI Support Chatbot)
* Tích hợp trí tuệ nhân tạo Gemini để giải đáp thắc mắc của người dùng về sự kiện và đặt vé.
* Trò chuyện đa lượt (multi-turn chat) với hiệu ứng đang suy nghĩ (thinking state) và tự động cuộn xuống tin nhắn mới nhất.
* **Nhận diện ngữ cảnh**: Trợ lý ảo có khả năng phân tích câu hỏi của người dùng, tự động tìm kiếm và hiển thị danh sách sự kiện phù hợp trực tiếp dưới dạng thẻ sự kiện tương tác ngay trong khu chat.

### 5. Quản lý Tài khoản & Xác thực (Authentication & Profile)
* **Đăng nhập đa phương thức**: Đăng nhập nhanh chóng và bảo mật thông qua tài khoản Email/Mật khẩu hoặc liên kết tài khoản Google.
* **Khôi phục mật khẩu**: Hỗ trợ gửi mã xác nhận và liên kết đặt lại mật khẩu trực tiếp qua email.
* **Trang cá nhân tùy biến**: Hiển thị số lượng người theo dõi, đang theo dõi, tổng số lượt thích, danh sách bài viết cá nhân và cho phép chỉnh sửa thông tin tài khoản (tên hiển thị, ảnh đại diện).
* **Kiểm soát truy cập khách (Guest Access)**: Hiển thị hộp thoại yêu cầu đăng nhập (`LoginRequiredDialog`) thân thiện khi người dùng chưa đăng nhập cố gắng tương tác với các tính năng cộng đồng hoặc thông báo.

---

## Công nghệ Sử dụng (Technology Stack)

* **Ngôn ngữ**: Kotlin (100%)
* **Giao diện**: Jetpack Compose (Material 3) với hiệu ứng chuyển động mượt mà và phối màu cao cấp (Evergreen, VibeGreen).
* **Kiến trúc**: Feature-First Single-Module (app, core, domain, data, feature).
* **Cơ sở dữ liệu & Back-end**: Google Firebase (Firestore, Authentication, Storage).
* **Xử lý bất đồng bộ**: Kotlin Coroutines & Flow (StateFlow, collectAsStateWithLifecycle).
* **Phát Media**: Google Media3 ExoPlayer & Coil Image Loader.
* **Trí tuệ nhân tạo**: Firebase GenAI (Gemini API).

---

## Cấu trúc Thư mục Dự án

* `app`: Chứa cấu hình ứng dụng chính (`FanZoneApp.kt`) và trình quản lý dependency.
* `core/designsystem`: Chứa hệ thống chủ đề (Theme), màu sắc và các thành phần giao diện dùng chung (`AppShell.kt`, `ChatComponents.kt`).
* `core/navigation`: Định nghĩa các đích đến (`AppDestination.kt`) và sơ đồ điều hướng (`FanZoneNavHost.kt`).
* `domain`: Định nghĩa các thực thể nghiệp vụ (`CommunityPost.kt`, `UserProfile.kt`) và các giao diện Repository.
* `data`: Cài đặt chi tiết các Repository kết nối tới Firebase Firestore (`CommunityRepositoryImpl.kt`).
* `feature`: Chứa mã nguồn cho từng màn hình/tính năng riêng biệt:
  * `booking`: Đặt vé & sơ đồ ghế.
  * `community`: Bảng tin, soạn thảo bài viết, xem media.
  * `event`: Chi tiết sự kiện.
  * `profile`: Trang cá nhân, thông báo, cài đặt.
  * `support`: Khung chat hỗ trợ với Gemini AI.

