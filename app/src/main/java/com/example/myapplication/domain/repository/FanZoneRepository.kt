package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.Category
import com.example.myapplication.domain.model.CommunityPost
import com.example.myapplication.domain.model.Event
import com.example.myapplication.domain.model.PaymentMethod
import com.example.myapplication.domain.model.SupportShortcut
import com.example.myapplication.domain.model.TicketTier
import com.example.myapplication.domain.model.TicketWalletItem
import com.example.myapplication.domain.model.UserProfile

interface FanZoneRepository {
    val user: UserProfile
    val categories: List<Category>
    val events: List<Event>
    val tiers: List<TicketTier>
    val posts: List<CommunityPost>
    val walletSeed: List<TicketWalletItem>
    val paymentMethods: List<PaymentMethod>
    val supportShortcuts: List<SupportShortcut>
}

