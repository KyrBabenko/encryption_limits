package com.poc.encryption.combo

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties.BLOCK_MODE_GCM
import android.security.keystore.KeyProperties.ENCRYPTION_PADDING_NONE
import android.security.keystore.KeyProperties.KEY_ALGORITHM_AES
import android.security.keystore.KeyProperties.PURPOSE_DECRYPT
import android.security.keystore.KeyProperties.PURPOSE_ENCRYPT
import android.util.Base64
import java.nio.ByteBuffer
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec


object GCMCipher {

    private const val KEY_ALIAS = "myOwnKeyAlias"
    private const val TRANSFORMATION = "$KEY_ALGORITHM_AES/$BLOCK_MODE_GCM/$ENCRYPTION_PADDING_NONE"

    private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
    private const val IV_SIZE = 12
    private const val AUTH_TAG_SIZE = 128
    private const val KEY_SIZE = 256
    private val cipher = Cipher.getInstance(TRANSFORMATION)

    fun encrypt(plaintext: String): String {
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        val stringBytes = plaintext.toByteArray()
        val ciphertext = cipher.doFinal(stringBytes)

        val iv: ByteArray = cipher.iv
        val integerBytes = Integer.SIZE / java.lang.Byte.SIZE
        val buffer = ByteBuffer.allocate(IV_SIZE + integerBytes + ciphertext.size)
        buffer.put(iv)
        buffer.putInt(ciphertext.size)
        buffer.put(ciphertext)

        return Base64.encodeToString(buffer.array(), Base64.DEFAULT)
    }

    fun decrypt(ciphertextWithIvString: String): String {
        val ciphertextWithIv = Base64.decode(ciphertextWithIvString.toByteArray(), Base64.DEFAULT)
        val buffer = ByteBuffer.wrap(ciphertextWithIv)

        val iv = ByteArray(IV_SIZE).apply { buffer.get(this) }
        val ciphertextSize = buffer.int
        val ciphertext = ByteArray(ciphertextSize).apply { buffer.get(this) }

        cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), GCMParameterSpec(AUTH_TAG_SIZE, iv))
        val plaintext = cipher.doFinal(ciphertext)

        return String(plaintext, Charsets.UTF_8)
    }

    private fun getOrCreateKey(): SecretKey {
        val keystore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply {
            load(null)
        }

        keystore.getKey(KEY_ALIAS, null)?.let { key ->
            return key as SecretKey
        }

        val keySpec = KeyGenParameterSpec.Builder(KEY_ALIAS, PURPOSE_ENCRYPT or PURPOSE_DECRYPT)
            .setBlockModes(BLOCK_MODE_GCM)
            .setEncryptionPaddings(ENCRYPTION_PADDING_NONE)
            .setKeySize(KEY_SIZE)
            .build()

        val keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM_AES, KEYSTORE_PROVIDER).apply {
            init(keySpec)
        }

        return keyGenerator.generateKey()
    }
}
