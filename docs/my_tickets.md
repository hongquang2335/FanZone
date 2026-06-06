# Tính năng Vé của tôi (My Tickets)

Tài liệu này mô tả chi tiết về tính năng "Vé của tôi", bao gồm cấu trúc dữ liệu và logic xử lý, đảm bảo tính đồng nhất với hệ thống hiện tại.

## 1. Luồng nghiệp vụ (Workflow)

1.  **Mua vé:** Khi thanh toán thành công, hệ thống tạo một bản ghi trong collection `orders` và giải phóng các vé lẻ vào sub-collection `my_tickets` của người dùng.
2.  **Hiển thị:** Trang "Vé của tôi" hiển thị danh sách các `orders`. Khi nhấn vào đơn hàng, chi tiết các vé lẻ (`my_tickets`) sẽ được hiển thị kèm mã QR check-in.
3.  **Bán lại (Resale):** Người dùng có thể chọn một vé lẻ để đăng bán. Trạng thái vé chuyển sang `RESELLING`.

## 2. Cấu trúc dữ liệu (Data Schema)

### Collection: `orders`
Lưu trữ thông tin giao dịch tài chính (Không can thiệp vào bảng `bookings` của team khác).

| Field | Type | Description |
| :--- | :--- | :--- |
| `bookingId` | string | ID đơn hàng (Match snippet ban đầu) |
| `userId` | string | ID người mua |
| `totalPrice` | int64 | Tổng tiền |
| `qrCodeData` | string | QR đối soát thanh toán |
| `paymentStatus` | string | Trạng thái (VD: `success`) |

### Sub-collection: `users/{uid}/my_tickets/{ticketId}`
Lưu trữ từng vé lẻ để check-in hoặc bán lại.

| Field | Type | Description |
| :--- | :--- | :--- |
| `ticketId` | string | ID vé |
| `qrCodeData` | string | **QR Check-in** (Sẽ đổi mã khi bán lại) |
| `status` | string | `UPCOMING`, `COMPLETED`, `CANCELLED`, `RESELLING` |

## 3. Quy tắc đặt tên & Trạng thái (Naming & Status Conventions)

Để đảm bảo đồng bộ với code của các thành viên trước, chúng ta tuân thủ:
- **Trạng thái vé (`TicketStatus`):** Sử dụng các giá trị nguyên bản `UPCOMING`, `COMPLETED`, `CANCELLED` và bổ sung `RESELLING`.
- **Mã QR:** Sử dụng tên trường `qrCodeData` cho các collection dữ liệu mới để khớp với snippet ban đầu của bạn.

## 4. Giao diện (UI Design)

- **Tab:** Phân chia "Vé đã mua" và "Vé bán lại".
- **Gom nhóm:** Hiển thị theo Đơn hàng (Order-based) để tránh làm rối danh sách khi mua nhiều vé cùng lúc.
- **Gợi ý:** Tích hợp hệ thống gợi ý sự kiện ngẫu nhiên ở cuối trang để tăng trải nghiệm người dùng.
