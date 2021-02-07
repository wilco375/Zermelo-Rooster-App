package com.wilco375.roosternotification.activity

import android.os.Bundle
import com.wilco375.roosternotification.R.layout.activity_help
import io.multimoon.colorful.CAppCompatActivity
import kotlinx.android.synthetic.main.activity_help.*

class HelpActivity : CAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_help)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }
}
