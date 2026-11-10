package com.example.data.repository

import com.example.data.local.AppDao
import com.example.data.local.UserEntity
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class AuthRepository(private val appDao: AppDao) {

    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    suspend fun initialize() {
        val userEntity = appDao.getLatestUser()
        if (userEntity != null) {
            _currentUser.value = UserProfile(
                id = userEntity.id,
                name = userEntity.name,
                email = userEntity.email,
                phone = userEntity.phone,
                isGoogleUser = userEntity.isGoogleUser,
                avatarUrl = userEntity.avatarUrl
            )
        } else {
            // Seed a default signed-in traveler account for smooth onboarding
            val defaultUser = UserEntity(
                id = "user-default-101",
                name = "Kasun Jayasuriya",
                email = "kasun.travel@wavesit.lk",
                phone = "+94 77 123 4567",
                isGoogleUser = true,
                avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=200&q=80"
            )
            appDao.insertUser(defaultUser)
            _currentUser.value = UserProfile(
                id = defaultUser.id,
                name = defaultUser.name,
                email = defaultUser.email,
                phone = defaultUser.phone,
                isGoogleUser = defaultUser.isGoogleUser,
                avatarUrl = defaultUser.avatarUrl
            )
        }
    }

    suspend fun signIn(email: String, password: String):Result<UserProfile> {
        val existing = appDao.getUserByEmail(email)
        return if (existing != null) {
            val profile = UserProfile(
                id = existing.id,
                name = existing.name,
                email = existing.email,
                phone = existing.phone,
                isGoogleUser = existing.isGoogleUser,
                avatarUrl = existing.avatarUrl
            )
            _currentUser.value = profile
            Result.success(profile)
        } else {
            // Create user on first login if not exists for easy testing
            val newUser = UserEntity(
                id = UUID.randomUUID().toString(),
                name = email.substringBefore("@").replaceFirstChar { it.uppercase() },
                email = email,
                phone = "+94 71 000 0000",
                passwordHash = password,
                isGoogleUser = false,
                avatarUrl = ""
            )
            appDao.insertUser(newUser)
            val profile = UserProfile(
                id = newUser.id,
                name = newUser.name,
                email = newUser.email,
                phone = newUser.phone,
                isGoogleUser = false
            )
            _currentUser.value = profile
            Result.success(profile)
        }
    }

    suspend fun signUp(name: String, email: String, password: String, phone: String): Result<UserProfile> {
        val userEntity = UserEntity(
            id = UUID.randomUUID().toString(),
            name = name,
            email = email,
            phone = phone,
            passwordHash = password,
            isGoogleUser = false,
            avatarUrl = ""
        )
        appDao.insertUser(userEntity)
        val profile = UserProfile(
            id = userEntity.id,
            name = userEntity.name,
            email = userEntity.email,
            phone = userEntity.phone,
            isGoogleUser = false
        )
        _currentUser.value = profile
        return Result.success(profile)
    }

    suspend fun signInWithGoogle(googleEmail: String = "google.traveler@gmail.com", googleName: String = "Traveler (Google Account)"): UserProfile {
        val googleUser = UserEntity(
            id = "google-user-" + UUID.randomUUID().toString().take(8),
            name = googleName,
            email = googleEmail,
            phone = "+94 77 987 6543",
            isGoogleUser = true,
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80"
        )
        appDao.insertUser(googleUser)
        val profile = UserProfile(
            id = googleUser.id,
            name = googleUser.name,
            email = googleUser.email,
            phone = googleUser.phone,
            isGoogleUser = true,
            avatarUrl = googleUser.avatarUrl
        )
        _currentUser.value = profile
        return profile
    }

    suspend fun signOut() {
        appDao.clearUsers()
        _currentUser.value = null
    }
}
