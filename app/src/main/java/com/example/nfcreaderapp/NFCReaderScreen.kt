package com.example.nfcreaderapp

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun NFCReaderScreen(activity: Activity) {
    // Inicializa NFCManager y los estados necesarios
    val nfcManager = remember { NFCManager(activity) }
    val isNFCEnabled = remember { mutableStateOf(nfcManager.isNFCEnabled()) }
    val isReaderActive = remember { mutableStateOf(false) }

    // Pasa los parámetros y funciones de activación/desactivación de lector al segundo NFCReaderScreen
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
        },
        onClearNFCId = {
            nfcManager.clearNFCId()
        }
    )
}

@Composable
fun NFCReaderScreen(
    nfcManager: NFCManager,
    isNFCEnabled: Boolean,
    isReaderActive: Boolean,
    onActivateReader: () -> Unit,
    onDeactivateReader: () -> Unit,
    onClearNFCId: () -> Unit // Nueva función para limpiar el ID después de la lectura
) {
    val nfcId by nfcManager.nfcId.collectAsState()

    // Monitorear cambios en el estado del NFC cada vez que la pantalla sea visible
    LaunchedEffect(Unit) {
        nfcManager.checkNFCStatus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!isNFCEnabled) {
            Text(
                text = "NFC no está habilitado",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Botón para abrir la configuración de NFC
            Button(
                onClick = { nfcManager.goToNFCSettings() },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Activar NFC")
            }
        } else {
            Text(
                text = "Presiona el botón para habilitar el lector NFC",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Button(
                onClick = {
                    if (isReaderActive) onDeactivateReader() else onActivateReader()
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = if (isReaderActive) "Desactivar Lector" else "Activar Lector")
            }

            if (isReaderActive) {
                Text(
                    text = "Acerca una tarjeta NFC",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 16.dp)
                )

                if (nfcId != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "ID de la tarjeta:", fontSize = 16.sp)
                            Text(
                                text = nfcId ?: "",
                                fontSize = 24.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Button(
                        onClick = { onClearNFCId() }, // Limpia el ID para permitir una nueva lectura
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = "Realizar nueva lectura")
                    }
                }
            }
        }
    }
}

