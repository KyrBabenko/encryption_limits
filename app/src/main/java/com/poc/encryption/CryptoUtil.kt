package com.poc.encryption

import android.content.SharedPreferences
import com.poc.encryption.combo.GCMCipher
import com.poc.encryption.lib.AesGcmJce
import java.security.SecureRandom

class CryptoUtil(
    preferences: SharedPreferences
) {
    companion object {
        private const val VALUE_KEYSET_ALIAS: String = "__encrypted_prefs_value__"
    }

    private val aesGcmJce: AesGcmJce

    init {
        if (preferences.contains(VALUE_KEYSET_ALIAS).not()) {
            val secureRandom = SecureRandom()
            val key = ByteArray(32)
            secureRandom.nextBytes(key)
            val hexKey = key.toHex()
            val encrypted = GCMCipher.encrypt(hexKey)
            preferences.edit().putString(VALUE_KEYSET_ALIAS, encrypted).commit()
        }
        val encryptedKey = preferences.getString(VALUE_KEYSET_ALIAS, "") ?: ""
        if (encryptedKey.isEmpty()) {
            throw IllegalStateException("Key not found")
        }
        val key = GCMCipher.decrypt(encryptedKey).hexToByteArray()
        aesGcmJce = AesGcmJce(key)
    }

    fun encrypt(value: String): String = GCMCipher.encrypt(value)

    fun decrypt(value: String): String = GCMCipher.decrypt(value)

    fun encryptSecure(value: String): String {
        val valueByteArray = value.encodeBase64()
        val encrypted = aesGcmJce.encrypt(valueByteArray)
        return encrypted.encodeToStringBase64()
    }

    fun decryptSecure(value: String): String {
        val decodedValue = value.decodeBase64()
        val decrypted = aesGcmJce.decrypt(decodedValue)
        return decrypted.decodeToStringBase64()
    }
}
