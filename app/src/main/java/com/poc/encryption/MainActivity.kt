package com.poc.encryption

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.poc.encryption.databinding.ActivityMainBinding
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val gson = Gson()
    private lateinit var cryptoUtil: CryptoUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initCryptoUtil()
        initListeners()
    }

    private fun initListeners() {
        binding.encrypt.setOnClickListener {
            val data = loadJsonFromAssets(this)
            val encryptedData = cryptoUtil.encrypt(data)
            binding.encryptedValue.text = encryptedData
        }
        binding.decrypt.setOnClickListener {
            val encryptedData = binding.encryptedValue.text.toString()
            val decryptedData = cryptoUtil.decrypt(encryptedData)
            binding.decryptedValue.text = decryptedData
            val array = gson.fromJson<Map<String, String>>(decryptedData, object:TypeToken<Map<String, String>>() {}.type)
            Toast.makeText(this, "Length: ${array.size}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadJsonFromAssets(context: Context): String {
        return try {
//            val inputStream = context.assets.open("randomized-limit.json")
            val inputStream = context.assets.open("randomized-correct.json")
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            var line: String?

            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }

            bufferedReader.close()
            stringBuilder.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun initCryptoUtil() {
        val preferences = getSharedPreferences("crypto", MODE_PRIVATE)
        cryptoUtil = CryptoUtil(preferences)
    }
}