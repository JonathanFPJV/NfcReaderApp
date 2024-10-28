package com.example.nfcreaderapp

import android.app.Activity
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.NfcA
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import android.content.Intent
import android.provider.Settings

class NFCManager(private val activity: Activity) {
    private var nfcAdapter: NfcAdapter? = null

    // Nueva propiedad para almacenar el estado de NFC
    private val _isNFCEnabled = MutableStateFlow(isNFCEnabled())
    val isNFCEnabled: StateFlow<Boolean> = _isNFCEnabled

    private val _nfcId = MutableStateFlow<String?>(null)
    val nfcId: StateFlow<String?> = _nfcId

    init {
        nfcAdapter = NfcAdapter.getDefaultAdapter(activity)
    }

    fun isNFCEnabled(): Boolean {
        return nfcAdapter?.isEnabled == true
    }

    fun checkNFCStatus() {
        // Actualiza el valor de _isNFCEnabled cada vez que se verifique el estado
        _isNFCEnabled.value = isNFCEnabled()
    }

    fun goToNFCSettings() {
        val intent = Intent(Settings.ACTION_NFC_SETTINGS)
        activity.startActivity(intent)
    }

    fun enableReaderMode() {
        nfcAdapter?.enableReaderMode(activity,
            { tag: Tag? ->
                tag?.let {
                    val idBytes = it.id
                    val id = bytesToHexString(idBytes)
                    _nfcId.value = id
                }
            },
            NfcAdapter.FLAG_READER_NFC_A or
                    NfcAdapter.FLAG_READER_NFC_B or
                    NfcAdapter.FLAG_READER_NFC_F or
                    NfcAdapter.FLAG_READER_NFC_V,
            null)
    }

    fun disableReaderMode() {
        nfcAdapter?.disableReaderMode(activity)
    }

    fun clearNFCId() {
        _nfcId.value = null // Limpia el ID para permitir una nueva lectura
    }

    private fun bytesToHexString(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (b in bytes) {
            sb.append(String.format("%02x", b))
        }
        return sb.toString()
    }
}


