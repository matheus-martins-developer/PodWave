package com.example.podwave.data

import android.app.Activity
import android.content.Context
import android.util.Patterns
import android.widget.Toast
import com.example.podwave.R.string.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class RssFetcher(private val context: Context) {

    private val client = OkHttpClient()

    //🛜🛜
    fun fetchRss(url: String, callback: (String?) -> Unit) {

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                showToast(context.getString(connection_failure))
                callback(null)
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        callback(responseBody)
                    } else {
                        showToast(context.getString(empty_rss))
                        callback(null)
                    }
                } else {
                    showToast(context.getString(response_error))
                    callback(null)
                }
            }
        })
    }

    //💬💬
    private fun showToast(message: String) {
        (context as? Activity)?.runOnUiThread {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}