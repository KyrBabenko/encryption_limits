package com.poc.encryption

import android.util.Base64

fun String.encodeBase64(): ByteArray {
    return Base64.encode(toByteArray(), Base64.DEFAULT)
}

fun String.decodeBase64(): ByteArray {
    return Base64.decode(toByteArray(), Base64.DEFAULT)
}

fun ByteArray.encodeToStringBase64(): String {
    return Base64.encodeToString(this, Base64.DEFAULT)
}

fun ByteArray.decodeToStringBase64(): String {
    return Base64.decode(this, Base64.DEFAULT).decodeToString()
}

fun ByteArray.toHex(): String {
    return joinToString(separator = "") { "%02x".format(it) }
}

fun String.hexToByteArray(): ByteArray {
    val len = length
    val byteArray = ByteArray(len / 2)
    for (i in 0 until len step 2) {
        byteArray[i / 2] = ((this[i].digitToInt(16) shl 4) + this[i + 1].digitToInt(16)).toByte()
    }
    return byteArray
}