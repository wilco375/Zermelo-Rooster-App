package com.wilco375.roosternotification.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.widget.ImageView
import com.wilco375.roosternotification.R.layout.activity_help
import io.multimoon.colorful.CAppCompatActivity
import kotlinx.android.synthetic.main.activity_help.*
import kotlinx.android.synthetic.main.content_help.*
import java.lang.ref.WeakReference

class HelpActivity : CAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_help)
        setSupportActionBar(toolbar)
        DownloadImageTask(stepOneImg)
                .execute("https://raw.githubusercontent.com/wilco375/JFCAppResources/master/zermelo_code_1.png")
        DownloadImageTask(stepTwoImg)
                .execute("https://raw.githubusercontent.com/wilco375/JFCAppResources/master/zermelo_code_2.png")
        DownloadImageTask(stepThreeImg)
                .execute("https://raw.githubusercontent.com/wilco375/JFCAppResources/master/zermelo_code_3.png")
    }

    private class DownloadImageTask internal constructor(bmImage: ImageView) : AsyncTask<String, Void, Bitmap>() {
        private val bmImage: WeakReference<ImageView> = WeakReference(bmImage)

        override fun doInBackground(vararg urls: String): Bitmap? {
            val urldisplay = urls[0]
            var mIcon11: Bitmap? = null
            try {
                val `in` = java.net.URL(urldisplay).openStream()
                mIcon11 = BitmapFactory.decodeStream(`in`)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return mIcon11
        }

        override fun onPostExecute(result: Bitmap) {
            bmImage.get()?.setImageBitmap(result)
        }
    }
}
