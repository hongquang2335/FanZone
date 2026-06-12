# FanZone - Nền tảng cộng đồng và đặt vé sự kiện tối ưu

FanZone là một ứng dụng di động Android hiện đại được xây dựng hoàn toàn bằng Kotlin, Jetpack Compose, Material 3 và Firebase. Ứng dụng mô phỏng trải nghiệm tham gia cộng đồng người hâm mộ và đặt vé sự kiện chuyên nghiệp với đầy đủ các tính năng từ tìm kiếm, đặt ghế, thanh toán, đến tương tác mạng xã hội thời gian thực và trợ lý ảo thông minh.

---

## Các tính năng nổi bật

### 1. Khám phá và đặt vé sự kiện
* **Giao diện trang chủ hiện đại**: Hiển thị danh sách sự kiện hấp dẫn theo các danh mục như concert, sportsSoccer, movie, v.v., kèm theo thanh tìm kiếm thông minh và bộ lọc danh mục trực quan.
* **Chi tiết sự kiện trực quan**: Cung cấp đầy đủ thông tin về thời gian, địa điểm, nghệ sĩ biểu diễn, ban tổ chức và sơ đồ vé.
* **Sơ đồ chọn ghế tương tác**: Cho phép người dùng chọn vé theo từng hạng ghế (VIP, standard, v.v.) trực tiếp trên một lưới ghế trực quan.
* **Quy trình thanh toán và ví vé an toàn**:
  * Tích hợp cổng thanh toán giả lập với nhiều phương thức linh hoạt.
  * Màn hình thanh toán thành công sinh mã vé chi tiết.
  * **Ví vé** lưu trữ toàn bộ vé đã mua của người dùng dưới dạng thẻ vé tiện lợi.

### 2. Mạng xã hội cộng đồng
* **Bảng tin thời gian thực**: Kết nối trực tiếp với Firestore để cập nhật bài viết tức thời.
* **Đăng tải đa phương tiện**:
  * Đăng bài viết kèm hình ảnh động (sử dụng thư viện **Coil** tải ảnh bất đồng bộ).
  * Phát video trực tiếp trên bảng tin với trình phát video **Google Media3 ExoPlayer** tích hợp bộ điều khiển.
  * Chia sẻ các đoạn ghi âm thoại sinh động.
* **Tương tác xã hội toàn diện**:
  * Thích, bình luận và chia sẻ bài viết cùng lời nhắn cá nhân.
  * Hệ thống theo dõi giữa các thành viên để xây dựng vòng kết nối.
  * **Cộng đồng sự kiện**: Mỗi sự kiện lớn có một cộng đồng nhỏ riêng biệt để người hâm mộ của sự kiện đó dễ dàng trao đổi.

### 3. Hệ thống thông báo thông minh
* **Thông báo tức thời**: Tự động gửi thông báo thời gian thực khi có người thích, bình luận, chia sẻ bài viết hoặc theo dõi bạn.
* **Tối ưu hóa ghi dữ liệu hàng loạt**: Sử dụng coroutines kết hợp các lô ghi Firestore (`createNotifications`) để gửi thông báo đến hàng trăm người theo dõi cùng lúc mà không gây đơ hay giật giao diện (chạy nền hoàn toàn dưới luồng `Dispatchers.IO + NonCancellable`).
* **Phân biệt trạng thái trực quan**: Thông báo chưa đọc hiển thị nổi bật với bóng đổ 8dp và nền trắng, trong khi thông báo đã đọc được làm chìm xuống dạng phẳng không bóng đổ trên nền xám nhạt (`VibeSurfaceMuted`).
* **Điều hướng thông minh và an toàn**: Nhấn vào thông báo sẽ tự động mở bài viết gốc hoặc trang cá nhân của người gửi. Ứng dụng tự động kiểm tra và thông báo bằng Toast nếu bài viết liên quan đã bị xóa.

### 4. Trợ lý hỗ trợ ảo Gemini
* Tích hợp trí tuệ nhân tạo Gemini để giải đáp thắc mắc của người dùng về sự kiện và đặt vé.
* Trò chuyện đa lượt với hiệu ứng đang suy nghĩ và tự động cuộn xuống tin nhắn mới nhất.
* **Nhận diện ngữ cảnh**: Trợ lý ảo có khả năng phân tích câu hỏi của người dùng, tự động tìm kiếm và hiển thị danh sách sự kiện phù hợp trực tiếp dưới dạng thẻ sự kiện tương tác ngay trong khung chat.

### 5. Quản lý tài khoản và xác thực
* **Đăng nhập đa phương thức**: Đăng nhập nhanh chóng và bảo mật thông qua tài khoản email/mật khẩu hoặc liên kết tài khoản Google.
* **Khôi phục mật khẩu**: Hỗ trợ gửi mã xác nhận và liên kết đặt lại mật khẩu trực tiếp qua email.
* **Trang cá nhân tùy biến**: Hiển thị số lượng người theo dõi, đang theo dõi, tổng số lượt thích, danh sách bài viết cá nhân và cho phép chỉnh sửa thông tin tài khoản (tên hiển thị, ảnh đại diện).
* **Kiểm soát truy cập khách**: Hiển thị hộp thoại yêu cầu đăng nhập (`LoginRequiredDialog`) thân thiện khi người dùng chưa đăng nhập cố gắng tương tác với các tính năng cộng đồng hoặc thông báo.

---

## Công nghệ sử dụng

* **Ngôn ngữ**: Kotlin (100%)
* **Giao diện**: Jetpack Compose (Material 3) với hiệu ứng chuyển động mượt mà và phối màu cao cấp (Evergreen, VibeGreen).
* **Kiến trúc**: Đơn mô-đun sắp xếp theo tính năng (app, core, domain, data, feature).
* **Cơ sở dữ liệu và dịch vụ đám mây**: Google Firebase (Firestore, Authentication, Storage).
* **Xử lý bất đồng bộ**: Kotlin coroutines và Flow (StateFlow, collectAsStateWithLifecycle).
* **Phát phương tiện**: Google Media3 ExoPlayer và Coil Image Loader.
* **Trí tuệ nhân tạo**: Firebase GenAI (Gemini API).

---

## Cấu trúc thư mục dự án

* `app`: Chứa cấu hình ứng dụng chính (`FanZoneApp.kt`) và quản lý các phụ thuộc.
* `core/designsystem`: Chứa hệ thống giao diện, màu sắc và các thành phần giao diện dùng chung (`AppShell.kt`, `ChatComponents.kt`).
* `core/navigation`: Định nghĩa các đích đến (`AppDestination.kt`) và sơ đồ điều hướng (`FanZoneNavHost.kt`).
* `domain`: Định nghĩa các thực thể nghiệp vụ (`CommunityPost.kt`, `UserProfile.kt`) và các giao diện kho lưu trữ.
* `data`: Cài đặt chi tiết các kho lưu trữ kết nối tới Firebase Firestore (`CommunityRepositoryImpl.kt`).
* `feature`: Chứa mã nguồn cho từng tính năng riêng biệt:
  * `booking`: Đặt vé và sơ đồ ghế.
  * `community`: Bảng tin, soạn thảo bài viết và xem phương tiện.
  * `event`: Chi tiết sự kiện.
  * `profile`: Trang cá nhân, thông báo và cài đặt.
  * `support`: Khung chat hỗ trợ với trợ lý ảo Gemini.
