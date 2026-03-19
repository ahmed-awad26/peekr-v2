package com.peekr.ui.settings.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peekr.data.local.dao.AccountDao
import com.peekr.data.local.entities.AccountEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class YoutubeViewModel @Inject constructor(
    private val accountDao: AccountDao
) : ViewModel() {

    // قنوات اليوتيوب المحفوظة في DB
    val channels = accountDao.getAllAccountsByPlatform("youtube")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addChannel(url: String) {
        if (url.isBlank()) return
        viewModelScope.launch {
            accountDao.insertAccount(
                AccountEntity(
                    platformId = "youtube",
                    accountName = url.trim(),
                    isConnected = true,
                    connectedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun removeChannel(account: AccountEntity) {
        viewModelScope.launch {
            accountDao.deleteAccountById(account.id)
        }
    }

    fun updateChannel(account: AccountEntity, newUrl: String) {
        viewModelScope.launch {
            accountDao.insertAccount(account.copy(accountName = newUrl.trim()))
        }
    }
}
