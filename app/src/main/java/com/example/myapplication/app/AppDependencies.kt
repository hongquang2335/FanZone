package com.example.myapplication.app

import android.content.Context
import com.example.myapplication.data.firebase.CommunityFirestoreDataSource
import com.example.myapplication.data.firebase.CommunityStorageDataSource
import com.example.myapplication.data.repository.CommunityRepositoryImpl
import com.example.myapplication.data.repository.FakeFanZoneRepository
import com.example.myapplication.domain.repository.CommunityRepository
import com.example.myapplication.domain.repository.FanZoneRepository

object AppDependencies {
    private const val CLOUDINARY_CLOUD_NAME = "dpup3u5ce"
    private const val CLOUDINARY_UPLOAD_PRESET = "fanzone_unsigned"

    val fanZoneRepository: FanZoneRepository = FakeFanZoneRepository
    private val communityFirestoreDataSource by lazy { CommunityFirestoreDataSource() }
    @Volatile private var communityRepositoryInstance: CommunityRepository? = null

    fun communityRepository(context: Context): CommunityRepository {
        return communityRepositoryInstance ?: synchronized(this) {
            communityRepositoryInstance ?: CommunityRepositoryImpl(
                firestoreDataSource = communityFirestoreDataSource,
                storageDataSource = CommunityStorageDataSource(
                    context = context.applicationContext,
                    cloudName = CLOUDINARY_CLOUD_NAME,
                    uploadPreset = CLOUDINARY_UPLOAD_PRESET
                )
            ).also { communityRepositoryInstance = it }
        }
    }
}
