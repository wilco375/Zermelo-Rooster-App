package com.wilco375.roosternotification.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.wilco375.roosternotification.R;

import java.io.InputStream;
import java.lang.ref.WeakReference;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        new DownloadImageTask(findViewById(R.id.stepOneImg))
                .execute("https://raw.githubusercontent.com/wilco375/JFCAppResources/master/zermelo_code_1.png");
        new DownloadImageTask(findViewById(R.id.stepTwoImg))
                .execute("https://raw.githubusercontent.com/wilco375/JFCAppResources/master/zermelo_code_2.png");
        new DownloadImageTask(findViewById(R.id.stepThreeImg))
                .execute("https://raw.githubusercontent.com/wilco375/JFCAppResources/master/zermelo_code_3.png");
    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private WeakReference<ImageView> bmImage;

        DownloadImageTask(ImageView bmImage) {
            this.bmImage = new WeakReference<>(bmImage);
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
            ImageView imageView = bmImage.get();
            if(imageView != null) imageView.setImageBitmap(result);
        }
    }
}
