package com.liweiyap.foxtrot

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.liweiyap.foxtrot.util.scraper.ScraperObserver
import com.liweiyap.foxtrot.util.scraper.ScraperViewModel
import com.liweiyap.foxtrot.util.scraper.WebpageScraper

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vm.scrapeLatestStrip(object: ScraperObserver {
            override fun update(str: String) {
                val tv: TextView = findViewById(R.id.hello_world)
                tv.text = str
            }
        })

        val btn: MaterialButton = findViewById(R.id.testButton)
        btn.setOnClickListener {
            vm.scrapePrevStrips(object : ScraperObserver {
                override fun update(str: String) {
                    val tv: TextView = findViewById(R.id.hello_world)
                    tv.text = str
                }
            })
        }
    }

    private val vm: ScraperViewModel = ScraperViewModel(WebpageScraper())
}