package com.poc.encryption

import com.poc.encryption.combo.GCMCipher
import com.poc.encryption.lib.AesGcmJce
import java.security.SecureRandom

object CryptoUtil {

    private val aesGcmJce: AesGcmJce

    init {
        val keySize = 32
        val key = ByteArray(keySize)
        SecureRandom().nextBytes(key)
        aesGcmJce = AesGcmJce(key)
    }

    fun encrypt(value: String): String = GCMCipher.encrypt(value)

    fun decrypt(value: String): String = GCMCipher.decrypt(value)

    fun encryptInsecure(value: String): String {
        val valueByteArray = value.encodeBase64()
        val encrypted = aesGcmJce.encrypt(valueByteArray)
        return encrypted.encodeToStringBase64()
    }

    fun decryptInsecure(value: String): String {
        val decodedValue = value.decodeBase64()
        val decrypted = aesGcmJce.decrypt(decodedValue)
        return decrypted.decodeToStringBase64()
    }
}

