package com.example.myapplication.app

import com.example.myapplication.data.repository.FakeFanZoneRepository
import com.example.myapplication.domain.repository.FanZoneRepository

object AppDependencies {
    val fanZoneRepository: FanZoneRepository = FakeFanZoneRepository
}
