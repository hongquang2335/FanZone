# VNPAY Sandbox with local Express and ngrok

This setup is intended for local Sandbox testing without Firebase Blaze.

The payment secret and Firebase service account remain on the development
computer. They are never included in the Android APK.

## Architecture

```text
Android app
  -> HTTPS ngrok URL
  -> Express at localhost:3000
  -> VNPAY Sandbox + Firebase Auth/Firestore

VNPAY Return/IPN
  -> HTTPS ngrok URL
  -> Express at localhost:3000
```

The app opens the VNPAY payment page in a WebView. It only navigates to the
Success screen after the Express backend receives and validates the VNPAY IPN.

## 1. Prepare local Firebase credentials

In Firebase Console:

1. Open project `fanzone-268e8`.
2. Open **Project settings > Service accounts**.
3. Generate a new private key.
4. Save it as:

```text
backend/service-account.json
```

This file is ignored by Git. Never commit or share it.

## 2. Install and configure ngrok

Install ngrok from:

```text
https://ngrok.com/downloads
```

Create a free ngrok account and configure its authtoken:

```powershell
ngrok config add-authtoken YOUR_NGROK_AUTHTOKEN
```

Start the public HTTPS tunnel:

```powershell
ngrok http 3000
```

Copy the HTTPS forwarding URL displayed by ngrok, for example:

```text
https://example-name.ngrok-free.app
```

The backend and ngrok processes must remain running throughout a payment.

## 3. Configure the Express backend

Copy:

```text
backend/.env.example
```

to:

```text
backend/.env
```

Set:

```dotenv
PORT=3000
PUBLIC_BASE_URL=https://example-name.ngrok-free.app
VNPAY_TMN_CODE=ZCF6E6PD
VNPAY_HASH_SECRET=YOUR_ROTATED_SANDBOX_SECRET
VNPAY_URL=https://sandbox.vnpayment.vn/paymentv2/vpcpay.html
GOOGLE_APPLICATION_CREDENTIALS=./service-account.json
```

The Sandbox secret previously shared in conversation should be rotated before
use. Do not put the secret in Kotlin, Gradle, Firestore, or Git.

Install and start the backend:

```powershell
cd backend
npm.cmd install
npm.cmd start
```

Verify it from another terminal:

```powershell
Invoke-RestMethod https://example-name.ngrok-free.app/health
```

Expected result:

```json
{"status":"ok","environment":"vnpay-sandbox"}
```

## 4. Configure the Android app

In the ignored root `local.properties` file, set the same ngrok base URL:

```properties
VNPAY_BACKEND_URL=https://example-name.ngrok-free.app
```

Rebuild/reinstall the Android app whenever this value changes.

## 5. Configure VNPAY Sandbox

The backend sends this Return URL dynamically in every signed payment request:

```text
https://example-name.ngrok-free.app/vnpay/return
```

It does not need to be entered in the VNPAY management page.

If the VNPAY Sandbox management page provides an IPN URL setting, use:

```text
https://example-name.ngrok-free.app/vnpay/ipn
```

The backend validates and processes both the signed Return and IPN callbacks.
This keeps local Sandbox testing working when an IPN is delayed, while IPN
remains the preferred server-to-server confirmation.

If the ngrok domain changes, update:

1. `backend/.env` -> `PUBLIC_BASE_URL`
2. `local.properties` -> `VNPAY_BACKEND_URL`
3. The VNPAY Sandbox IPN URL, if one is configured

## 6. Test order

Use this startup order:

1. Run `ngrok http 3000`.
2. Confirm the ngrok HTTPS URL.
3. Update `.env`, `local.properties`, and the VNPAY IPN URL if necessary.
4. Stop any old backend process, then run `npm.cmd start` inside `backend`.
5. Build and launch Android.
6. Select a seat and confirm payment.

## Limitations

- This is a development/testing setup, not production hosting.
- Closing the computer, Express, or ngrok breaks Return/IPN delivery.
- The computer must have internet access throughout the transaction.
- Do not add ngrok OAuth/basic authentication to VNPAY endpoints because
  VNPAY must call the IPN without an interactive login.
- A public development tunnel should only expose this backend while testing.

For production, move the same Express app to an always-on HTTPS server and
rotate all secrets.
