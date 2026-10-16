package com.example

import android.app.Application
import com.example.data.AppDatabase
import com.example.data.EventRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class DiesApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { EventRepository(database.eventDao(), database.photoDao(), database.memoryDao()) }

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }
}
