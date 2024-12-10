package com.example.podwave.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.bumptech.glide.Glide
import com.example.podwave.R
import com.example.podwave.data.RssFetcher
import com.example.podwave.data.model.Podcast
import com.example.podwave.util.RssParser
import com.example.podwave.util.SharedPreferences
import com.example.podwave.util.UIUtil
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var urlInput: TextInputEditText
    private lateinit var openUrl: MaterialButton
    private lateinit var clearUrls: TextView
    private lateinit var verifyUrl: TextView
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
            val url = urlInput.text.toString().trim()

            if (url.isEmpty()) {
                verifyUrl.visibility = MaterialButton.VISIBLE
                verifyUrl.text = getString(R.string.empty_url_text)
            } else if (!isValidUrl(url)) {
                verifyUrl.visibility = MaterialButton.VISIBLE
                verifyUrl.text = getString(R.string.invalid_url_text)
            } else {
                verifyUrl.visibility = MaterialButton.INVISIBLE
                runOnUiThread {
                    showLoader()
                }
                rectifyUrl(url)
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
        verifyUrl = findViewById(R.id.verify_url)
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

            if (isCacheValid(this)) {
                loadRssFromCache(this)?.let { cachedData ->
                    val rssParser = RssParser()
                    val rssFeed = rssContent?.let { rssParser.parseRss(it) }

                    runOnUiThread {
                        //➡️➡️
                        rssContent?.let { openPodcastActivity(rssFeed, url, it) }
                        hideLoader()
                    }
                    return@fetchRss
                }
            }

            if (rssContent != null) {
                val rssParser = RssParser()
                val rssFeed = rssParser.parseRss(rssContent)

                saveRssToCache(this, rssContent)

                runOnUiThread {
                    //➡️➡️
                    openPodcastActivity(rssFeed, url, rssContent)
                    hideLoader()
                }
            } else {
                runOnUiThread {
                    hideLoader()
                    runOnUiThread {
                        if (rssContent != null) {
                            UIUtil.showDialog(
                                context = this,
                                title = getString(R.string.dialog_tittle_fetcher),
                                message = rssContent,
                                titleColor = String.format(
                                    "#%06X",
                                    0xFFFFFF and ContextCompat.getColor(this, R.color.red)
                                ),
                                okButtonText = "",
                                closeButtonText = getString(R.string.dialog_button_1_fetcher),
                                showOkButton = false,
                                showCloseButton = true,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun openPodcastActivity(rssFeed: Podcast?, url: String, rssContent: String) {
        if (rssFeed != null) {
            saveUrl(url)
        } else {
            runOnUiThread {
                UIUtil.showDialog(
                    context = this,
                    title = getString(R.string.dialog_tittle_fetcher),
                    message = rssContent,
                    titleColor = String.format(
                        "#%06X",
                        0xFFFFFF and ContextCompat.getColor(this, R.color.red)
                    ),
                    okButtonText = "",
                    closeButtonText = getString(R.string.dialog_button_1_fetcher),
                    showOkButton = false,
                    showCloseButton = true,
                )
            }
        }

        val intent = Intent(this, PodcastActivity::class.java)
        intent.putExtra("PODCAST", rssFeed)
        startActivity(intent)
        Animatoo.animateSlideLeft(this)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                UIUtil.showDialog(
                    context = this,
                    title = getString(R.string.dialog_tittle_cache),
                    message = getString(R.string.dialog_description_chace),
                    titleColor = String.format(
                        "#%06X",
                        0xFFFFFF and ContextCompat.getColor(this, R.color.red)
                    ),
                    okButtonText = getString(R.string.dialog_button_1_chace),
                    closeButtonText = getString(R.string.dialog_button_2_chace),
                    showOkButton = true,
                    showCloseButton = true,
                    onOkClick = {
                        runOnUiThread {
                            clearRssCache(this)
                            Thread {
                                Glide.get(this).clearDiskCache()
                            }.start()
                            Glide.get(this).clearMemory()
                        }
                    },
                )
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    //💾💾
    fun loadRssFromCache(context: Context, fileName: String = "rss_cache.xml"): String? {
        val file = File(context.cacheDir, fileName)
        return if (file.exists()) file.readText() else null
    }//💾💾

    fun isCacheValid(
        context: Context,
        fileName: String = "rss_cache.xml",
        maxAgeMillis: Long = 24 * 60 * 60 * 1000
    ): Boolean {
        val file = File(context.cacheDir, fileName)
        return file.exists() && (System.currentTimeMillis() - file.lastModified() < maxAgeMillis)
    }

    //💾💾
    fun saveRssToCache(context: Context, rssContent: String, fileName: String = "rss_cache.xml") {
        val file = File(context.cacheDir, fileName)
        file.writeText(rssContent)
    }

    //🗑️🗑️
    fun clearRssCache(context: Context, fileName: String = "rss_cache.xml"): Boolean {
        val file = File(context.cacheDir, fileName)
        return file.delete()
    }
}
