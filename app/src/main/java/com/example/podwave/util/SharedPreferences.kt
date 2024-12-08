package com.example.podwave.util

import android.content.Context
import android.content.SharedPreferences

class SharedPreferences(context: Context) {

    companion object {
        private const val PREF_PODWAVE= "app_preferences"
        private const val SEARCHED_URLS = "searched_urls"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_PODWAVE, Context.MODE_PRIVATE)

    fun saveUrl(url: String) {
        val urls = getUrls().toMutableSet()
        urls.add(url)
        sharedPreferences.edit().putStringSet(SEARCHED_URLS, urls).apply()
    }

    fun getUrls(): Set<String> {
        return sharedPreferences.getStringSet(SEARCHED_URLS, emptySet()) ?: emptySet()
    }

    fun clearUrls() {
        sharedPreferences.edit().remove(SEARCHED_URLS).apply()
    }

}