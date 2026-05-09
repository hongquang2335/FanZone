package com.example.myapplication

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.myapplication.feature.success.PurchaseSuccessScreen
import com.example.myapplication.domain.model.TicketStatus
import com.example.myapplication.domain.model.TicketWalletItem
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class PurchaseSuccessScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun walletButton_invokesWalletCallback() {
        var walletClicks = 0

        composeRule.setContent {
            PurchaseSuccessScreen(
                ticket = sampleTicket(),
                onOpenWallet = { walletClicks++ },
                onGoHome = {}
            )
        }

        composeRule.onNodeWithText("Vao vi ve cua toi").assertIsDisplayed().performClick()
        composeRule.runOnIdle {
            assertEquals(1, walletClicks)
        }
    }

    @Test
    fun homeButton_invokesHomeCallback() {
        var homeClicks = 0

        composeRule.setContent {
            PurchaseSuccessScreen(
                ticket = sampleTicket(),
                onOpenWallet = {},
                onGoHome = { homeClicks++ }
            )
        }

        composeRule.onNodeWithText("Ve trang chu").assertIsDisplayed().performClick()
        composeRule.runOnIdle {
            assertEquals(1, homeClicks)
        }
    }

    private fun sampleTicket() = TicketWalletItem(
        id = "ticket-1",
        eventId = "neon-night",
        eventTitle = "Neon Nights Festival 2024",
        seatLabel = "Zone A (VIP) x1",
        schedule = "20:00, Thu Bay 15/06/2024",
        venue = "San van dong My Dinh, Ha Noi",
        qrCode = "QR-NEON-1",
        status = TicketStatus.UPCOMING
    )
}

