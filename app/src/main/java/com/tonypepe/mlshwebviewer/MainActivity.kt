package com.tonypepe.mlshwebviewer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Xml
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.browse
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.info
import org.xml.sax.helpers.XMLReaderFactory
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory

import java.net.URL

class MainActivity : AppCompatActivity(), AnkoLogger {
    companion object {
        const val MAIN_NEWS =
            "http://newweb.mlsh.tp.edu.tw/RSSFeed/RSS_news.asp?id={2419DAD7-B407-4439-BDAA-25B6CC8962FE}"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val httpClient = OkHttpClient()
        val request = Request.Builder().url(URL(MAIN_NEWS)).build()
        doAsync {
            val httpStream = httpClient.newCall(request).execute().body()?.byteStream()
            val parser = Xml.newPullParser()
            var eventType = parser.eventType
            parser.setInput(httpStream, "UTF-8")
            var inItem = false
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.name.equals("item", ignoreCase = true)) {
                        inItem = true
                    } else if (parser.name.equals("title", ignoreCase = true)) {
                        if (inItem) {
                            info { parser.nextText() }
                        }
                    } else if (parser.name.equals("link" , ignoreCase = true)) {
                        if (inItem) {
                            info { parser.nextText() }
                        }
                    }
                } else if (eventType == XmlPullParser.END_TAG && parser.name.equals( "title", ignoreCase = true)) {
                    inItem = false
                }
                eventType = parser.next()
            }
        }
        browse("http://newweb.mlsh.tp.edu.tw/news/u_news_v2.asp?id={2419DAD7-B407-4439-BDAA-25B6CC8962FE}&newsid=9776")
    }
}
