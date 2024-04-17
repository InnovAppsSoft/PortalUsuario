package com.marlon.portalusuario.banner

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.suitetecsa.sdk.promotion.PromotionsCollector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PromotionsViewModel @Inject constructor() : ViewModel() {
    var state = MutableLiveData(PromotionState())
        private set

    fun onEvent(event: PromotionEvent) {
        when (event) {
            PromotionEvent.Reload -> loadPromotions()
        }
    }

    private fun loadPromotions() {
        viewModelScope.launch {
            state.value = PromotionState(isLoading = true)
            state.value = withContext(Dispatchers.IO) {
                runCatching {
                    val promotions = PromotionsCollector.collect()
                    PromotionState(promotions)
                }.getOrNull() ?: PromotionState(onError = true)
            }
        }
    }
}