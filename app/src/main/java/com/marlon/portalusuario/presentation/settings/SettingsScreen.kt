package com.marlon.portalusuario.presentation.settings

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.marlon.portalusuario.R
import com.marlon.portalusuario.data.preferences.AppPreferencesEvent
import com.marlon.portalusuario.data.preferences.AppPreferencesViewModel
import com.marlon.portalusuario.domain.model.ModeNight
import com.marlon.portalusuario.ui.theme.PortalUsuarioTheme

private const val TAG = "SettingsScreen"

@Composable
fun SettingsScreen(viewModel: AppPreferencesViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var isShowingModeNightDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        SettingsItem(stringResource(R.string.apariecia_settings), getModeNightName(context, state.modeNight)) {
            isShowingModeNightDialog = true
        }
        SettingsItemCheckable(stringResource(R.string.show_floating_bubble), state.isShowingTrafficBubble) {
            viewModel.onEvent(AppPreferencesEvent.OnUpdateIsShowingTrafficBubble(!state.isShowingTrafficBubble))
        }
    }

    if (isShowingModeNightDialog) {
        ModeNightDialog(
            state.modeNight,
            {
                viewModel.onEvent(AppPreferencesEvent.OnUpdateModeNight(it))
            }) {
            isShowingModeNightDialog = false
        }
    }
}

@Composable
fun SettingsItemCheckable(title: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .height(50.dp)
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Text(text = title)
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun SettingsItem(title: String, currentValue: String, onValueChange: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .height(50.dp)
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clickable { onValueChange() }
    ) {
        Text(text = title, modifier = Modifier.padding(horizontal = 8.dp))
        Text(text = currentValue, color = TextFieldDefaults.colors().disabledTextColor)
    }
}

@Composable
fun ModeNightDialog(
    currentModeNight: ModeNight,
    onModeNightChange: (ModeNight) -> Unit,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current

    val options = ModeNight.entries.map { Pair(getModeNightName(context, it), it) }
    val selectedOption = options.first { it.second == currentModeNight }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                options.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .clickable {
                                Log.i(TAG, "ModeNightDialog: changing modeNight to ${option.first}")
                                onModeNightChange(option.second)
                            }) {
                        Text(
                            text = option.first,
                            textAlign = TextAlign.Start
                        )
                        RadioButton(
                            selected = option == selectedOption,
                            onClick = {
                                Log.i(TAG, "ModeNightDialog: changing modeNight to ${option.first}")
                                onModeNightChange(option.second)
                            })
                    }
                }
            }
        }
    }
}

fun getModeNightName(context: Context, modeNight: ModeNight) = when (modeNight) {
        ModeNight.YES -> context.getString(R.string.ui_mode_dark)
        ModeNight.NO -> context.getString(R.string.ui_mode_light)
        ModeNight.FOLLOW_SYSTEM -> context.getString(R.string.ui_mode_system)
    }

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ModeNightDialogPreview() {
    PortalUsuarioTheme {
        Surface(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) { ModeNightDialog(ModeNight.YES, {}) {} }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SettingsItemCheckablePreview() {
    PortalUsuarioTheme {
        Surface(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            SettingsItemCheckable(stringResource(R.string.show_floating_bubble), false) {}
        }
    }
}
