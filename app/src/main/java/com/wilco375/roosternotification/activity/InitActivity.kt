package com.wilco375.roosternotification.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.wilco375.roosternotification.R
import com.wilco375.roosternotification.online.ZermeloSync
import io.multimoon.colorful.CAppCompatActivity
import kotlinx.android.synthetic.main.activity_init.*
import kotlinx.android.synthetic.main.content_init.*

class InitActivity : CAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)
        setSupportActionBar(toolbar)

        setupInit()
    }

    /**
     * Launch MainActivity after entering code or launch HelpActivity on button click
     */
    private fun setupInit() {
        zermeloConfirm.setOnClickListener({
            val code = zermeloCode.text.toString().replace((" ").toRegex(), "")
            val sp = getSharedPreferences("Main", Context.MODE_PRIVATE)
            if (ZermeloSync.authenticate(code, baseContext, sp)) {
                AlertDialog.Builder(this@InitActivity)
                        .setTitle(R.string.first_sync_title)
                        .setMessage(R.string.first_sync_message)
                        .setCancelable(true)
                        .setNeutralButton(android.R.string.ok, { _, _ ->
                            val i = Intent(this@InitActivity, MainActivity::class.java)
                            finish()
                            startActivity(i)
                        })
                        .show()
            }
        })

        zermeloCodeHelp.setOnClickListener({ _ ->
            val i = Intent(application, HelpActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
        })
    }
}
