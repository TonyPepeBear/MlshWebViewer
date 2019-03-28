package com.tonypepe.mlshwebviewer

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity(), AnkoLogger {
    companion object {
        const val MAIN_NEWS =
            "http://newweb.mlsh.tp.edu.tw/RSSFeed/RSS_news.asp?id={2419DAD7-B407-4439-BDAA-25B6CC8962FE}"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun goToMainNews(view: View) {
        startActivity<MainNewsActivity>()
    }
}
