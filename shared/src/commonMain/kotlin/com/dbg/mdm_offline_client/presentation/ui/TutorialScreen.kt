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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dbg.mdm_offline_client.presentation.i18n.Strings
import com.dbg.mdm_offline_client.presentation.ui.theme.CardCorner
import com.dbg.mdm_offline_client.presentation.ui.theme.ControlCorner
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentAccent
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentCard
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentLayerDefault
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentNavSelected
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentStroke
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentText
import com.dbg.mdm_offline_client.presentation.ui.theme.FluentTextSecondary

@Composable
fun TutorialScreen(
    strings: Strings,
    onFinished: () -> Unit,
) {
    var step by remember { mutableStateOf(0) }
    val steps = listOf(
        strings.tutorialStep1Title to strings.tutorialStep1Body,
        strings.tutorialStep2Title to strings.tutorialStep2Body,
        strings.tutorialStep3Title to strings.tutorialStep3Body,
        strings.tutorialStep4Title to strings.tutorialStep4Body,
    )
    val isLast = step == steps.lastIndex

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FluentLayerDefault)
            .verticalScroll(rememberScrollState())
            .padding(30.dp),
    ) {
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
        Spacer(Modifier.height(12.dp))
        PrivacyBadge(strings.privacyBadge)
        Spacer(Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CardCorner)
                .background(FluentLayerDefault)
                //.border(1.dp, FluentStroke, CardCorner)
                .padding(20.dp),
        ) {
            Text(
                text = strings.tutorialTitle,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = strings.tutorialWelcome,
                style = MaterialTheme.typography.bodyLarge,
                color = FluentText,
            )
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = FluentText)
            Spacer(Modifier.height(16.dp))

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
            Text(
                text = steps[step].second,
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(Modifier.height(24.dp))
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
        }

       // Spacer(Modifier.height(12.dp))
       // steps.forEachIndexed { index, (title, _) ->
       //     val active = index == step
        //    Text(
        //        text = "${index + 1}. $title",
        //        style = MaterialTheme.typography.bodyMedium,
        //color = if (active) FluentAccent else FluentTextSecondary,
        //        fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
        //        modifier = Modifier
        //            .fillMaxWidth()
        //            .clip(ControlCorner)
        //            .background( FluentCard)
        //            .padding(horizontal = 10.dp, vertical = 8.dp),
        //)
        //}
    }
}
