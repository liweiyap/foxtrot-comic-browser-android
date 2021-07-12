package com.liweiyap.foxtrot.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.liweiyap.foxtrot.R
import com.liweiyap.foxtrot.database.StripDataModel

class StripFragment(private val strip: StripDataModel): Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_strip, container, false)

    // https://stackoverflow.com/a/38718205/12367873
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tv: TextView = view.findViewById(R.id.hello_world)
        tv.text = strip.date.toString()
    }
}