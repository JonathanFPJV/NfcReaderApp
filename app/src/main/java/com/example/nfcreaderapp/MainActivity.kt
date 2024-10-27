package com.example.nfcreaderapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    private lateinit var nfcManager: NFCManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcManager = NFCManager(this)

        setContent {
            NFCReaderApp(nfcManager)
        }
    }

    override fun onResume() {
        super.onResume()
        nfcManager.enableReaderMode()
    }

    override fun onPause() {
        super.onPause()
        nfcManager.disableReaderMode()
    }
}

@Composable
fun NFCReaderApp(nfcManager: NFCManager) {
    val isNFCEnabled = remember { mutableStateOf(nfcManager.isNFCEnabled()) }
    val isReaderActive = remember { mutableStateOf(false) }

    Surface(color = MaterialTheme.colorScheme.background) {
        NFCReaderScreen(
            nfcManager = nfcManager,
            isNFCEnabled = isNFCEnabled.value,
            isReaderActive = isReaderActive.value,
            onActivateReader = {
                isReaderActive.value = true
                nfcManager.enableReaderMode()
            },
            onDeactivateReader = {
                isReaderActive.value = false
                nfcManager.disableReaderMode()
            }
        )
    }
}


