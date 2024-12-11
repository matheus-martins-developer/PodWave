package com.example.podwave.ui.activity

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.core.app.NotificationCompat
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

    private lateinit var verifyUrl: TextView
    private lateinit var slogan: TextView
    private lateinit var init: TextView

    private lateinit var clearUrls: MaterialButton
    private lateinit var linear_history: LinearLayout
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
        textCreator()
        notificationPermission()
    }

    //💬💬
    private fun notificationPermission() {
        val notificationPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->

            }

        if (!UIUtil.isNotificationPermissionGranted(this)) {
            UIUtil.requestNotificationPermission(notificationPermissionLauncher)
        }
    }

    //🖌️🖌️
    private fun textCreator() {
        val sloganText = getString(R.string.text_slogan)
        val initText = getString(R.string.text_init)

        val spannableSlogan = SpannableString(sloganText)
        val spannableInit = SpannableString(initText)

        //slogan
        spannableSlogan.setSpan(
            ForegroundColorSpan(getColor(R.color.black_text)),
            0,
            sloganText.length - 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableSlogan.setSpan(
            ForegroundColorSpan(getColor(R.color.red)),
            sloganText.length - 1,
            sloganText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        //init
        spannableInit.setSpan(
            ForegroundColorSpan(getColor(R.color.black_text)),
            0,
            spannableInit.length - 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableInit.setSpan(
            ForegroundColorSpan(getColor(R.color.red)),
            spannableInit.length - 1,
            spannableInit.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        slogan.text = spannableSlogan
        init.text = spannableInit
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
                    buttonsStates(false)
                }
                rectifyUrl(url)
                fetcherRss(url)
            }
        }

        clearUrls.setOnClickListener {
            sharedPreferences.clearUrls()
            linear_history.visibility = ListView.GONE
        }

        urlsListHistory.setOnItemClickListener { _, _, position, _ ->
            val selectedUrl = urlsListHistoryAdapter.getItem(position)!!.trim()
            runOnUiThread {
                showLoader()
            }
            buttonsStates(false)
            urlInput.setText(selectedUrl)
            fetcherRss(selectedUrl)
        }
    }

    //▶️▶️
    private fun initVariables() {
        urlInput = findViewById(R.id.urlEditText_layout)
        clearUrls = findViewById(R.id.clearHistory_layout)
        verifyUrl = findViewById(R.id.verify_url_layout)
        slogan = findViewById(R.id.slogan_text_layout)
        init = findViewById(R.id.init_text_layout)
        openUrl = findViewById(R.id.urlButton_layout)
        urlsListHistory = findViewById(R.id.urlsListHistory_layout)
        linear_history = findViewById(R.id.linear_history_layout)
        loader = findViewById(R.id.loader_layout)
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
            linear_history.visibility = ListView.GONE
        } else {
            linear_history.visibility = ListView.VISIBLE
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
            sharedPreferences.clearPodcast()

            if (isCacheValid(this)) {
                loadRssFromCache(this)?.let { cachedData ->
                    val rssParser = RssParser()
                    val rssFeed = rssContent?.let { rssParser.parseRss(it) }

                    runOnUiThread {
                        //➡️➡️
                        rssContent?.let { openPodcastActivity(rssFeed, url, it) }
                        hideLoader()
                        buttonsStates(true)
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
                    buttonsStates(true)
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
                        } else {
                            UIUtil.showDialog(
                                context = this,
                                title = getString(R.string.dialog_tittle_fetcher),
                                message = getString(R.string.parse_null),
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
                        buttonsStates(true)
                    }
                }
            }
        }
    }

    private fun buttonsStates(state: Boolean) {
        openUrl.isEnabled = state
        clearUrls.isEnabled = state
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

        rssFeed?.let { sharedPreferences.savePodcast(it) }
        startActivity(Intent(this, PodcastActivity::class.java))
        Animatoo.animateSlideLeft(this)
        finish()
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
    }

    //💾💾
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
