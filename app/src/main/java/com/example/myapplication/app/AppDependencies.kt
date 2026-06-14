package com.example.myapplication.app

import android.content.Context
import com.example.myapplication.data.firebase.CommunityFirestoreDataSource
import com.example.myapplication.data.firebase.CommunityStorageDataSource
import com.example.myapplication.data.firebase.NotificationFirestoreDataSource
import com.example.myapplication.data.repository.CommunityRepositoryImpl
import com.example.myapplication.data.repository.FakeFanZoneRepository
import com.example.myapplication.data.repository.NotificationRepositoryImpl
import com.example.myapplication.domain.repository.CommunityRepository
import com.example.myapplication.domain.repository.FanZoneRepository
import com.example.myapplication.domain.repository.NotificationRepository

object AppDependencies {
    private const val CLOUDINARY_CLOUD_NAME = "dpup3u5ce"
    private const val CLOUDINARY_UPLOAD_PRESET = "fanzone_unsigned"

    val fanZoneRepository: FanZoneRepository = FakeFanZoneRepository

    private val notificationFirestoreDataSource by lazy { NotificationFirestoreDataSource() }
    private val communityFirestoreDataSource by lazy { CommunityFirestoreDataSource() }

    @Volatile private var communityRepositoryInstance: CommunityRepository? = null
    @Volatile private var notificationRepositoryInstance: NotificationRepository? = null

    fun setCommunityRepositoryForTesting(repository: CommunityRepository?) {
        communityRepositoryInstance = repository
    }

    fun setNotificationRepositoryForTesting(repository: NotificationRepository?) {
        notificationRepositoryInstance = repository
    }


    fun communityStorageDataSource(context: Context): CommunityStorageDataSource {
        return CommunityStorageDataSource(
            context = context.applicationContext,
            cloudName = CLOUDINARY_CLOUD_NAME,
            uploadPreset = CLOUDINARY_UPLOAD_PRESET
        )
    }

    fun communityRepository(context: Context): CommunityRepository {
        return communityRepositoryInstance ?: synchronized(this) {
            communityRepositoryInstance ?: CommunityRepositoryImpl(
                firestoreDataSource = communityFirestoreDataSource,
                storageDataSource = communityStorageDataSource(context)
            ).also { communityRepositoryInstance = it }
        }
    }

    fun notificationRepository(context: Context): NotificationRepository {
        return notificationRepositoryInstance ?: synchronized(this) {
            notificationRepositoryInstance ?: NotificationRepositoryImpl(
                firestoreDataSource = notificationFirestoreDataSource
            ).also { notificationRepositoryInstance = it }
        }
    }
}
