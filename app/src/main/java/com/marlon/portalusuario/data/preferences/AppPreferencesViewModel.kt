package com.marlon.portalusuario.data.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marlon.portalusuario.domain.model.AppSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppPreferencesViewModel @Inject constructor(
    private val appPreferencesManager: AppPreferencesManager
) : ViewModel() {
    val state = appPreferencesManager.preferences().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = AppSettings()
    )

    /**
     * Handles events related to application preferences.
     *
     * This function processes different types of `AppPreferencesEvent` and updates
     * the corresponding preferences managed by `appPreferencesManager`. Each event
     * represents a change or an action related to a specific preference.
     *
     * @param event The `AppPreferencesEvent` to be processed. This event determines
     *              which preference needs to be updated and the new value to be set.
     *
     * Events handled:
     * - `AppPreferencesEvent.OnUpdateIsShowingTrafficBubble`: Updates the preference
     *   that determines whether the traffic bubble should be displayed.
     * - `AppPreferencesEvent.OnUpdateModeNight`: Updates the preference that controls
     *   the night mode setting of the application.
     * - `AppPreferencesEvent.OnUpdateSkippedLogin`: Indicates a login skip event, does nothing for now.
     * - `AppPreferencesEvent.OnUpdateIsIntroOpened`: Updates the preference that indicates
     *   whether the intro/tutorial has been opened.
     * - `AppPreferencesEvent.OnSwitchingAccountBalanceOnTrafficBubbleVisibility`: Updates
     *   the preference that controls whether the account balance is shown on the traffic bubble.
     * - `AppPreferencesEvent.OnSwitchingDataBalanceOnTrafficBubbleVisibility`: Updates
     *   the preference that controls whether the data balance is shown on the traffic bubble.
     *
     * The function uses `viewModelScope` to launch a coroutine, ensuring that the
     * preference updates are performed asynchronously in a background thread.
     *
     * Note: The `AppPreferencesEvent.OnUpdateSkippedLogin` event currently does not
     * perform any action (empty block `{}`). It is kept for future implementation.
     */
    fun onEvent(event: AppPreferencesEvent) {
        viewModelScope.launch {
            when (event) {
                is AppPreferencesEvent.OnUpdateIsShowingTrafficBubble ->
                    appPreferencesManager.updateIsShowingTrafficBubble(event.value)
                is AppPreferencesEvent.OnUpdateModeNight ->
                    appPreferencesManager.updateModeNight(event.value)
                is AppPreferencesEvent.OnUpdateSkippedLogin -> {}
                is AppPreferencesEvent.OnUpdateIsIntroOpened ->
                    appPreferencesManager.updateIsIntroOpened(event.value)
                is AppPreferencesEvent.OnSwitchingAccountBalanceOnTrafficBubbleVisibility ->
                    appPreferencesManager.updateIsShowingAccountBalanceOnTrafficBubble(event.value)
                is AppPreferencesEvent.OnSwitchingDataBalanceOnTrafficBubbleVisibility ->
                    appPreferencesManager.updateIsShowingDataBalanceOnTrafficBubble(event.value)
            }
        }
    }
}
