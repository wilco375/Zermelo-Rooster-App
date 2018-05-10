package com.wilco375.roosternotification.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.wilco375.roosternotification.R
import com.wilco375.roosternotification.exception.InvalidCodeException
import com.wilco375.roosternotification.exception.InvalidWebsiteException
import com.wilco375.roosternotification.exception.NoInternetException
import com.wilco375.roosternotification.exception.UnknownAuthenticationException
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
            // Check website
            var website = zermeloSite.text.toString()
            if(!website.endsWith(".zportal.nl")) {
                if (website != "" && !website.contains(".")) {
                    website += ".zportal.nl"
                } else {
                    zermeloSite.error = getString(R.string.invalid_site)
                    return@setOnClickListener
                }
            }

            // Check code and get API token
            val code = zermeloCode.text.toString().replace(" ", "")
            val sp = getSharedPreferences("Main", Context.MODE_PRIVATE)
            try {
                if (ZermeloSync.authenticate(website, code, baseContext, sp)) {
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
            } catch (e: InvalidWebsiteException) {
                zermeloSite.error = getString(R.string.invalid_site)
            } catch (e: InvalidCodeException) {
                zermeloCode.error = getString(R.string.invalid_code)
            } catch (e: NoInternetException) {
                zermeloCode.error = getString(R.string.no_connection)
            } catch (e: UnknownAuthenticationException) {
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }
        })

        zermeloCodeHelp.setOnClickListener({ _ ->
            val i = Intent(application, HelpActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
        })
    }
}
