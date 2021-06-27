package com.liweiyap.foxtrot

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vm.makeConnectionRequest(object: Observer {
            override fun update(str: String) {
                val tv: TextView = findViewById(R.id.hello_world)
                tv.text = str
            }
        })
    }

    private val vm: TestViewModel = TestViewModel(TestConnectionBroker())
}