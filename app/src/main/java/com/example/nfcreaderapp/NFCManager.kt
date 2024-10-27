package com.example.nfcreaderapp

import android.app.Activity
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.NfcA
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NFCManager(private val activity: Activity) {
    private var nfcAdapter: NfcAdapter? = null
    private val _nfcId = MutableStateFlow<String?>(null)
    val nfcId: StateFlow<String?> = _nfcId

    init {
        nfcAdapter = NfcAdapter.getDefaultAdapter(activity)
    }

    fun isNFCEnabled(): Boolean {
        return nfcAdapter?.isEnabled == true
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

    private fun bytesToHexString(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (b in bytes) {
            sb.append(String.format("%02x", b))
        }
        return sb.toString()
    }
}