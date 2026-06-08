# Cấu trúc Firebase (Firebase Schema)

Tài liệu này mô tả các collection trong hệ thống, bao gồm các phần đã có và các phần bổ sung mới.

## 1. Các Collection hiện tại (Team Legacy)

### Collection: `event`
Quản lý thông tin sự kiện và các hạng vé (`ticketTypes`).

### Collection: `bookings`
(Quản lý bởi team khác) - Lưu trữ thông tin đặt chỗ và giữ chỗ.

## 2. Các Collection bổ sung (My Tickets Feature)

### Collection: `orders`
Lưu trữ thông tin giao dịch tài chính độc lập, hỗ trợ hiển thị danh sách vé theo nhóm đơn hàng.
*Xem chi tiết tại [docs/my_tickets.md](file:///C:/Users/DELL/Documents/GitHub/FanZone/docs/my_tickets.md)*

### Sub-collection: `users/{uid}/my_tickets`
Lưu trữ chi tiết từng vé của người dùng, kế thừa từ `TicketWalletItem` với các trạng thái:
- `UPCOMING`: Vé sắp diễn ra.
- `COMPLETED`: Vé đã sử dụng.
- `CANCELLED`: Vé đã hủy.
- `RESELLING`: Vé đang được đăng bán lại.

## 3. Quy tắc chung
- Tất cả ID sự kiện (`eventId`) sử dụng kiểu dữ liệu `string`.
- Mã QR check-in được lưu trong trường `qrCode`.

## Community posts

Trang Community hiện chỉ dùng Firestore collection:

```text
posts
```

Collection cũ `communityPosts` không còn được app đọc/ghi. Schema chuẩn chi tiết nằm ở:

```text
docs/community_post_schema.md
```
