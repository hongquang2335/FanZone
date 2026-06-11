# Tài liệu Tính năng Cộng đồng (Community Feature Documentation)

Tài liệu này mô tả chi tiết kiến trúc, các thành phần UI, luồng xử lý dữ liệu và hệ thống thông báo của tính năng Cộng đồng (Community) trong ứng dụng FanZone.

---

## 1. Tổng quan (Overview)

Tính năng Cộng đồng cho phép người dùng:
- Xem dòng trạng thái các bài đăng nổi bật hoặc bài đăng thuộc về một sự kiện cụ thể.
- Chia sẻ cảm nghĩ, hình ảnh, bài hát.
- Thích (Like), bình luận (Comment), theo dõi (Follow) người dùng khác.
- Nhận thông báo tức thời (In-app & Local Device Notification) khi có tương tác mới.
- Điều hướng nhanh từ thông báo thẳng đến bài viết mục tiêu.

---

## 2. Kiến trúc & Các thành phần (Architecture & Components)

Tính năng được xây dựng theo mô hình MVVM (Model-View-ViewModel) kết hợp với Jetpack Compose và Firebase Firestore:

```mermaid
graph TD
    UI[CommunityScreen / EventCommunityScreen] -->|Theo dõi State| VM[CommunityViewModel]
    VM -->|Yêu cầu dữ liệu| Repo[CommunityRepository]
    Repo -->|Truy vấn thực tế| DataSource[CommunityFirestoreDataSource]
    DataSource -->|Đồng bộ realtime| Firestore[(Firebase Firestore)]
```

### A. Lớp Giao diện (UI Layer)
- **`CommunityScreen`**: Hiển thị bảng tin chung của cộng đồng, hỗ trợ giao diện co giãn (Responsive) trên màn hình lớn (Expanded Layout) và màn hình dọc điện thoại (Compact Layout).
- **`EventCommunityScreen`**: Hiển thị bảng tin riêng của từng sự kiện cụ thể (ví dụ: concert, trận đấu bóng đá) với Banner sự kiện ở trên đầu.
- **`CommunityCard`**: Component hiển thị thông tin bài đăng, ảnh đính kèm, lượt thích, số bình luận, nút chia sẻ và danh sách bình luận bên dưới.

### B. Lớp Xử lý Logic (ViewModel Layer)
- **`CommunityViewModel`**: 
  - Quản lý trạng thái giao diện (`CommunityUiState`), bao gồm danh sách bài viết (`posts`), bình luận theo từng bài viết (`commentsByPostId`), số thông báo chưa đọc (`unreadNotificationCount`).
  - Lắng nghe realtime các bài viết trên Firestore qua Listener.
  - Xử lý các thao tác Thích, Thêm/Xóa bài viết, Viết bình luận, Follow người dùng khác.

### C. Lớp Dữ liệu (Data Layer)
- **`CommunityRepository`** & **`CommunityFirestoreDataSource`**: Thực hiện các truy vấn đọc/ghi tài liệu bài đăng, lượt thích, bình luận và theo dõi trên Firestore Database.

> [!NOTE]
> Chi tiết thiết kế và tất cả các Composable con của hệ thống bài đăng đã được tách và mô tả đầy đủ tại [Tài liệu Chi tiết các Thành phần Giao diện Cộng đồng](file:///c:/Users/quang/AndroidStudioProjects/MyApplication/docs/COMMUNITY_COMPONENTS.md).

---

## 3. Quy trình điều hướng & Cuộn tự động (Navigation & Auto-Scroll Flow)

Khi người dùng nhấn vào một thông báo liên quan đến một bài viết cụ thể:

```mermaid
sequenceDiagram
    participant U as Người dùng
    participant Nav as FanZoneNavHost
    participant VM as CommunityViewModel
    participant UI as CommunityScreen
    
    U->>Nav: Nhấp vào thông báo bài viết
    Nav->>VM: Gọi setTargetCommentsPostId(postId)
    Note over VM: Cập nhật targetCommentsPostId trong UiState
    Nav->>UI: Điều hướng sang trang Community/EventCommunity
    Note over UI: LaunchedEffect phát hiện targetCommentsPostId khác null
    UI->>UI: Tìm index bài viết trong danh sách
    UI->>UI: Thực hiện cuộn mượt (animateScrollToItem) tới bài viết đó
    UI->>VM: Gọi onCommentsDismissed() (đặt targetCommentsPostId về null)
```

- **Lợi ích**: Giúp người dùng tập trung trực tiếp vào nội dung bài đăng được tương tác mà không bị che khuất bởi hộp thoại bình luận tự động mở như trước.

---

## 4. Hệ thống Thông báo (Notification System)

Hệ thống thông báo của ứng dụng FanZone hoạt động thông qua hai hình thức song song:

### A. Thông báo trong ứng dụng (In-App Notifications)
- **Chấm đỏ báo hiệu (Red Dot Badge)**: Trên thanh tiêu đề của trang Cộng đồng, Cộng đồng Sự kiện và Cá nhân, nút hình chuông hiển thị một dấu chấm đỏ kích thước `10.dp` góc trên bên phải khi có thông báo chưa đọc (`unreadNotificationCount > 0`). Số lượng số cụ thể đã được lược bỏ để giao diện thanh lịch và tối giản hơn.
- **Trang Danh sách**: Hiển thị các hành động Thích, Bình luận, Theo dõi kèm ảnh đại diện và tên người gửi tương tác.

### B. Thông báo trên thiết bị (Local Device Notifications)
Khi có người dùng tương tác, ứng dụng sẽ đẩy một biểu ngữ thông báo (System Notification Banner) lên điện thoại của bạn kể cả khi ứng dụng đang chạy ngầm.

- **`NotificationHelper`**: 
  - Tạo một kênh thông báo (Notification Channel) với độ ưu tiên cao (`IMPORTANCE_HIGH`) tên là `FanZone Notifications`.
  - Sử dụng biểu tượng cục bộ của ứng dụng (`R.mipmap.ic_launcher`) làm biểu tượng nhỏ (Small Icon) để đảm bảo hiển thị đồng bộ và tránh lỗi hiển thị trên một số hệ điều hành tùy biến (như MIUI, OneUI).
  - Quản lý danh sách ID thông báo đã hiển thị (`displayedNotificationIds`) để **chỉ báo chuông/rung cho những thông báo mới phát sinh** khi bạn đang online, tránh làm phiền thiết bị bằng một loạt thông báo cũ khi vừa đăng nhập.
  - Hàm `reset()` sẽ được gọi để giải phóng bộ nhớ lưu trữ đệm này khi người dùng đăng xuất.

---

## 5. Phân quyền thiết bị (Device Permissions)

Để gửi thông báo trên điện thoại, ứng dụng xử lý linh hoạt theo phiên bản hệ điều hành Android:

### Android 12 trở xuống (Bao gồm Android 11)
- Thông báo được **tự động cấp quyền mặc định** ngay khi người dùng cài đặt ứng dụng.
- Ứng dụng sẽ trực tiếp gửi thông báo dạng banner mà không cần yêu cầu người dùng thao tác cấp quyền.

### Android 13 trở lên (API 33+)
- Ứng dụng khai báo quyền `<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />` trong `AndroidManifest.xml`.
- Tại thời điểm khởi chạy ứng dụng (`MainActivity.onCreate`), hệ thống sẽ kiểm tra quyền này. Nếu chưa được cấp, ứng dụng sẽ kích hoạt hộp thoại hệ thống yêu cầu người dùng xác nhận **Cho phép** (Allow).

---

## 6. Kiểm tra & Khắc phục sự cố (Troubleshooting)

Nếu bạn không nhận được thông báo trên điện thoại khi thử nghiệm, hãy kiểm tra các điểm sau:

1. **Kiểm tra Cài đặt thiết bị**:
   - Truy cập **Cài đặt hệ điều hành Android** -> **Ứng dụng** -> **FanZone** -> **Thông báo**. Đảm bảo tùy chọn này đang ở trạng thái **Bật (Allowed)**.
2. **Quy tắc tự tương tác**:
   - Hệ thống sẽ không bắn thông báo nếu bạn tự Thích hoặc tự Bình luận trên bài đăng của chính mình. Hãy dùng một tài khoản khác để tương tác và thử nghiệm.
3. **Firestore Security Rules**:
   - Đảm bảo cơ sở dữ liệu Firebase của bạn đã cấp quyền ghi (`allow create`) và đọc (`allow read`) cho tài liệu trong bộ sưu tập `notifications`.

---

## 7. Công nghệ Sử dụng (Tech Stack)

Để phát triển tính năng Cộng đồng một cách bền vững và hiệu năng cao, ứng dụng FanZone sử dụng các công nghệ cốt lõi sau:

### A. Giao diện (Frontend & UI)
- **Jetpack Compose (với Kotlin Compose Compiler)**: Xây dựng toàn bộ giao diện theo cơ chế khai báo (Declarative UI).
- **Material Design 3 (M3)**: Sử dụng các component chuẩn M3 như `Card`, `IconButton`, `Text`, `CircularProgressIndicator`, và `ModalBottomSheet` kết hợp hệ thống màu tùy chỉnh (`Evergreen`, `SoftText`, `Background`).
- **Coil Compose (`io.coil-kt:coil-compose`)**: Đọc và hiển thị ảnh bất đồng bộ từ URL (Firebase Storage) với cơ chế cache tự động và tối ưu hóa bộ nhớ cho danh sách cuộn nhanh.
- **Jetpack Media3 ExoPlayer & UI**:
  - `media3-exoplayer`: Bộ giải mã và phát video hiệu năng cao, hỗ trợ tải trước (buffering) và cấu hình phát lặp lại (looping).
  - `media3-ui` (`PlayerView`): Sử dụng `AndroidView` để nhúng khung phát video native vào Compose.
  - Quản lý vòng đời chặt chẽ: Giải phóng ExoPlayer thủ công ngay khi Dialog hoặc Screen bị hủy (`onDispose`), ngăn ngừa rò rỉ bộ nhớ (Memory Leak).

### B. Kiến trúc & Quản lý Trạng thái (Architecture & State Management)
- **Mô hình kiến trúc MVVM**:
  - **Model**: Đại diện bởi lớp thực thể dữ liệu sạch (`CommunityPost`, `CommunityComment`, `SharedCommunityPost`).
  - **View**: Giao diện Compose thuần túy, không chứa business logic (`CommunityScreen`, `EventCommunityScreen`).
  - **ViewModel**: `CommunityViewModel` đóng vai trò là single source of truth cho luồng dữ liệu của UI.
- **Luồng dữ liệu một chiều (UDF - Unidirectional Data Flow)**: Giao diện chỉ nhận dữ liệu từ `StateFlow<CommunityUiState>` và truyền các sự kiện hành động (Events) ngược lại ViewModel qua lambda callbacks.
- **Hoisting Dialogs**: Trạng thái của các dialog chỉnh sửa (`EditPostDialog`) được đưa lên cấp điều hướng cao nhất ở `FanZoneNavHost`, đảm bảo hiển thị đồng nhất khi người dùng mở từ bất kỳ Route nào (Community feed, Event detail, Profile cá nhân, hoặc trang Profile người lạ).

### C. Quản lý Dữ liệu & Cơ sở dữ liệu (Data & Storage)
- **Firebase Cloud Firestore**: Cơ sở dữ liệu NoSQL phân tán, thời gian thực.
  - **Realtime Sync**: Sử dụng `addSnapshotListener` để nhận cập nhật tức thì khi có bài đăng mới, lượt thích, bình luận, hoặc khi bài đăng bị chỉnh sửa/xóa mà không cần reload trang.
  - **Đồng bộ bài viết gốc**: Khi tải feed, hàm `hydrateOriginalPosts` truy vấn bổ sung thông tin từ Firestore để điền dữ liệu động vào `SharedCommunityPost`. Nếu bài viết gốc bị xóa, trường `isDeleted` được gán thành `true` để UI hiển thị thông báo lỗi phù hợp.
  - **Deserialize linh hoạt**: Helper `parseCreatedAt()` xử lý đa dạng các kiểu dữ liệu thời gian của Firestore (`Timestamp`, `Date`, `Long`, hoặc cấu trúc `Map` chứa seconds/nanoseconds), đảm bảo thời gian hiển thị chính xác trên mọi dòng thiết bị.
- **Firebase Authentication**: Xác thực người dùng hiện tại và quản lý trạng thái đăng nhập (`AuthStateListener`). Giải phóng tài nguyên và xóa cache thông báo của `NotificationHelper` khi người dùng đăng xuất.

### D. Hệ thống Nền tảng & Phân quyền (Platform Services & Permissions)
- **Android NotificationManager & NotificationChannel**: Cấu hình kênh thông báo với độ ưu tiên cao (`IMPORTANCE_HIGH`) để bắn thông báo dạng banner (heads-up banner).
- **Runtime Permissions Check (API 33+)**: Yêu cầu quyền `POST_NOTIFICATIONS` động trên Android 13 trở lên, tự động cho phép trên các phiên bản Android cũ hơn để đảm bảo tính tương thích ngược rộng rãi.
