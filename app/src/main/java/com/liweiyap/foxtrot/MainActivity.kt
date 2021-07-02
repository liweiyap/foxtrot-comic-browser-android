package com.liweiyap.foxtrot

import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.button.MaterialButton
import com.liweiyap.foxtrot.util.StripDataModel
import com.liweiyap.foxtrot.util.scraper.ScraperResult
import com.liweiyap.foxtrot.util.scraper.ScraperViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val stripDataObserver = Observer<ScraperResult<StripDataModel>> { result ->
            val tv: TextView = findViewById(R.id.hello_world)

            when (result) {
                is ScraperResult.Success<StripDataModel> -> tv.text = result.component1().date.toString()
                else -> tv.text = "Fail"
            }
        }

        vm.stripDataResult.observe(this, stripDataObserver)

        val stripCountObserver = Observer<ScraperResult<Int>> { result ->
            val tv: TextView = findViewById(R.id.hello_world)

            when (result) {
                is ScraperResult.Success<Int> -> tv.text = result.component1().toString()
                else -> tv.text = "Fail"
            }
        }

        vm.stripCountResult.observe(this, stripCountObserver)

        vm.scrapeAllStrips()

        val btn: MaterialButton = findViewById(R.id.testButton)
        btn.setOnClickListener {
            vm.countStrips()
        }
    }

    private val vm: ScraperViewModel by viewModels()
}