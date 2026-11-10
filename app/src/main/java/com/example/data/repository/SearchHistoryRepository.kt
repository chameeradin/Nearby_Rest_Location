package com.example.data.repository

import com.example.data.local.AppDao
import com.example.data.local.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class SearchHistoryRepository(private val appDao: AppDao) {
    fun getRecentSearches(): Flow<List<SearchHistoryEntity>> = appDao.getRecentSearches()

    suspend fun addSearch(query: String, town: String) {
        if (query.isBlank()) return
        val entity = SearchHistoryEntity(
            id = UUID.randomUUID().toString(),
            query = query.trim(),
            town = town.trim()
        )
        appDao.insertSearch(entity)
    }

    suspend fun clearHistory() {
        appDao.clearSearchHistory()
    }
}
