package com.example.myapplication.feature.checkout

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Message
import android.util.Log
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.myapplication.R
import com.example.myapplication.core.designsystem.component.formatPrice
import com.example.myapplication.core.designsystem.theme.VibeCanvas
import com.example.myapplication.core.designsystem.theme.VibeGreen
import com.example.myapplication.core.designsystem.theme.VibeGreenDark
import com.example.myapplication.core.designsystem.theme.VibeStroke
import com.example.myapplication.core.designsystem.theme.VibeSurfaceMuted
import com.example.myapplication.core.designsystem.theme.VibeText
import com.example.myapplication.domain.model.Event
import com.example.myapplication.domain.model.EventSeat
import com.example.myapplication.domain.model.PaymentMethod
import com.example.myapplication.domain.model.TicketTier
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

private val CheckoutBackground = Color(0xFFF6F3F2)
private val CheckoutMetaText = Color(0xFF3D4A3F)
private val TicketLineBorder = Color(0x26BCCABC)

@Composable
fun CheckoutScreen(
    event: Event,
    tiers: List<TicketTier>,
    quantities: Map<String, Int>,
    selectedSeats: List<EventSeat>,
    paymentMethods: List<PaymentMethod>,
    selectedPaymentMethod: String,
    isCreatingPayment: Boolean,
    isVerifyingPayment: Boolean,
    paymentUrl: String?,
    paymentError: String?,
    onBack: () -> Unit,
    onSelectPayment: (String) -> Unit,
    onConfirm: () -> Unit,
    onPaymentReturn: (String, String) -> Unit,
    onPaymentDismiss: () -> Unit,
    onPaymentWebError: () -> Unit,
    onDismissPaymentError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ticketGroups = remember(selectedSeats, tiers, quantities) {
        buildTicketGroups(selectedSeats, tiers, quantities)
    }
    val total = ticketGroups.sumOf { it.price * it.quantity }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { CheckoutTopBar(onBack = onBack) },
        bottomBar = {
            CheckoutBottomBar(
                enabled = total > 0,
                isLoading = isCreatingPayment || isVerifyingPayment,
                onConfirm = onConfirm
            )
        },
        containerColor = CheckoutBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 32.dp, bottom = 132.dp),
            verticalArrangement = Arrangement.spacedBy(40.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 512.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    SectionTitle("Tóm tắt đơn hàng", fontSize = 28)
                    OrderSummaryCard(
                        event = event,
                        ticketGroups = ticketGroups,
                        total = total
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 512.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    SectionTitle("Phương thức thanh toán", fontSize = 24)
                    VnpayPaymentCard()
                }
            }
        }
    }

    if (paymentUrl != null) {
        VnpayPaymentWebView(
            paymentUrl = paymentUrl,
            onPaymentReturn = onPaymentReturn,
            onDismiss = onPaymentDismiss,
            onWebError = onPaymentWebError
        )
    }

    if (isCreatingPayment || isVerifyingPayment) {
        PaymentLoadingOverlay(
            message = if (isVerifyingPayment) {
                "Đang xác minh giao dịch..."
            } else {
                "Đang kết nối VNPAY..."
            }
        )
    }

    if (paymentError != null) {
        PaymentFailureDialog(
            message = paymentError,
            onDismiss = onDismissPaymentError
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CheckoutTopBar(onBack: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Thanh toán",
                color = VibeText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        },
        navigationIcon = {
            Surface(
                modifier = Modifier
                    .padding(start = 24.dp)
                    .size(40.dp),
                shape = CircleShape,
                color = Color(0xFFEAE7E7)
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = VibeText
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = VibeCanvas.copy(alpha = 0.86f)
        )
    )
}

@Composable
private fun SectionTitle(text: String, fontSize: Int) {
    Text(
        text = text,
        color = VibeText,
        fontSize = fontSize.sp,
        lineHeight = (fontSize + 14).sp,
        fontWeight = FontWeight.ExtraBold,
        maxLines = 1
    )
}

@Composable
private fun OrderSummaryCard(
    event: Event,
    ticketGroups: List<TicketGroup>,
    total: Int
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(20.dp, RoundedCornerShape(28.dp), ambientColor = Color.Black.copy(alpha = 0.03f)),
        shape = RoundedCornerShape(28.dp),
        color = VibeCanvas
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            EventMeta(event)
            ticketGroups.forEach { group ->
                TicketBreakdownCard(group)
            }
            GrandTotal(total)
        }
    }
}

@Composable
private fun EventMeta(event: Event) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = 80.dp, height = 96.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF0EDED))
        ) {
            if (!event.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = event.imageUrl,
                    contentDescription = event.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else if (event.imageRes != 0) {
                Image(
                    painter = painterResource(event.imageRes),
                    contentDescription = event.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 104.dp)
                .padding(bottom = 4.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = event.title,
                color = VibeText,
                fontSize = 18.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(8.dp))
            MetaRow(
                icon = Icons.Default.CalendarToday,
                text = event.checkoutSchedule()
            )
            Spacer(Modifier.height(6.dp))
            MetaRow(
                icon = Icons.Default.LocationOn,
                text = event.venue.ifBlank { event.city }
            )
            Spacer(Modifier.height(4.dp))
        }
    }
}

@Composable
private fun MetaRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = CheckoutMetaText,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = text,
            color = CheckoutMetaText,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun TicketBreakdownCard(group: TicketGroup) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = VibeSurfaceMuted
    ) {
        Column(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = group.name,
                        color = VibeGreenDark,
                        fontSize = 18.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Ghế: ${group.seatLabels.joinToString()}",
                        color = VibeGreenDark,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = VibeCanvas,
                    shadowElevation = 1.dp
                ) {
                    Text(
                        text = "x${group.quantity}",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        color = VibeText,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(TicketLineBorder)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Giá mỗi vé",
                    color = CheckoutMetaText,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
                Text(
                    text = formatPrice(group.price),
                    color = VibeText,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun GrandTotal(total: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = "Tổng thanh toán",
            color = CheckoutMetaText,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = formatPrice(total),
            color = VibeGreenDark,
            fontSize = 28.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1
        )
    }
}

@Composable
private fun VnpayPaymentCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = VibeGreen.copy(alpha = 0.10f),
        border = BorderStroke(2.dp, VibeGreenDark.copy(alpha = 0.40f))
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            VnpayLogo()
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "Ví VNPAY",
                    color = VibeText,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Thanh toán an toàn qua cổng VNPAY",
                    color = CheckoutMetaText,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .border(2.dp, VibeGreenDark, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(VibeGreenDark, CircleShape)
                )
            }
        }
    }
}

@Composable
private fun VnpayLogo() {
    Surface(
        modifier = Modifier.size(48.dp),
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Image(
            painter = painterResource(R.drawable.vnpay_app_logo),
            contentDescription = "VNPAY",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
                .clip(RoundedCornerShape(6.dp))
        )
    }
}

@Composable
private fun CheckoutBottomBar(
    enabled: Boolean,
    isLoading: Boolean,
    onConfirm: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = VibeCanvas.copy(alpha = 0.90f),
        shadowElevation = 16.dp
    ) {
        Button(
            onClick = onConfirm,
            enabled = enabled && !isLoading,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .fillMaxWidth()
                .height(58.dp)
                .shadow(12.dp, CircleShape)
                .background(
                    brush = Brush.linearGradient(listOf(VibeGreenDark, VibeGreen)),
                    shape = CircleShape
                ),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                disabledContainerColor = VibeStroke,
                contentColor = Color.White,
                disabledContentColor = CheckoutMetaText
            ),
            contentPadding = PaddingValues(horizontal = 24.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isLoading) "Đang xử lý..." else "Xác nhận thanh toán",
                    fontSize = 18.sp,
                    lineHeight = 27.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VnpayPaymentWebView(
    paymentUrl: String,
    onPaymentReturn: (String, String) -> Unit,
    onDismiss: () -> Unit,
    onWebError: () -> Unit
) {
    val context = LocalContext.current
    val webViewHolder = remember { arrayOfNulls<WebView>(1) }
    val popupWebViews = remember { mutableListOf<WebView>() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Thanh toán VNPAY",
                            fontWeight = FontWeight.Bold,
                            color = VibeText
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Đóng thanh toán"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = VibeCanvas
                    )
                )
            },
            containerColor = Color.White
        ) { padding ->
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                factory = {
                    val container = FrameLayout(context)
                    var handledReturn = false
                    lateinit var createPaymentWebView: (Boolean) -> WebView

                    fun handleUri(uri: Uri?): Boolean {
                        if (
                            uri?.scheme == PAYMENT_RETURN_SCHEME &&
                            uri.host == PAYMENT_RETURN_HOST
                        ) {
                            handledReturn = true
                            onPaymentReturn(
                                uri.getQueryParameter("txnRef").orEmpty(),
                                uri.getQueryParameter("responseCode").orEmpty()
                            )
                            return true
                        }

                        val scheme = uri?.scheme?.lowercase()
                        if (scheme != null && scheme !in WEB_SCHEMES) {
                            return runCatching {
                                val intent = if (scheme == "intent") {
                                    Intent.parseUri(uri.toString(), Intent.URI_INTENT_SCHEME)
                                } else {
                                    Intent(Intent.ACTION_VIEW, uri)
                                }
                                context.startActivity(intent)
                            }.isSuccess
                        }
                        return false
                    }

                    createPaymentWebView = { isPopup ->
                        WebView(context).apply paymentWebView@{
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.databaseEnabled = true
                            settings.javaScriptCanOpenWindowsAutomatically = true
                            settings.setSupportMultipleWindows(true)
                            settings.cacheMode = WebSettings.LOAD_NO_CACHE
                            settings.mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW

                            CookieManager.getInstance().apply {
                                setAcceptCookie(true)
                                setAcceptThirdPartyCookies(this@paymentWebView, true)
                                flush()
                            }

                            webViewClient = object : WebViewClient() {
                                override fun shouldOverrideUrlLoading(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): Boolean = handleUri(request?.url)

                                @Deprecated("Deprecated in Java")
                                override fun shouldOverrideUrlLoading(
                                    view: WebView?,
                                    url: String?
                                ): Boolean = handleUri(url?.let(Uri::parse))

                                override fun onPageStarted(
                                    view: WebView?,
                                    url: String?,
                                    favicon: Bitmap?
                                ) {
                                    Log.d(VNPAY_WEB_TAG, "Loading: ${url?.safeLogUrl()}")
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    Log.d(VNPAY_WEB_TAG, "Finished: ${url?.safeLogUrl()}")
                                }

                                override fun onReceivedError(
                                    view: WebView?,
                                    request: WebResourceRequest?,
                                    error: WebResourceError?
                                ) {
                                    if (request?.isForMainFrame == true && !handledReturn) {
                                        Log.e(
                                            VNPAY_WEB_TAG,
                                            "Main frame error ${error?.errorCode}: ${error?.description}"
                                        )
                                        onWebError()
                                    }
                                }
                            }

                            webChromeClient = object : WebChromeClient() {
                                override fun onConsoleMessage(
                                    consoleMessage: ConsoleMessage
                                ): Boolean {
                                    Log.d(
                                        VNPAY_WEB_TAG,
                                        "JS ${consoleMessage.messageLevel()}: ${consoleMessage.message()}"
                                    )
                                    return true
                                }

                                override fun onCreateWindow(
                                    view: WebView?,
                                    isDialog: Boolean,
                                    isUserGesture: Boolean,
                                    resultMsg: Message?
                                ): Boolean {
                                    val transport = resultMsg?.obj as? WebView.WebViewTransport
                                        ?: return false
                                    val popup = createPaymentWebView(true)
                                    popupWebViews += popup
                                    container.addView(
                                        popup,
                                        FrameLayout.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.MATCH_PARENT
                                        )
                                    )
                                    transport.webView = popup
                                    resultMsg.sendToTarget()
                                    Log.d(VNPAY_WEB_TAG, "Opened payment popup WebView")
                                    return true
                                }

                                override fun onCloseWindow(window: WebView?) {
                                    window ?: return
                                    container.removeView(window)
                                    popupWebViews.remove(window)
                                    window.destroy()
                                    Log.d(VNPAY_WEB_TAG, "Closed payment popup WebView")
                                }
                            }

                            if (isPopup) {
                                setBackgroundColor(android.graphics.Color.WHITE)
                            }
                        }
                    }

                    val mainWebView = createPaymentWebView(false)
                    container.addView(
                        mainWebView,
                        FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    )
                    mainWebView.loadUrl(paymentUrl)
                    webViewHolder[0] = mainWebView
                    container
                }
            )
        }
    }

    DisposableEffect(paymentUrl) {
        onDispose {
            popupWebViews.toList().forEach { popup ->
                (popup.parent as? ViewGroup)?.removeView(popup)
                popup.stopLoading()
                popup.destroy()
            }
            popupWebViews.clear()
            webViewHolder[0]?.stopLoading()
            webViewHolder[0]?.destroy()
            webViewHolder[0] = null
        }
    }
}

@Composable
private fun PaymentLoadingOverlay(message: String) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f)),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(color = VibeGreenDark)
                    Text(
                        text = message,
                        color = VibeText,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentFailureDialog(
    message: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.60f))
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 384.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                shadowElevation = 24.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(32.dp))
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .shadow(
                                20.dp,
                                CircleShape,
                                ambientColor = Color(0x26BA1A1A)
                            )
                            .background(Color(0xFFFFDAD6), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = Color(0xFFBA1A1A),
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                    Text(
                        text = "Thanh toán thất bại",
                        color = VibeText,
                        fontSize = 30.sp,
                        lineHeight = 36.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = message,
                        color = CheckoutMetaText,
                        fontSize = 16.sp,
                        lineHeight = 26.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(Modifier.height(32.dp))
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(20.dp, CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(VibeGreenDark, VibeGreen)
                                ),
                                CircleShape
                            ),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Đóng",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}

private const val PAYMENT_RETURN_SCHEME = "eventhub"
private const val PAYMENT_RETURN_HOST = "vnpay-return"
private const val VNPAY_WEB_TAG = "VnpayWebView"
private val WEB_SCHEMES = setOf("http", "https", "about", "javascript", "data", "blob")

private fun String.safeLogUrl(): String = substringBefore('?')

private data class TicketGroup(
    val name: String,
    val price: Int,
    val quantity: Int,
    val seatLabels: List<String>
)

private fun buildTicketGroups(
    selectedSeats: List<EventSeat>,
    tiers: List<TicketTier>,
    quantities: Map<String, Int>
): List<TicketGroup> {
    if (selectedSeats.isNotEmpty()) {
        return selectedSeats
            .groupBy { seat -> seat.zoneId to seat.price }
            .values
            .map { seats ->
                val first = seats.first()
                TicketGroup(
                    name = first.ticketTypeName(),
                    price = first.price,
                    quantity = seats.size,
                    seatLabels = seats.map { it.seatId }.sortedWith { left, right ->
                        compareSeatLabel(left, right)
                    }
                )
            }
            .sortedByDescending { it.price }
    }

    return tiers
        .mapNotNull { tier ->
            val quantity = quantities[tier.id]?.takeIf { it > 0 } ?: return@mapNotNull null
            TicketGroup(
                name = tier.name,
                price = tier.price,
                quantity = quantity,
                seatLabels = List(quantity) { "Vé ${it + 1}" }
            )
        }
}

private fun EventSeat.ticketTypeName(): String {
    return when {
        isVip -> "VIP"
        zoneId == EventSeat.STANDARD_ZONE_ID -> "Standard"
        zoneId.isBlank() -> "Standard"
        else -> zoneId
            .replace('_', ' ')
            .split(' ')
            .joinToString(" ") { part -> part.replaceFirstChar(Char::uppercaseChar) }
    }
}

private fun compareSeatLabel(left: String, right: String): Int {
    val leftRow = left.takeWhile(Char::isLetter)
    val rightRow = right.takeWhile(Char::isLetter)
    val rowCompare = leftRow.compareTo(rightRow, ignoreCase = true)
    if (rowCompare != 0) return rowCompare
    val leftNumber = left.dropWhile(Char::isLetter).toIntOrNull() ?: 0
    val rightNumber = right.dropWhile(Char::isLetter).toIntOrNull() ?: 0
    return leftNumber.compareTo(rightNumber)
}

private fun Event.checkoutSchedule(): String {
    val start = startTime.toVietnamDate() ?: return schedule.replace("|", ", ")
    val end = endTime.toVietnamDate()
    val vietnamTimeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh")
    val timeFormat = SimpleDateFormat("HH:mm", Locale("vi", "VN")).apply {
        timeZone = vietnamTimeZone
    }
    val dateFormat = SimpleDateFormat("dd 'tháng' M, yyyy", Locale("vi", "VN")).apply {
        timeZone = vietnamTimeZone
    }
    val timeRange = if (end != null) {
        "${timeFormat.format(start)}-${timeFormat.format(end)}"
    } else {
        timeFormat.format(start)
    }
    return "$timeRange, ${dateFormat.format(start)}"
}

private fun String.toVietnamDate(): java.util.Date? {
    if (isBlank()) return null
    return try {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.US).parse(trim())
    } catch (_: Exception) {
        null
    }
}
