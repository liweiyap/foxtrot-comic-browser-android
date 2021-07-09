package com.liweiyap.foxtrot

import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.liweiyap.foxtrot.database.StripDataModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ComicBrowserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val stripDataObserver = Observer<StripDataModel?> { result ->
            val tv: TextView = findViewById(R.id.hello_world)

            if (result == null) {
                tv.text = "Fail"
            } else {
                tv.text = result.date.toString()
            }
        }

        vm.stripDataResult.observe(this, stripDataObserver)

        vm.scrapeAllStrips()
    }

    private val vm: ComicBrowserViewModel by viewModels()
}