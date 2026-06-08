# Firebase Auth Demo Note

## Dang nhap

- App dung `FirebaseAuth.currentUser` de biet user nao dang dang nhap.
- Ve, binh luan, tao bai viet nen lay theo `currentUser.uid`.
- Dang nhap bang so dien thoai da bi tat. Man dang nhap chi nhan email va mat khau.

## Google Sign-In

- Bat provider Google trong Firebase Console: Authentication > Sign-in method > Google.
- Thay `google_web_client_id` trong `app/src/main/res/values/strings.xml` bang Web client ID cua Firebase/Google Cloud.
- Google Cloud project phai la Firebase project `fanzone-268e8`, project number `936697997604`.
- Tao Android OAuth client trong Google Auth Platform/Clients:
  - Package name: `com.example.myapplication`
  - Android client ID hien tai: `936697997604-4j09771o8qrose1o0prcjm29lt3oujks.apps.googleusercontent.com`
  - SHA-1 debug: `32:81:7E:62:E9:62:A4:E2:9B:A4:FB:5A:FE:58:66:44:82:9C:79:D3`
  - SHA-256 debug: `16:8F:56:C8:D1:A8:F1:62:D5:30:F1:EF:83:32:48:1F:09:CA:54:83:6C:6F:2D:52:C9:A5:76:84:9B:2A:E5:68`
- Sau khi them SHA/OAuth client, tai lai `google-services.json` tu Firebase Project settings va thay vao `app/google-services.json`.
- Neu user da co tai khoan email/password: dang nhap bang email/password truoc, vao Thong tin tai khoan, bam `Lien ket Google`. Sau do co the dang nhap bang Google.
- Neu tao tai khoan email/password ma email da ton tai, app chan va bao `Email nay da duoc dung.`
- Neu dang nhap Google gap email da thuoc provider khac, app bao dang nhap email/password truoc roi lien ket Google.

## User Firestore Schema

Collection: `users`

```json
{
  "uid": null,
  "email": null,
  "displayName": null,
  "phone": null,
  "birthday": null,
  "gender": null,
  "avatarUrl": null,
  "emailVerified": null,
  "authProviders": null,
  "schemaVersion": 1,
  "createdAt": null,
  "updatedAt": null
}
```

- File seed mau nam o `scripts/user_seed.json`, document id la `__user_schema_seed__`.
- Khi app luu user that dau tien, code se xoa document seed nay.
- Avatar trong trang thong tin tai khoan duoc upload qua Cloudinary va luu lai bang truong `avatarUrl`.
