package com.example.podwave.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.example.podwave.R
import com.example.podwave.R.string.parse_error
import com.example.podwave.R.string.parse_null
import com.example.podwave.data.RssFetcher
import com.example.podwave.util.RssParser
import com.example.podwave.util.SharedPreferences
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    private lateinit var urlInput: TextInputEditText
    private lateinit var openUrl: MaterialButton
    private lateinit var clearUrls: TextView
    private lateinit var urlsListHistory: ListView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var urlsListHistoryAdapter: ArrayAdapter<String>
    private lateinit var loader: LottieAnimationView


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
                runOnUiThread {
                    showLoader()
                }
                saveUrl(url)
                fetcherRss(url)
            }
        }

        clearUrls.setOnClickListener {
            sharedPreferences.clearUrls()
            urlsListHistory.visibility = ListView.GONE
            clearUrls.visibility = MaterialButton.GONE
        }

        urlsListHistory.setOnItemClickListener { _, _, position, _ ->
            val selectedUrl = urlsListHistoryAdapter.getItem(position)!!.trim()
            runOnUiThread {
                showLoader()
            }
            urlInput.setText(selectedUrl)
            fetcherRss(selectedUrl)
        }
    }

    //▶️▶️
    private fun initVariables() {
        urlInput = findViewById(R.id.urlEditText_layout)
        clearUrls = findViewById(R.id.clearHistory_layout)
        openUrl = findViewById(R.id.urlButton_layout)
        urlsListHistory = findViewById(R.id.urlsListHistory_layout)
        loader = findViewById(R.id.loader)

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
            clearUrls.visibility = MaterialButton.GONE
        } else {
            urlsListHistory.visibility = ListView.VISIBLE
            clearUrls.visibility = MaterialButton.VISIBLE

        }
        adjustListViewHeight()
    }

    //💾💾
    private fun saveUrl(url: String) {
        sharedPreferences.saveUrl(url)
        getUrls()
        adjustListViewHeight()
    }

    //🛜🛜
    private fun fetcherRss(url: String) {
        val rssFetcher = RssFetcher(this)

        rssFetcher.fetchRss(url) { rssContent ->
            if (rssContent != null) {
                val rssParser = RssParser()
                val rssFeed = rssParser.parseRss(rssContent)
                if (rssFeed != null) {
                    //➡️➡️
                    val intent = Intent(this, PodcastActivity::class.java)
                    intent.putExtra("PODCAST", rssFeed)
                    startActivity(intent)
                    Animatoo.animateSlideLeft(this)
                } else {
                    runOnUiThread {
                        Toast.makeText(this, parse_error, Toast.LENGTH_SHORT).show()
                    }
                }
                runOnUiThread {
                    hideLoader()
                }
            } else {
                runOnUiThread {
                    hideLoader()
                    Toast.makeText(this, parse_null, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun adjustListViewHeight() {
        val listAdapter = urlsListHistory.adapter ?: return

        var totalHeight = 0
        for (i in 0 until listAdapter.count) {
            val listItem = listAdapter.getView(i, null, urlsListHistory)
            listItem.measure(0, 0)
            totalHeight += listItem.measuredHeight
        }

        val params = urlsListHistory.layoutParams
        params.height = totalHeight + (urlsListHistory.dividerHeight * (listAdapter.count - 1))
        urlsListHistory.layoutParams = params
        urlsListHistory.requestLayout()
    }

    fun showLoader() {
        loader.visibility = View.VISIBLE
        loader.playAnimation()
    }

    fun hideLoader() {
        loader.cancelAnimation()
        loader.visibility = View.GONE
    }
}
