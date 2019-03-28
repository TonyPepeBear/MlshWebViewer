package com.tonypepe.mlshwebviewer

import android.os.Bundle
import android.util.Xml
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main_news.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.xmlpull.v1.XmlPullParser
import java.net.URL

class MainNewsActivity : AppCompatActivity(), AnkoLogger {

    val newsList = arrayListOf<News>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_news)
        supportActionBar?.title = resources.getString(R.string.important_news)
        getMainNews()
    }

    private fun setRecycler() {
        info { newsList }
        recycler.setHasFixedSize(true)
        recycler.layoutManager = LinearLayoutManager(this@MainNewsActivity)
        recycler.adapter = NewsAdapter()
    }


    private fun getMainNews() {
        val httpClient = OkHttpClient()
        val request = Request.Builder().url(URL(MainActivity.MAIN_NEWS)).build()
        doAsync {
            val httpStream = httpClient.newCall(request).execute().body()?.byteStream()
            val parser = Xml.newPullParser()
            var eventType = parser.eventType
            parser.setInput(httpStream, "UTF-8")
            var inItem = false
            var title = ""
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.name.equals("item", ignoreCase = true)) {
                        inItem = true
                    } else if (parser.name.equals("title", ignoreCase = true)) {
                        if (inItem) {
                            title = parser.nextText().run {
                                substring(1, length - 1)
                            }
                        }
                    } else if (parser.name.equals("link", ignoreCase = true)) {
                        if (inItem) {
                            newsList.add(News(title, parser.nextText().run {
                                substring(1, length - 1)
                            }))
                        }
                    }
                } else if (eventType == XmlPullParser.END_TAG && parser.name.equals("title", ignoreCase = true)) {
                    inItem = false
                }
                eventType = parser.next()
            }
            uiThread {
                setRecycler()
            }
        }
    }

    inner class NewsAdapter : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder = NewsViewHolder(
            LayoutInflater.from(this@MainNewsActivity)
                .inflate(R.layout.news_card, parent, false)
        )

        override fun getItemCount(): Int = newsList.size

        override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
            holder.txTitle.text = newsList[position].title
            info { title }
            holder.url = newsList[position].url
        }


        inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val txTitle: TextView = itemView.findViewById(R.id.title)
            var url: String = ""

            init {
                itemView.onClick {
                    browse(url)
                }
            }
        }
    }
}


data class News(val title: String, val url: String)

