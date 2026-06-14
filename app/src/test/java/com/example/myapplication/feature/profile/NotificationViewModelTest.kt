package com.example.myapplication.feature.profile

import android.app.Application
import com.example.myapplication.app.AppDependencies
import com.example.myapplication.domain.repository.NotificationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test

class NotificationViewModelTest {

    private val mockApplication = mockk<Application>(relaxed = true)
    private val mockRepository = mockk<NotificationRepository>(relaxed = true)
    private val mockAuth = mockk<FirebaseAuth>(relaxed = true)
    private val mockUser = mockk<FirebaseUser>(relaxed = true)

    @Before
    fun setUp() {
        mockkStatic(FirebaseAuth::class)
        every { FirebaseAuth.getInstance() } returns mockAuth
        every { mockAuth.currentUser } returns mockUser
        every { mockUser.uid } returns "user-123"

        AppDependencies.setNotificationRepositoryForTesting(mockRepository)
    }

    @After
    fun tearDown() {
        unmockkStatic(FirebaseAuth::class)
        AppDependencies.setNotificationRepositoryForTesting(null)
    }

    @Test
    fun markAsRead_callsRepository() {
        val viewModel = NotificationViewModel(mockApplication)

        viewModel.markAsRead("noti-456")

        verify { mockRepository.markAsRead("noti-456", any(), any()) }
    }

    @Test
    fun markAllAsRead_callsRepository() {
        val viewModel = NotificationViewModel(mockApplication)

        viewModel.markAllAsRead()

        verify { mockRepository.markAllAsRead("user-123", any(), any()) }
    }
}
