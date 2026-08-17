package com.dbg.mdm_offline_client.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dbg.mdm_offline_client.domain.isJvmPlatform
import com.dbg.mdm_offline_client.presentation.i18n.Strings
import com.dbg.mdm_offline_client.presentation.ui.theme.CardCorner
import com.dbg.mdm_offline_client.presentation.ui.theme.ControlCorner
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentAccent
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentCard
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentLayerDefault
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentNavSelected
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentSmoke
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentStroke
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentText
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentTextSecondary

@Composable
fun TutorialScreen(
    strings: Strings,
    onFinished: () -> Unit,
    showHeader: Boolean = true,
) {
    if (isJvmPlatform() && showHeader) {
        DesktopTutorialScreen(strings = strings, onFinished = onFinished)
        return
    }

    var step by remember { mutableIntStateOf(0) }
    val steps = listOf(
        strings.tutorialTitle to strings.tutorialWelcome,
        strings.tutorialStep1Title to strings.tutorialStep1Body,
        strings.tutorialStep2Title to strings.tutorialStep2Body,
        strings.tutorialStep3Title to strings.tutorialStep3Body,
        strings.tutorialStep4Title to strings.tutorialStep4Body,
    )
    val isLast = step == steps.lastIndex

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (showHeader) FluentLayerDefault else FluentCard)
            .padding(if (showHeader) 20.dp else 24.dp),
    ) {
        if (showHeader) {
            Text(
                text = strings.appTitle,
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = strings.appSubtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = FluentText,
            )
            Spacer(Modifier.height(20.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(CardCorner)
                .background(FluentLayerDefault)
                .padding(20.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                steps.indices.forEach { index ->
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .weight(3f)
                            .clip(ControlCorner)
                            .background(if (index <= step) FluentAccent else FluentStroke),
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = "${step + 1} / ${steps.size}",
                style = MaterialTheme.typography.labelMedium,
                color = FluentAccent,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = steps[step].first,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(Modifier.height(16.dp))
            // Fixed-height body slot so nav buttons stay at a stable Y (higher than screen bottom).
            Text(
                text = steps[step].second,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .verticalScroll(rememberScrollState()),
            )
            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (step > 0) {
                        SecondaryButton(
                            text = strings.back,
                            onClick = { step -= 1 },
                            modifier = Modifier.width(100.dp),
                        )
                    }
                    PrimaryButton(
                        text = if (isLast) strings.getStarted else strings.next,
                        onClick = {
                            if (isLast) onFinished() else step += 1
                        },
                        modifier = Modifier.width(160.dp),
                    )
                }
            }
            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
private fun DesktopTutorialScreen(
    strings: Strings,
    onFinished: () -> Unit,
) {
    var step by remember { mutableIntStateOf(0) }
    val steps = listOf(
        strings.tutorialStep1Title to strings.tutorialStep1Body,
        strings.tutorialStep2Title to strings.tutorialStep2Body,
        strings.tutorialStep3Title to strings.tutorialStep3Body,
        strings.tutorialStep4Title to strings.tutorialStep4Body,
    )
    val isLast = step == steps.lastIndex

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(FluentLayerDefault),
    ) {
        Column(
            modifier = Modifier
                .width(272.dp)
                .fillMaxHeight()
                .background(FluentSmoke)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = strings.appTitle,
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = strings.appSubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = FluentTextSecondary,
                )

                Spacer(Modifier.height(28.dp))
                steps.forEachIndexed { index, (title, _) ->
                    val active = index == step
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(ControlCorner)
                            .background(if (active) FluentNavSelected else FluentSmoke)
                            .clickable { step = index }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(16.dp)
                                .background(if (active) FluentAccent else FluentStroke),
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (active) FluentAccent else FluentTextSecondary,
                        )
                    }
                    Spacer(Modifier.height(2.dp))
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxWidth()
                    .clip(CardCorner)
                    .background(FluentCard)
                    .border(1.dp, FluentStroke, CardCorner)
                    .padding(32.dp),
            ) {
                Text(
                    text = strings.tutorialTitle,
                    style = MaterialTheme.typography.headlineMedium,
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = strings.tutorialWelcome,
                    style = MaterialTheme.typography.bodyLarge,
                    color = FluentTextSecondary,
                )
                Spacer(Modifier.height(24.dp))
                HorizontalDivider(color = FluentStroke)
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "${step + 1} / ${steps.size}",
                    style = MaterialTheme.typography.labelMedium,
                    color = FluentAccent,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = steps[step].first,
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = steps[step].second,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(Modifier.height(32.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AccentTextButton(text = strings.skip, onClick = onFinished)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (step > 0) {
                            SecondaryButton(
                                text = strings.back,
                                onClick = { step -= 1 },
                            )
                        }
                        PrimaryButton(
                            text = if (isLast) strings.getStarted else strings.next,
                            onClick = {
                                if (isLast) onFinished() else step += 1
                            },
                        )
                    }
                }
            }
        }
    }
}
