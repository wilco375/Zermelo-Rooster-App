package com.wilco375.roosternotification.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.wilco375.roosternotification.R;

import java.io.InputStream;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
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
