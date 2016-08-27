package com.wilco375.roosternotification;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

public class ZermeloHelp extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zermelo_help);
        new DownloadImageTask((ImageView) findViewById(R.id.stepOneImg))
                .execute("https://raw.githubusercontent.com/wilco375/JFCAppResources/master/zermelo_code_1.png");
        new DownloadImageTask((ImageView) findViewById(R.id.stepTwoImg))
                .execute("https://raw.githubusercontent.com/wilco375/JFCAppResources/master/zermelo_code_2.png");
        new DownloadImageTask((ImageView) findViewById(R.id.stepThreeImg))
                .execute("https://raw.githubusercontent.com/wilco375/JFCAppResources/master/zermelo_code_3.png");
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
