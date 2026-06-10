"use strict";

require("dotenv").config();

const crypto = require("crypto");
const cors = require("cors");
const express = require("express");
const fs = require("fs");
const path = require("path");
const qs = require("qs");
const {
  applicationDefault,
  initializeApp,
} = require("firebase-admin/app");
const { getAuth } = require("firebase-admin/auth");
const {
  FieldValue,
  getFirestore,
} = require("firebase-admin/firestore");

const config = loadConfig();

initializeApp({
  credential: applicationDefault(),
  projectId: "fanzone-268e8",
});

const db = getFirestore();
const app = express();

app.set("trust proxy", true);
app.disable("x-powered-by");
app.use(cors());
app.use(express.json({ limit: "32kb" }));

app.get("/health", (_request, response) => {
  response.json({ status: "ok", environment: "vnpay-sandbox" });
});

app.post("/api/payments/vnpay", async (request, response) => {
  try {
    const user = await requireUser(request);
    const eventId = String(request.body?.eventId || "").trim();
    const seatIds = uniqueStrings(request.body?.seatIds);

    if (!eventId || seatIds.length === 0 || seatIds.length > 8) {
      response.status(400).json({ error: "invalid_payment_request" });
      return;
    }

    const seatRefs = seatIds.map((seatId) =>
      db.collection("event_seats").doc(seatId),
    );
    const seatSnapshots = await db.getAll(...seatRefs);
    const seats = seatSnapshots.map((snapshot) => {
      if (!snapshot.exists) {
        throw new PaymentError("seat_not_found");
      }
      const data = snapshot.data();
      if (data.eventId !== eventId) {
        throw new PaymentError("seat_event_mismatch");
      }
      if (String(data.status || "").toLowerCase() !== "available") {
        throw new PaymentError("seat_unavailable");
      }
      const price = Number(data.price || 0);
      if (!Number.isSafeInteger(price) || price <= 0) {
        throw new PaymentError("invalid_seat_price");
      }
      return {
        documentId: snapshot.id,
        seatId: String(data.seatId || snapshot.id),
        zoneId: String(data.zoneId || ""),
        price,
      };
    });

    const amount = seats.reduce((sum, seat) => sum + seat.price, 0);
    const eventSnapshot = await db.collection("event").doc(eventId).get();
    if (!eventSnapshot.exists) {
      response.status(404).json({ error: "event_not_found" });
      return;
    }

    const event = eventSnapshot.data();
    const txnRef = createTransactionReference();
    const paymentRef = db.collection("vnpay_payments").doc(txnRef);

    await paymentRef.create({
      txnRef,
      uid: user.uid,
      eventId,
      eventTitle: String(event.title || ""),
      eventSchedule: String(event.startTime || event.schedule || ""),
      eventEndTime: String(event.endTime || ""),
      venue: String(event.venue || event.address || event.city || ""),
      imageUrl: String(event.banner || event.imageUrl || ""),
      amount,
      seats,
      status: "pending",
      paymentMethod: "vnpay",
      createdAt: FieldValue.serverTimestamp(),
      updatedAt: FieldValue.serverTimestamp(),
    });

    const now = new Date();
    const expiresAt = new Date(now.getTime() + 15 * 60 * 1000);
    const params = {
      vnp_Amount: String(amount * 100),
      vnp_Command: "pay",
      vnp_CreateDate: formatVnpayDate(now),
      vnp_CurrCode: "VND",
      vnp_ExpireDate: formatVnpayDate(expiresAt),
      vnp_IpAddr: clientIp(request),
      vnp_Locale: "vn",
      vnp_OrderInfo: `Thanh toan ve su kien ${eventId}`,
      vnp_OrderType: "other",
      vnp_ReturnUrl: `${config.publicBaseUrl}/vnpay/return`,
      vnp_TmnCode: config.tmnCode,
      vnp_TxnRef: txnRef,
      vnp_Version: "2.1.0",
    };

    const encodedParams = sortAndEncode(params);
    const signData = qs.stringify(encodedParams, { encode: false });
    const secureHash = hmacSha512(config.hashSecret, signData);
    const paymentUrl =
      `${config.vnpayUrl}?${signData}&vnp_SecureHash=${secureHash}`;

    console.log(
      `Created VNPAY payment txnRef=${txnRef} eventId=${eventId} amount=${amount}`,
    );
    response.json({ paymentUrl, txnRef });
  } catch (error) {
    handleHttpError(response, error);
  }
});

app.post("/api/payments/vnpay/status", async (request, response) => {
  try {
    const user = await requireUser(request);
    const txnRef = String(request.body?.txnRef || "").trim();
    if (!txnRef) {
      response.status(400).json({ error: "missing_transaction_reference" });
      return;
    }

    const paymentSnapshot =
      await db.collection("vnpay_payments").doc(txnRef).get();
    if (!paymentSnapshot.exists) {
      response.status(404).json({ error: "payment_not_found" });
      return;
    }

    const payment = paymentSnapshot.data();
    if (payment.uid !== user.uid) {
      response.status(403).json({ error: "forbidden" });
      return;
    }

    response.json({
      txnRef,
      status: payment.status,
      responseCode: payment.responseCode || null,
      transactionStatus: payment.transactionStatus || null,
    });
  } catch (error) {
    handleHttpError(response, error);
  }
});

app.get("/vnpay/ipn", async (request, response) => {
  const validation = validateVnpayResponse(request.query, config.hashSecret);
  if (!validation.valid) {
    console.warn("Rejected VNPAY IPN with an invalid signature");
    response.status(200).json({
      RspCode: "97",
      Message: "Invalid signature",
    });
    return;
  }

  const params = validation.params;

  try {
    const result = await confirmVnpayPayment(params);
    console.log(
      `Processed VNPAY IPN txnRef=${String(params.vnp_TxnRef || "")}` +
      ` responseCode=${String(params.vnp_ResponseCode || "")}` +
      ` result=${result.RspCode}`,
    );
    response.status(200).json(result);
  } catch (error) {
    console.error("VNPAY IPN failed", error);
    response.status(200).json({
      RspCode: "99",
      Message: "Unknown error",
    });
  }
});

app.get("/vnpay/return", async (request, response) => {
  const validation = validateVnpayResponse(request.query, config.hashSecret);
  const params = validation.params;
  const txnRef = String(params.vnp_TxnRef || "");
  let responseCode = validation.valid
    ? String(params.vnp_ResponseCode || "99")
    : "97";

  if (validation.valid) {
    try {
      const result = await confirmVnpayPayment(params);
      console.log(
        `Processed VNPAY Return txnRef=${txnRef}` +
        ` responseCode=${responseCode} result=${result.RspCode}`,
      );
      if (!["00", "02"].includes(result.RspCode)) {
        responseCode = result.RspCode;
      }
    } catch (error) {
      console.error("VNPAY Return processing failed", error);
      responseCode = "99";
    }
  } else {
    console.warn(`Rejected VNPAY Return txnRef=${txnRef} with invalid signature`);
  }

  const redirectUrl =
    `eventhub://vnpay-return?txnRef=${encodeURIComponent(txnRef)}` +
    `&responseCode=${encodeURIComponent(responseCode)}`;

  response
    .status(200)
    .set("Content-Type", "text/html; charset=utf-8")
    .send(paymentReturnHtml(redirectUrl));
});

app.use((error, _request, response, _next) => {
  console.error(error);
  response.status(500).json({ error: "internal_error" });
});

const server = app.listen(config.port, "0.0.0.0", () => {
  console.log(`Event Hub VNPAY backend: http://localhost:${config.port}`);
  console.log(`Public base URL: ${config.publicBaseUrl}`);
  console.log(`VNPAY Return URL: ${config.publicBaseUrl}/vnpay/return`);
  console.log(`VNPAY IPN URL: ${config.publicBaseUrl}/vnpay/ipn`);
});

process.on("SIGINT", () => server.close(() => process.exit(0)));
process.on("SIGTERM", () => server.close(() => process.exit(0)));

async function confirmVnpayPayment(params) {
  const txnRef = String(params.vnp_TxnRef || "");
  if (!txnRef) {
    return { RspCode: "01", Message: "Order not found" };
  }

  const paymentRef = db.collection("vnpay_payments").doc(txnRef);
  return db.runTransaction(async (transaction) => {
    const paymentSnapshot = await transaction.get(paymentRef);
    if (!paymentSnapshot.exists) {
      return { RspCode: "01", Message: "Order not found" };
    }

    const payment = paymentSnapshot.data();
    if (payment.status !== "pending") {
      return { RspCode: "02", Message: "Order already confirmed" };
    }

    const returnedAmount = Number(params.vnp_Amount || 0) / 100;
    if (returnedAmount !== payment.amount) {
      return { RspCode: "04", Message: "Invalid amount" };
    }

    const successful =
      params.vnp_ResponseCode === "00" &&
      params.vnp_TransactionStatus === "00";

    transaction.update(paymentRef, {
      status: successful ? "success" : "failed",
      responseCode: String(params.vnp_ResponseCode || ""),
      transactionStatus: String(params.vnp_TransactionStatus || ""),
      vnpayTransactionNo: String(params.vnp_TransactionNo || ""),
      bankCode: String(params.vnp_BankCode || ""),
      payDate: String(params.vnp_PayDate || ""),
      updatedAt: FieldValue.serverTimestamp(),
    });

    if (successful) {
      createPurchasedTickets(transaction, paymentRef, payment, txnRef);
    }

    return { RspCode: "00", Message: "Confirm success" };
  });
}

function createPurchasedTickets(transaction, paymentRef, payment, txnRef) {
  const orderRef = db.collection("orders").doc(txnRef);
  transaction.set(orderRef, {
    bookingId: txnRef,
    userId: payment.uid,
    eventId: payment.eventId,
    eventTitle: payment.eventTitle,
    totalPrice: payment.amount,
    paymentStatus: "success",
    paymentMethod: "VNPAY",
    qrCodeData: `VNPAY-${txnRef}`,
    venue: payment.venue,
    startTime: payment.eventSchedule,
    createdAt: FieldValue.serverTimestamp(),
    paymentRef,
  });

  for (const seat of payment.seats || []) {
    const seatRef = db.collection("event_seats").doc(seat.documentId);
    const ticketRef = db
      .collection("users")
      .doc(payment.uid)
      .collection("my_tickets")
      .doc(`${txnRef}-${seat.documentId}`);

    transaction.update(seatRef, {
      status: "sold",
      soldTo: payment.uid,
      orderId: txnRef,
      updatedAt: FieldValue.serverTimestamp(),
    });
    transaction.set(ticketRef, {
      ticketId: `${txnRef}-${seat.documentId}`,
      bookingId: txnRef,
      eventId: payment.eventId,
      eventTitle: payment.eventTitle,
      startTime: payment.eventSchedule,
      endTime: payment.eventEndTime,
      venue: payment.venue,
      imageUrl: payment.imageUrl,
      seatId: seat.seatId,
      zoneId: seat.zoneId,
      purchasePrice: seat.price,
      qrCodeData: `QR-${txnRef}-${seat.documentId}`,
      status: "UPCOMING",
      createdAt: FieldValue.serverTimestamp(),
    });
  }
}

async function requireUser(request) {
  const authorization = String(request.headers.authorization || "");
  if (!authorization.startsWith("Bearer ")) {
    throw new AuthError();
  }
  const token = authorization.substring("Bearer ".length);
  return getAuth().verifyIdToken(token);
}

function validateVnpayResponse(query, secret) {
  const params = {};
  for (const [key, value] of Object.entries(query || {})) {
    if (key.startsWith("vnp_")) {
      params[key] = Array.isArray(value) ? value[0] : String(value);
    }
  }

  const secureHash = String(params.vnp_SecureHash || "");
  delete params.vnp_SecureHash;
  delete params.vnp_SecureHashType;
  const encodedParams = sortAndEncode(params);
  const signData = qs.stringify(encodedParams, { encode: false });
  const expected = hmacSha512(secret, signData);
  return {
    valid: safeEquals(secureHash.toLowerCase(), expected.toLowerCase()),
    params,
  };
}

function sortAndEncode(params) {
  return Object.keys(params)
    .sort()
    .reduce((result, key) => {
      result[encodeURIComponent(key)] = encodeURIComponent(String(params[key]))
        .replace(/%20/g, "+");
      return result;
    }, {});
}

function hmacSha512(secret, data) {
  return crypto
    .createHmac("sha512", secret)
    .update(Buffer.from(data, "utf-8"))
    .digest("hex");
}

function safeEquals(left, right) {
  if (!left || left.length !== right.length) return false;
  return crypto.timingSafeEqual(Buffer.from(left), Buffer.from(right));
}

function uniqueStrings(value) {
  if (!Array.isArray(value)) return [];
  return [...new Set(value.map((item) => String(item).trim()).filter(Boolean))];
}

function createTransactionReference() {
  const suffix = crypto.randomBytes(3).toString("hex");
  return `${Date.now()}${suffix}`;
}

function formatVnpayDate(date) {
  const parts = new Intl.DateTimeFormat("en-GB", {
    timeZone: "Asia/Ho_Chi_Minh",
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
    hour12: false,
  }).formatToParts(date);
  const values = Object.fromEntries(
    parts.map((part) => [part.type, part.value]),
  );
  return (
    values.year +
    values.month +
    values.day +
    values.hour +
    values.minute +
    values.second
  );
}

function clientIp(request) {
  const forwarded = String(request.headers["x-forwarded-for"] || "")
    .split(",")[0]
    .trim();
  return forwarded || request.ip || "127.0.0.1";
}

function paymentReturnHtml(redirectUrl) {
  const safeUrl = redirectUrl.replace(/"/g, "&quot;");
  return `<!doctype html>
<html lang="vi">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width,initial-scale=1">
  <title>Kết quả thanh toán</title>
</head>
<body style="font-family:sans-serif;text-align:center;padding:48px 20px">
  <p>Đang xác minh kết quả thanh toán...</p>
  <p><a href="${safeUrl}">Quay lại ứng dụng</a></p>
  <script>window.location.replace(${JSON.stringify(redirectUrl)});</script>
</body>
</html>`;
}

function handleHttpError(response, error) {
  if (error instanceof AuthError) {
    response.status(401).json({ error: "unauthenticated" });
    return;
  }
  if (error instanceof PaymentError) {
    response.status(409).json({ error: error.message });
    return;
  }
  console.error(error);
  response.status(500).json({ error: "internal_error" });
}

function loadConfig() {
  const required = [
    "PUBLIC_BASE_URL",
    "VNPAY_TMN_CODE",
    "VNPAY_HASH_SECRET",
    "GOOGLE_APPLICATION_CREDENTIALS",
  ];
  const missing = required.filter((name) => !String(process.env[name] || "").trim());
  if (missing.length > 0) {
    throw new Error(`Missing environment variables: ${missing.join(", ")}`);
  }

  const publicBaseUrl = String(process.env.PUBLIC_BASE_URL)
    .trim()
    .replace(/\/+$/, "");
  if (!publicBaseUrl.startsWith("https://")) {
    throw new Error("PUBLIC_BASE_URL must be an HTTPS ngrok URL");
  }
  if (publicBaseUrl.includes("your-ngrok-domain")) {
    throw new Error("PUBLIC_BASE_URL still contains the example ngrok domain");
  }

  const hashSecret = String(process.env.VNPAY_HASH_SECRET).trim();
  if (hashSecret.startsWith("replace-with-")) {
    throw new Error("VNPAY_HASH_SECRET still contains the example value");
  }

  const credentialsPath = path.resolve(
    process.cwd(),
    String(process.env.GOOGLE_APPLICATION_CREDENTIALS).trim(),
  );
  if (!fs.existsSync(credentialsPath)) {
    throw new Error(
      `Firebase service account was not found at ${credentialsPath}`,
    );
  }

  const port = Number(process.env.PORT || 3000);
  if (!Number.isInteger(port) || port <= 0 || port > 65535) {
    throw new Error("PORT must be an integer between 1 and 65535");
  }

  return {
    port,
    publicBaseUrl,
    tmnCode: String(process.env.VNPAY_TMN_CODE).trim(),
    hashSecret,
    vnpayUrl: String(
      process.env.VNPAY_URL ||
      "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html",
    ).trim(),
  };
}

class AuthError extends Error {}
class PaymentError extends Error {}
