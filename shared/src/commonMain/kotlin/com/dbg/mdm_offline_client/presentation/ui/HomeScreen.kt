package com.dbg.mdm_offline_client.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dbg.mdm_offline_client.domain.model.ConnectionPhase
import com.dbg.mdm_offline_client.presentation.ui.theme.CardCorner
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentAccent
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentCard
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentError
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentInfoBarBg
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentInfoBarStroke
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentLayerDefault
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentStroke
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentSuccess
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentTextSecondary
import com.dbg.mdm_offline_client.presentation.viewmodel.ClientUiState

@Composable
fun HomeScreen(
    state: ClientUiState,
    onToggleAgent: () -> Unit,
) {
    val strings = state.strings
    val agentOn = state.phase != ConnectionPhase.Idle
    val (statusLabel, statusColor) = when (state.phase) {
        ConnectionPhase.Connected -> strings.statusConnected to FluentSuccess
        ConnectionPhase.Discovering -> strings.statusSearching to FluentTextSecondary
        ConnectionPhase.Idle -> strings.statusIdle to FluentTextSecondary
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FluentLayerDefault)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = strings.appTitle,
                    style = MaterialTheme.typography.headlineMedium,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = strings.appSubtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = FluentTextSecondary,
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        PrivacyBadge(strings.privacyBadge)
        Spacer(Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CardCorner)
                .background(FluentCard)
                .border(1.dp, FluentStroke, CardCorner)
                .padding(20.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (state.phase == ConnectionPhase.Discovering) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = FluentAccent,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(statusColor),
                    )
                }
                Spacer(Modifier.size(10.dp))
                Text(
                    text = statusLabel,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(Modifier.height(5.dp))
            if (state.phase == ConnectionPhase.Connected) {
                StatusRow(strings.serverAddress, state.serverBaseUrl ?: strings.dash)
            }
            StatusRow(strings.deviceLabel, state.deviceName)
            StatusRow(strings.platformLabel, state.platform)
            StatusRow(
                strings.lastMessage,
                state.lastMessage ?: state.errorMessage ?: strings.dash,
            )
            state.listedOnServer?.let { listed ->
                Spacer(Modifier.height(8.dp))
                Text(
                    text = if (listed) strings.listedOnServer else strings.notListedOnServer,
                    style = MaterialTheme.typography.bodyMedium,
                    color = FluentLayerDefault
                )
            }
        }

        if (state.errorMessage != null && agentOn) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = state.errorMessage,
                style = MaterialTheme.typography.bodyLarge,
                color = FluentError,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CardCorner)
                    .background(FluentInfoBarBg)
                    .border(1.dp, FluentInfoBarStroke, CardCorner)
                    .padding(14.dp),
            )
        }

        Spacer(Modifier.height(20.dp))
        if (agentOn) {
            SecondaryButton(
                text = strings.agentStop,
                onClick = onToggleAgent,
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            PrimaryButton(
                text = strings.agentStart,
                onClick = onToggleAgent,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun StatusRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = FluentTextSecondary,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
