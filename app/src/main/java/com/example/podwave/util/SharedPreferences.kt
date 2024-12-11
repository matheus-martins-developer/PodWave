package com.example.podwave.util

import android.content.Context
import android.content.SharedPreferences
import com.example.podwave.data.model.Podcast
import com.google.gson.Gson

class SharedPreferences(context: Context) {

    companion object {
        private const val PREF_PODWAVE= "app_preferences"
        private const val SEARCHED_URLS = "searched_urls"
        private const val PODCAST = "podcast"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_PODWAVE, Context.MODE_PRIVATE)
    private val gson = Gson()

    //💾💾
    fun saveUrl(url: String) {
        val urls = getUrls().toMutableSet()
        urls.add(url)
        sharedPreferences.edit().putStringSet(SEARCHED_URLS, urls).apply()
    }

    //⬅️⬅️
    fun getUrls(): Set<String> {
        return sharedPreferences.getStringSet(SEARCHED_URLS, emptySet()) ?: emptySet()
    }

    //🗑️🗑️
    fun clearUrls() {
        sharedPreferences.edit().remove(SEARCHED_URLS).apply()
    }

    //💾💾
    fun savePodcast(podcast: Podcast) {
        val podcastJson = gson.toJson(podcast)
        sharedPreferences.edit().putString(PODCAST, podcastJson).apply()
    }

    // ⬅️⬅️
    fun getPodcast(): Podcast? {
        val podcastJson = sharedPreferences.getString(PODCAST, null)
        return if (podcastJson != null) {
            gson.fromJson(podcastJson, Podcast::class.java)
        } else {
            null
        }
    }

    // 🗑️🗑️️
    fun clearPodcast() {
        sharedPreferences.edit().remove(PODCAST).apply()
    }
}