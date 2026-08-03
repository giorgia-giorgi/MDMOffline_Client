package com.dbg.mdm_offline_client.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dbg.mdm_offline_client.i18n.Strings
import com.dbg.mdm_offline_client.ui.theme.CardCorner
import com.dbg.mdm_offline_client.ui.theme.FluentAccent
import com.dbg.mdm_offline_client.ui.theme.FluentCard
import com.dbg.mdm_offline_client.ui.theme.FluentLayerDefault
import com.dbg.mdm_offline_client.ui.theme.FluentStroke
import com.dbg.mdm_offline_client.ui.theme.FluentText
import com.dbg.mdm_offline_client.ui.theme.FluentTextSecondary

@Composable
fun HelpScreen(
    strings: Strings,
    onShowTutorial: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FluentLayerDefault)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
    ) {
        Text(
            text = strings.helpTitle,
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(Modifier.height(12.dp))
        PrivacyBadge(strings.privacyBadge)
        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CardCorner)
                .background(FluentCard)
                //.border(1.dp, FluentStroke, CardCorner)
                .padding(20.dp),
        ) {
            Text(
                text = strings.helpBody,
                style = MaterialTheme.typography.bodyLarge,
                color = FluentText,
                fontSize = 14.sp,
            )
            Spacer(Modifier.height(20.dp))
            PrimaryButton(
                text = strings.showTutorialAgain,
                onClick = onShowTutorial,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(10.dp))
            SecondaryButton(
                text = strings.back,
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
