package com.example.podwave.util

import com.example.podwave.data.model.Podcast
import com.example.podwave.data.model.Episode
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.parser.Parser

class RssParser {

    fun parseRss(xml: String): Podcast? {

        //⬅️⬅️
        return try {

            val document = Jsoup.parse(xml, "", Parser.xmlParser())

            val channel = document.select("channel").first() ?: return null
            val title = channel.select("title").first()?.text() ?: "Sem título"
            val description = channel.select("description").first()?.text() ?: "Sem descrição"
            val link = channel.select("link").first()?.text() ?: ""
            val imageUrl = channel.select("image > url").first()?.text()
                ?: channel.select("itunes\\:image").first()?.attr("href")
            val author = channel.select("itunes\\:author").first()?.text()
                ?: channel.select("author").first()?.text()
            val category = channel.select("itunes\\:category").first()?.attr("text") ?: "Sem categoria"
            val episodes = mutableListOf<Episode>()
            val items = channel.select("item")
            for (item in items) {
                episodes.add(parseEpisode(item))
            }

            Podcast(
                title = title,
                description = description,
                link = link,
                imageUrl = imageUrl,
                author = author,
                category = category,
                episodes = episodes
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun parseEpisode(item: Element): Episode {

        val title = item.select("itunes\\:title").first()?.text()
            ?: item.select("title").first()?.text() ?: "Sem título"
        val description = item.select("description").first()?.text() ?: "Sem descrição"
        val link = item.select("link").first()?.text() ?: ""
        val pubDate = item.select("pubDate").first()?.text() ?: "Sem data"
        val audioUrl = item.select("enclosure").first()?.attr("url") ?: ""
        val imageUrl = item.select("itunes\\:image").first()?.attr("href")

        //⬅️⬅️
        return Episode(
            title = title,
            description = description,
            audioUrl = audioUrl,
            imageUrl = imageUrl,
            pubDate = pubDate
        )
    }
}