package com.dbg.mdm_offline_client.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dbg.mdm_offline_client.presentation.i18n.Strings
import com.dbg.mdm_offline_client.presentation.navigation.AppRoute
import com.dbg.mdm_offline_client.presentation.ui.theme.ControlCorner
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentAccent
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentCard
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentNavHover
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentNavSelected
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentSmoke
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentText
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentTextSecondary

@Composable
fun DesktopCommandBar(strings: Strings) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(FluentCard)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = strings.appTitle,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = strings.appSubtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = FluentTextSecondary,
            )
        }
    }
}

@Composable
fun NavigationPane(
    strings: Strings,
    current: AppRoute?,
    onSelect: (AppRoute) -> Unit,
) {
    Column(
        modifier = Modifier
            .width(220.dp)
            .fillMaxHeight()
            .background(FluentSmoke)
            .padding(vertical = 8.dp, horizontal = 8.dp),
    ) {
        Text(
            text = strings.home.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = FluentTextSecondary,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        )
        NavItem(
            label = strings.home,
            selected = current == AppRoute.Home,
            onClick = { onSelect(AppRoute.Home) },
        )
        Spacer(Modifier.weight(1f))
        NavItem(
            label = strings.tutorial,
            selected = current == AppRoute.Tutorial,
            onClick = { onSelect(AppRoute.Tutorial) },
        )
    }
}

@Composable
private fun NavItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    badge: String? = null,
) {
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val background = when {
        selected -> FluentNavSelected
        hovered -> FluentNavHover
        else -> FluentSmoke
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .clip(ControlCorner)
            .background(background)
            .hoverable(interaction)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(16.dp)
                .background(if (selected) FluentAccent else FluentSmoke),
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (selected) FluentAccent else FluentText,
            modifier = Modifier.weight(1f),
        )
        if (badge != null) {
            Text(
                text = badge,
                style = MaterialTheme.typography.labelMedium,
                color = FluentAccent,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
