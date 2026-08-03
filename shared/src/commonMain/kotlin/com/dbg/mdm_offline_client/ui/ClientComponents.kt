package com.dbg.mdm_offline_client.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dbg.mdm_offline_client.ui.theme.ControlCorner
import com.dbg.mdm_offline_client.ui.theme.FluentAccent
import com.dbg.mdm_offline_client.ui.theme.FluentBadgeBg
import com.dbg.mdm_offline_client.ui.theme.FluentBadgeText
import com.dbg.mdm_offline_client.ui.theme.FluentCard
import com.dbg.mdm_offline_client.ui.theme.FluentLayerDefault
import com.dbg.mdm_offline_client.ui.theme.FluentOnAccent
import com.dbg.mdm_offline_client.ui.theme.FluentStroke
import com.dbg.mdm_offline_client.ui.theme.FluentText
import com.dbg.mdm_offline_client.ui.theme.FluentTextSecondary

@Composable
fun PrivacyBadge(text: String) {
    Text(
        text = text,
        color = FluentBadgeText,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(FluentBadgeBg)
            .padding(horizontal = 12.dp, vertical = 5.dp),
    )
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .defaultMinSize(minWidth = 120.dp)
            .height(48.dp),
        shape = ControlCorner,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = FluentAccent,
            contentColor = FluentOnAccent,
            disabledContainerColor = FluentStroke,
            disabledContentColor = FluentTextSecondary,
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun LittlePrimaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = ControlCorner,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = FluentAccent,
            contentColor = FluentOnAccent,
            disabledContainerColor = FluentStroke,
            disabledContentColor = FluentTextSecondary,
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .defaultMinSize(minWidth = 120.dp)
            .height(48.dp),
        shape = ControlCorner,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = FluentCard,
            contentColor = FluentText,
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, FluentStroke),
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun AccentTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = ControlCorner,
        contentPadding = PaddingValues(horizontal = 8.dp),
        colors = ButtonDefaults.textButtonColors(containerColor = FluentLayerDefault),
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}
