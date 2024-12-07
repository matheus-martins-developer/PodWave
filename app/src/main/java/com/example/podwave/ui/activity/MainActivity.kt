package com.example.podwave.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.podwave.R

class MainActivity : AppCompatActivity() {

    private lateinit var urlInput: EditText
    private lateinit var openUrl: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initVariables()

        openUrl.setOnClickListener {
            val url = urlInput.text.toString().trim()

            if (url.isEmpty()) {
                Toast.makeText(this, R.string.empty_url, Toast.LENGTH_SHORT).show()
            } else if (!isValidUrl(url)) {
                Toast.makeText(this, R.string.invalid_url, Toast.LENGTH_SHORT).show()
            } else {

            }
        }
    }

    private fun initVariables() {
        urlInput = findViewById(R.id.urlEditText)
        openUrl = findViewById(R.id.urlButton)
    }

    private fun isValidUrl(url: String): Boolean {
        return Patterns.WEB_URL.matcher(url).matches()
    }
}
