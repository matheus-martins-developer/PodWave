package com.example.podwave.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.podwave.R
import com.example.podwave.R.string.parse_error
import com.example.podwave.R.string.parse_null
import com.example.podwave.R.string.putExtra_podcast
import com.example.podwave.data.RssFetcher
import com.example.podwave.util.RssParser
import com.example.podwave.util.SharedPreferences

class MainActivity : AppCompatActivity() {

    private lateinit var urlInput: EditText
    private lateinit var openUrl: Button
    private lateinit var urlsListHistory: ListView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var urlsListHistoryAdapter: ArrayAdapter<String>


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initVariables()
        sharedPreferences = SharedPreferences(this)
        getUrls()
        clicks()
    }

    //☝️☝️
    private fun clicks() {
        openUrl.setOnClickListener {
            val url = rectifyUrl(urlInput.text.toString().trim())

            if (url.isEmpty()) {
                Toast.makeText(this, R.string.empty_url, Toast.LENGTH_SHORT).show()
            } else if (!isValidUrl(url)) {
                Toast.makeText(this, R.string.invalid_url, Toast.LENGTH_SHORT).show()
            } else {
                saveUrl(url)
                fetcherRss(url)
            }
        }

        urlsListHistory.setOnItemClickListener { _, _, position, _ ->
            val selectedUrl = urlsListHistoryAdapter.getItem(position)!!.trim()
            urlInput.setText(selectedUrl)
            fetcherRss(selectedUrl)
        }
    }

    //▶️▶️
    private fun initVariables() {
        urlInput = findViewById(R.id.urlEditText_layout)
        openUrl = findViewById(R.id.urlButton_layout)
        urlsListHistory = findViewById(R.id.urlsListHistory_layout)

    }

    //☑️☑️
    private fun isValidUrl(url: String): Boolean {
        return Patterns.WEB_URL.matcher(url).matches()
    }

    //☑️☑️
    private fun rectifyUrl(url: String): String {
        return if (!url.startsWith("http://") && !url.startsWith("https://")) {
            "http://$url"
        } else {
            url
        }
    }

    //⬅️⬅️
    private fun getUrls() {
        val history = sharedPreferences.getUrls().toList()
        urlsListHistoryAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, history)
        urlsListHistory.adapter = urlsListHistoryAdapter

        if (history.isEmpty()) {
            urlsListHistory.visibility = ListView.GONE
        } else {
            urlsListHistory.visibility = ListView.VISIBLE
        }
    }

    //💾💾
    private fun saveUrl(url: String) {
        sharedPreferences.saveUrl(url)
        getUrls()
    }

    //🛜🛜
    private fun fetcherRss(url: String) {
        val rssFetcher = RssFetcher(this)
        rssFetcher.fetchRss(url) { rssContent ->
            if (rssContent != null) {
                val rssParser = RssParser()
                val rssFeed = rssParser.parseRss(rssContent)
                if (rssFeed != null) {
                    val intent = Intent(this, PodcastActivity::class.java)
                    intent.putExtra("PODCAST", rssFeed)
                    startActivity(intent)
                } else {
                    runOnUiThread {
                        Toast.makeText(this, parse_error, Toast.LENGTH_SHORT).show()
                    }
                }

            } else {
                runOnUiThread {
                    Toast.makeText(this, parse_null, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
