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