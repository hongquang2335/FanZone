import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
import os

# For local development with emulator
os.environ["FIRESTORE_EMULATOR_HOST"] = "localhost:8080"
firebase_admin.initialize_app(credentials.AnonymousCredentials(), options={'projectId': 'fanzone-app'})

db = firestore.client()

def seed_events():
    events = [
        {
            "id": "23663",
            "title": "ART WORKSHOP \" UJI MATCHA CHEESECAKE TARTE\"",
            "description": "Workshop làm bánh Uji Matcha Cheesecake Tarte là một trải nghiệm đầy thú vị...",
            "startTime": "20:00, Thứ Bảy 15/06/2026",
            "venue": "Garden Art",
            "address": "Lầu 1, 386/17C Lê Văn Sỹ, P.14, Q.3, TP.HCM",
            "orgName": "Garden Art",
            "priceFrom": 420000,
            "status": "sale_closed",
            "ticketTypes": [
                {"typeId": "VIP", "price": 1100000, "zoneName": "Khu vực A (Gần sân khấu)", "status": "available"},
                {"typeId": "Standard", "price": 300000, "zoneName": "Khu vực B (Phổ thông)", "status": "available"}
            ]
        },
        {
            "id": "neon-night",
            "title": "Neon Nights Festival 2026",
            "description": "Đại nhạc hội lớn nhất năm quy tụ dàn sao cực khủng...",
            "startTime": "19:00, Thứ Bảy 20/07/2026",
            "venue": "Sân vận động Mỹ Đình",
            "orgName": "FanZone Entertainment",
            "priceFrom": 500000,
            "status": "on_sale"
        }
    ]
    for event in events:
        db.collection('event').document(event['id']).set(event)
    print("Events seeded.")

def seed_orders():
    orders = [
        {
            "bookingId": "booking_101",
            "userId": "user_001",
            "eventId": "23663",
            "eventTitle": "ART WORKSHOP \" UJI MATCHA CHEESECAKE TARTE\"",
            "items": [
                {"ticketType": "VIP", "quantity": 2, "price": 750000}
            ],
            "totalPrice": 1500000,
            "paymentStatus": "success",
            "paymentMethod": "ZaloPay",
            "sellerId": "system",
            "createdAt": firestore.SERVER_TIMESTAMP,
            "qrCodeData": "BILLING_QR_101",
            "venue": "Garden Art, TP.HCM",
            "startTime": "20:00, 15/06/2026"
        },
        {
            "bookingId": "booking_102",
            "userId": "user_001",
            "eventId": "neon-night",
            "eventTitle": "Neon Nights Festival 2026",
            "items": [
                {"ticketType": "Standard", "quantity": 1, "price": 500000}
            ],
            "totalPrice": 500000,
            "paymentStatus": "success",
            "paymentMethod": "MoMo",
            "sellerId": "system",
            "createdAt": firestore.SERVER_TIMESTAMP,
            "qrCodeData": "BILLING_QR_102",
            "venue": "SVĐ Mỹ Đình, Hà Nội",
            "startTime": "19:00, 20/07/2026"
        }
    ]
    for order in orders:
        db.collection('orders').document(order['bookingId']).set(order)
    print("Orders seeded.")

def seed_user_tickets():
    tickets = [
        {
            "ticketId": "ticket_999",
            "eventId": "23663",
            "bookingId": "booking_101",
            "eventTitle": "ART WORKSHOP \" UJI MATCHA CHEESECAKE TARTE\"",
            "startTime": "20:00, 15/06/2026",
            "venue": "Garden Art",
            "ticketType": "VIP",
            "zoneName": "Khu vực A (Gần sân khấu)",
            "purchasePrice": 1100000,
            "qrCodeData": "CHECKIN_QR_999",
            "status": "UPCOMING"
        },
        {
            "ticketId": "ticket_1000",
            "eventId": "23663",
            "bookingId": "booking_101",
            "eventTitle": "ART WORKSHOP \" UJI MATCHA CHEESECAKE TARTE\"",
            "startTime": "20:00, 15/06/2026",
            "venue": "Garden Art",
            "ticketType": "VIP",
            "zoneName": "Khu vực A (Gần sân khấu)",
            "purchasePrice": 1100000,
            "qrCodeData": "CHECKIN_QR_1000",
            "status": "UPCOMING"
        }
    ]
    uid = "user_001"
    for ticket in tickets:
        db.collection('users').document(uid).collection('my_tickets').document(ticket['ticketId']).set(ticket)
    print("User tickets seeded.")

def seed_resale_market():
    resales = [
        {
            "resaleId": "resale_001",
            "eventId": "23663",
            "eventTitle": "ART WORKSHOP \" UJI MATCHA CHEESECAKE TARTE\"",
            "sellerId": "user_001",
            "originalTicketId": "ticket_999",
            "ticketType": "VIP",
            "resalePrice": 1200000,
            "status": "available",
            "createdAt": firestore.SERVER_TIMESTAMP,
            "note": "Kẹt lịch học đột xuất nên cần pass lại vé VIP"
        }
    ]
    for resale in resales:
        db.collection('resale_market').document(resale['resaleId']).set(resale)
    print("Resale market seeded.")

if __name__ == "__main__":
    seed_events()
    seed_orders()
    seed_user_tickets()
    seed_resale_market()
    print("Done seeding data.")
