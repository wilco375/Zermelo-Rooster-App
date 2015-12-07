package com.wilco375.roosternotification;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class MainActivity extends Activity {

    CheckBox a12; EditText a13; EditText a14; CheckBox a22; EditText a23; EditText a24; CheckBox a32; EditText a33; EditText a34; CheckBox a42; EditText a43; EditText a44; CheckBox a52; EditText a53; EditText a54; CheckBox a62; EditText a63; EditText a64; CheckBox a72; EditText a73; EditText a74; CheckBox a82; EditText a83; EditText a84;
    CheckBox b12; EditText b13; EditText b14; CheckBox b22; EditText b23; EditText b24; CheckBox b32; EditText b33; EditText b34; CheckBox b42; EditText b43; EditText b44; CheckBox b52; EditText b53; EditText b54; CheckBox b62; EditText b63; EditText b64; CheckBox b72; EditText b73; EditText b74; CheckBox b82; EditText b83; EditText b84;
    CheckBox c12; EditText c13; EditText c14; CheckBox c22; EditText c23; EditText c24; CheckBox c32; EditText c33; EditText c34; CheckBox c42; EditText c43; EditText c44; CheckBox c52; EditText c53; EditText c54; CheckBox c62; EditText c63; EditText c64; CheckBox c72; EditText c73; EditText c74; CheckBox c82; EditText c83; EditText c84;
    CheckBox d12; EditText d13; EditText d14; CheckBox d22; EditText d23; EditText d24; CheckBox d32; EditText d33; EditText d34; CheckBox d42; EditText d43; EditText d44; CheckBox d52; EditText d53; EditText d54; CheckBox d62; EditText d63; EditText d64; CheckBox d72; EditText d73; EditText d74; CheckBox d82; EditText d83; EditText d84;
    CheckBox e12; EditText e13; EditText e14; CheckBox e22; EditText e23; EditText e24; CheckBox e32; EditText e33; EditText e34; CheckBox e42; EditText e43; EditText e44; CheckBox e52; EditText e53; EditText e54; CheckBox e62; EditText e63; EditText e64; CheckBox e72; EditText e73; EditText e74; CheckBox e82; EditText e83; EditText e84;
    CheckBox notify;
    InterstitialAd interstitialAd;
    AdRequest adRequest;
    Boolean paused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sendBroadcast(new Intent(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_HOME));
        setContentView(R.layout.activity_main);

        paused = false;

        AdView mAdView = (AdView) findViewById(R.id.adView);
        adRequest = new AdRequest.Builder().addTestDevice(getResources().getString(R.string.test_device_id)).build();
        mAdView.loadAd(adRequest);

        /*
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_ad_unit_id));
        adRequest = new AdRequest.Builder().addTestDevice(getResources().getString(R.string.test_device_id)).build();
        requestNewInterstitial();

        final Thread interstitialThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Sleeping for 45 sec");
                    while(!paused) {
                        Thread.sleep(30000);
                        System.out.println("Sleeping finished, now showing intersitial...");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("Showing interstitial, interstitialAd.isLoaded(): " + String.valueOf(interstitialAd.isLoaded()));
                                if (interstitialAd.isLoaded() && !paused) {
                                    interstitialAd.show();
                                    requestNewInterstitial();
                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        interstitialThread.start();
        */


        a12 = (CheckBox) findViewById(R.id.a12); a13 = (EditText) findViewById(R.id.a13); a14 = (EditText) findViewById(R.id.a14); a22 = (CheckBox) findViewById(R.id.a22); a23 = (EditText) findViewById(R.id.a23); a24 = (EditText) findViewById(R.id.a24); a32 = (CheckBox) findViewById(R.id.a32); a33 = (EditText) findViewById(R.id.a33); a34 = (EditText) findViewById(R.id.a34); a42 = (CheckBox) findViewById(R.id.a42); a43 = (EditText) findViewById(R.id.a43); a44 = (EditText) findViewById(R.id.a44); a52 = (CheckBox) findViewById(R.id.a52); a53 = (EditText) findViewById(R.id.a53); a54 = (EditText) findViewById(R.id.a54); a62 = (CheckBox) findViewById(R.id.a62); a63 = (EditText) findViewById(R.id.a63); a64 = (EditText) findViewById(R.id.a64); a72 = (CheckBox) findViewById(R.id.a72); a73 = (EditText) findViewById(R.id.a73); a74 = (EditText) findViewById(R.id.a74); a82 = (CheckBox) findViewById(R.id.a82); a83 = (EditText) findViewById(R.id.a83); a84 = (EditText) findViewById(R.id.a84);
        b12 = (CheckBox) findViewById(R.id.b12); b13 = (EditText) findViewById(R.id.b13); b14 = (EditText) findViewById(R.id.b14); b22 = (CheckBox) findViewById(R.id.b22); b23 = (EditText) findViewById(R.id.b23); b24 = (EditText) findViewById(R.id.b24); b32 = (CheckBox) findViewById(R.id.b32); b33 = (EditText) findViewById(R.id.b33); b34 = (EditText) findViewById(R.id.b34); b42 = (CheckBox) findViewById(R.id.b42); b43 = (EditText) findViewById(R.id.b43); b44 = (EditText) findViewById(R.id.b44); b52 = (CheckBox) findViewById(R.id.b52); b53 = (EditText) findViewById(R.id.b53); b54 = (EditText) findViewById(R.id.b54); b62 = (CheckBox) findViewById(R.id.b62); b63 = (EditText) findViewById(R.id.b63); b64 = (EditText) findViewById(R.id.b64); b72 = (CheckBox) findViewById(R.id.b72); b73 = (EditText) findViewById(R.id.b73); b74 = (EditText) findViewById(R.id.b74); b82 = (CheckBox) findViewById(R.id.b82); b83 = (EditText) findViewById(R.id.b83); b84 = (EditText) findViewById(R.id.b84);
        c12 = (CheckBox) findViewById(R.id.c12); c13 = (EditText) findViewById(R.id.c13); c14 = (EditText) findViewById(R.id.c14); c22 = (CheckBox) findViewById(R.id.c22); c23 = (EditText) findViewById(R.id.c23); c24 = (EditText) findViewById(R.id.c24); c32 = (CheckBox) findViewById(R.id.c32); c33 = (EditText) findViewById(R.id.c33); c34 = (EditText) findViewById(R.id.c34); c42 = (CheckBox) findViewById(R.id.c42); c43 = (EditText) findViewById(R.id.c43); c44 = (EditText) findViewById(R.id.c44); c52 = (CheckBox) findViewById(R.id.c52); c53 = (EditText) findViewById(R.id.c53); c54 = (EditText) findViewById(R.id.c54); c62 = (CheckBox) findViewById(R.id.c62); c63 = (EditText) findViewById(R.id.c63); c64 = (EditText) findViewById(R.id.c64); c72 = (CheckBox) findViewById(R.id.c72); c73 = (EditText) findViewById(R.id.c73); c74 = (EditText) findViewById(R.id.c74); c82 = (CheckBox) findViewById(R.id.c82); c83 = (EditText) findViewById(R.id.c83); c84 = (EditText) findViewById(R.id.c84);
        d12 = (CheckBox) findViewById(R.id.d12); d13 = (EditText) findViewById(R.id.d13); d14 = (EditText) findViewById(R.id.d14); d22 = (CheckBox) findViewById(R.id.d22); d23 = (EditText) findViewById(R.id.d23); d24 = (EditText) findViewById(R.id.d24); d32 = (CheckBox) findViewById(R.id.d32); d33 = (EditText) findViewById(R.id.d33); d34 = (EditText) findViewById(R.id.d34); d42 = (CheckBox) findViewById(R.id.d42); d43 = (EditText) findViewById(R.id.d43); d44 = (EditText) findViewById(R.id.d44); d52 = (CheckBox) findViewById(R.id.d52); d53 = (EditText) findViewById(R.id.d53); d54 = (EditText) findViewById(R.id.d54); d62 = (CheckBox) findViewById(R.id.d62); d63 = (EditText) findViewById(R.id.d63); d64 = (EditText) findViewById(R.id.d64); d72 = (CheckBox) findViewById(R.id.d72); d73 = (EditText) findViewById(R.id.d73); d74 = (EditText) findViewById(R.id.d74); d82 = (CheckBox) findViewById(R.id.d82); d83 = (EditText) findViewById(R.id.d83); d84 = (EditText) findViewById(R.id.d84);
        e12 = (CheckBox) findViewById(R.id.e12); e13 = (EditText) findViewById(R.id.e13); e14 = (EditText) findViewById(R.id.e14); e22 = (CheckBox) findViewById(R.id.e22); e23 = (EditText) findViewById(R.id.e23); e24 = (EditText) findViewById(R.id.e24); e32 = (CheckBox) findViewById(R.id.e32); e33 = (EditText) findViewById(R.id.e33); e34 = (EditText) findViewById(R.id.e34); e42 = (CheckBox) findViewById(R.id.e42); e43 = (EditText) findViewById(R.id.e43); e44 = (EditText) findViewById(R.id.e44); e52 = (CheckBox) findViewById(R.id.e52); e53 = (EditText) findViewById(R.id.e53); e54 = (EditText) findViewById(R.id.e54); e62 = (CheckBox) findViewById(R.id.e62); e63 = (EditText) findViewById(R.id.e63); e64 = (EditText) findViewById(R.id.e64); e72 = (CheckBox) findViewById(R.id.e72); e73 = (EditText) findViewById(R.id.e73); e74 = (EditText) findViewById(R.id.e74); e82 = (CheckBox) findViewById(R.id.e82); e83 = (EditText) findViewById(R.id.e83); e84 = (EditText) findViewById(R.id.e84);

        notify = (CheckBox) findViewById(R.id.showNotificationCheckbox);

        final SharedPreferences sp = getSharedPreferences("Main",MODE_PRIVATE);

        if(sp.getBoolean("RoosterSaved",false)){
            fillEditTextFields(sp);
        }
    }

    /*
    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(getResources().getString(R.string.test_device_id)).build();
        interstitialAd.loadAd(adRequest);
    }
    */

    private void fillEditTextFields(SharedPreferences sp){
        a13.setText(sp.getString("a13","")); a14.setText(sp.getString("a14","")); a23.setText(sp.getString("a23","")); a24.setText(sp.getString("a24","")); a33.setText(sp.getString("a33","")); a34.setText(sp.getString("a34","")); a43.setText(sp.getString("a43","")); a44.setText(sp.getString("a44","")); a53.setText(sp.getString("a53","")); a54.setText(sp.getString("a54","")); a63.setText(sp.getString("a63","")); a64.setText(sp.getString("a64","")); a73.setText(sp.getString("a73","")); a74.setText(sp.getString("a74","")); a83.setText(sp.getString("a83","")); a84.setText(sp.getString("a84",""));
        b13.setText(sp.getString("b13","")); b14.setText(sp.getString("b14","")); b23.setText(sp.getString("b23","")); b24.setText(sp.getString("b24","")); b33.setText(sp.getString("b33","")); b34.setText(sp.getString("b34","")); b43.setText(sp.getString("b43","")); b44.setText(sp.getString("b44","")); b53.setText(sp.getString("b53","")); b54.setText(sp.getString("b54","")); b63.setText(sp.getString("b63","")); b64.setText(sp.getString("b64","")); b73.setText(sp.getString("b73","")); b74.setText(sp.getString("b74","")); b83.setText(sp.getString("b83","")); b84.setText(sp.getString("b84",""));
        c13.setText(sp.getString("c13","")); c14.setText(sp.getString("c14","")); c23.setText(sp.getString("c23","")); c24.setText(sp.getString("c24","")); c33.setText(sp.getString("c33","")); c34.setText(sp.getString("c34","")); c43.setText(sp.getString("c43","")); c44.setText(sp.getString("c44","")); c53.setText(sp.getString("c53","")); c54.setText(sp.getString("c54","")); c63.setText(sp.getString("c63","")); c64.setText(sp.getString("c64","")); c73.setText(sp.getString("c73","")); c74.setText(sp.getString("c74","")); c83.setText(sp.getString("c83","")); c84.setText(sp.getString("c84",""));
        d13.setText(sp.getString("d13","")); d14.setText(sp.getString("d14","")); d23.setText(sp.getString("d23","")); d24.setText(sp.getString("d24","")); d33.setText(sp.getString("d33","")); d34.setText(sp.getString("d34","")); d43.setText(sp.getString("d43","")); d44.setText(sp.getString("d44","")); d53.setText(sp.getString("d53","")); d54.setText(sp.getString("d54","")); d63.setText(sp.getString("d63","")); d64.setText(sp.getString("d64","")); d73.setText(sp.getString("d73","")); d74.setText(sp.getString("d74","")); d83.setText(sp.getString("d83","")); d84.setText(sp.getString("d84",""));
        e13.setText(sp.getString("e13","")); e14.setText(sp.getString("e14","")); e23.setText(sp.getString("e23","")); e24.setText(sp.getString("e24","")); e33.setText(sp.getString("e33","")); e34.setText(sp.getString("e34","")); e43.setText(sp.getString("e43","")); e44.setText(sp.getString("e44","")); e53.setText(sp.getString("e53","")); e54.setText(sp.getString("e54","")); e63.setText(sp.getString("e63","")); e64.setText(sp.getString("e64","")); e73.setText(sp.getString("e73","")); e74.setText(sp.getString("e74","")); e83.setText(sp.getString("e83","")); e84.setText(sp.getString("e84", ""));

        a12.setChecked(sp.getBoolean("a12", false)); a22.setChecked(sp.getBoolean("a22",false)); a32.setChecked(sp.getBoolean("a32",false)); a42.setChecked(sp.getBoolean("a42",false)); a52.setChecked(sp.getBoolean("a52",false)); a62.setChecked(sp.getBoolean("a62",false)); a72.setChecked(sp.getBoolean("a72",false)); a82.setChecked(sp.getBoolean("a82",false));
        b12.setChecked(sp.getBoolean("b12",false)); b22.setChecked(sp.getBoolean("b22",false)); b32.setChecked(sp.getBoolean("b32",false)); b42.setChecked(sp.getBoolean("b42",false)); b52.setChecked(sp.getBoolean("b52",false)); b62.setChecked(sp.getBoolean("b62",false)); b72.setChecked(sp.getBoolean("b72",false)); b82.setChecked(sp.getBoolean("b82",false));
        c12.setChecked(sp.getBoolean("c12",false)); c22.setChecked(sp.getBoolean("c22",false)); c32.setChecked(sp.getBoolean("c32",false)); c42.setChecked(sp.getBoolean("c42",false)); c52.setChecked(sp.getBoolean("c52",false)); c62.setChecked(sp.getBoolean("c62",false)); c72.setChecked(sp.getBoolean("c72",false)); c82.setChecked(sp.getBoolean("c82",false));
        d12.setChecked(sp.getBoolean("d12",false)); d22.setChecked(sp.getBoolean("d22",false)); d32.setChecked(sp.getBoolean("d32",false)); d42.setChecked(sp.getBoolean("d42",false)); d52.setChecked(sp.getBoolean("d52",false)); d62.setChecked(sp.getBoolean("d62",false)); d72.setChecked(sp.getBoolean("d72",false)); d82.setChecked(sp.getBoolean("d82",false));
        e12.setChecked(sp.getBoolean("e12",false)); e22.setChecked(sp.getBoolean("e22",false)); e32.setChecked(sp.getBoolean("e32",false)); e42.setChecked(sp.getBoolean("e42",false)); e52.setChecked(sp.getBoolean("e52",false)); e62.setChecked(sp.getBoolean("e62",false)); e72.setChecked(sp.getBoolean("e72",false)); e82.setChecked(sp.getBoolean("e82",false));

        notify.setChecked(sp.getBoolean("notify",true));
    }

    private void saveEditTextFields(SharedPreferences sp){
        SharedPreferences.Editor spe = sp.edit();

        spe.putString("a13",a13.getText().toString()); spe.putString("a14",a14.getText().toString()); spe.putString("a23",a23.getText().toString()); spe.putString("a24",a24.getText().toString()); spe.putString("a33",a33.getText().toString()); spe.putString("a34",a34.getText().toString()); spe.putString("a43",a43.getText().toString()); spe.putString("a44",a44.getText().toString()); spe.putString("a53",a53.getText().toString()); spe.putString("a54",a54.getText().toString()); spe.putString("a63",a63.getText().toString()); spe.putString("a64",a64.getText().toString()); spe.putString("a73",a73.getText().toString()); spe.putString("a74",a74.getText().toString()); spe.putString("a83",a83.getText().toString()); spe.putString("a84",a84.getText().toString());
        spe.putString("b13",b13.getText().toString()); spe.putString("b14",b14.getText().toString()); spe.putString("b23",b23.getText().toString()); spe.putString("b24",b24.getText().toString()); spe.putString("b33",b33.getText().toString()); spe.putString("b34",b34.getText().toString()); spe.putString("b43",b43.getText().toString()); spe.putString("b44",b44.getText().toString()); spe.putString("b53",b53.getText().toString()); spe.putString("b54",b54.getText().toString()); spe.putString("b63",b63.getText().toString()); spe.putString("b64",b64.getText().toString()); spe.putString("b73",b73.getText().toString()); spe.putString("b74",b74.getText().toString()); spe.putString("b83",b83.getText().toString()); spe.putString("b84",b84.getText().toString());
        spe.putString("c13",c13.getText().toString()); spe.putString("c14",c14.getText().toString()); spe.putString("c23",c23.getText().toString()); spe.putString("c24",c24.getText().toString()); spe.putString("c33",c33.getText().toString()); spe.putString("c34",c34.getText().toString()); spe.putString("c43",c43.getText().toString()); spe.putString("c44",c44.getText().toString()); spe.putString("c53",c53.getText().toString()); spe.putString("c54",c54.getText().toString()); spe.putString("c63",c63.getText().toString()); spe.putString("c64",c64.getText().toString()); spe.putString("c73",c73.getText().toString()); spe.putString("c74",c74.getText().toString()); spe.putString("c83",c83.getText().toString()); spe.putString("c84",c84.getText().toString());
        spe.putString("d13",d13.getText().toString()); spe.putString("d14",d14.getText().toString()); spe.putString("d23",d23.getText().toString()); spe.putString("d24",d24.getText().toString()); spe.putString("d33",d33.getText().toString()); spe.putString("d34",d34.getText().toString()); spe.putString("d43",d43.getText().toString()); spe.putString("d44",d44.getText().toString()); spe.putString("d53",d53.getText().toString()); spe.putString("d54",d54.getText().toString()); spe.putString("d63",d63.getText().toString()); spe.putString("d64",d64.getText().toString()); spe.putString("d73",d73.getText().toString()); spe.putString("d74",d74.getText().toString()); spe.putString("d83",d83.getText().toString()); spe.putString("d84",d84.getText().toString());
        spe.putString("e13",e13.getText().toString()); spe.putString("e14",e14.getText().toString()); spe.putString("e23",e23.getText().toString()); spe.putString("e24",e24.getText().toString()); spe.putString("e33",e33.getText().toString()); spe.putString("e34",e34.getText().toString()); spe.putString("e43",e43.getText().toString()); spe.putString("e44",e44.getText().toString()); spe.putString("e53",e53.getText().toString()); spe.putString("e54",e54.getText().toString()); spe.putString("e63",e63.getText().toString()); spe.putString("e64",e64.getText().toString()); spe.putString("e73",e73.getText().toString()); spe.putString("e74",e74.getText().toString()); spe.putString("e83", e83.getText().toString()); spe.putString("e84", e84.getText().toString());

        spe.putBoolean("a12", a12.isChecked()); spe.putBoolean("a22", a22.isChecked()); spe.putBoolean("a32",a32.isChecked()); spe.putBoolean("a42",a42.isChecked()); spe.putBoolean("a52",a52.isChecked()); spe.putBoolean("a62",a62.isChecked()); spe.putBoolean("a72",a72.isChecked()); spe.putBoolean("a82",a82.isChecked());
        spe.putBoolean("b12",b12.isChecked()); spe.putBoolean("b22",b22.isChecked()); spe.putBoolean("b32",b32.isChecked()); spe.putBoolean("b42",b42.isChecked()); spe.putBoolean("b52",b52.isChecked()); spe.putBoolean("b62",b62.isChecked()); spe.putBoolean("b72",b72.isChecked()); spe.putBoolean("b82",b82.isChecked());
        spe.putBoolean("c12",c12.isChecked()); spe.putBoolean("c22",c22.isChecked()); spe.putBoolean("c32",c32.isChecked()); spe.putBoolean("c42",c42.isChecked()); spe.putBoolean("c52",c52.isChecked()); spe.putBoolean("c62",c62.isChecked()); spe.putBoolean("c72",c72.isChecked()); spe.putBoolean("c82",c82.isChecked());
        spe.putBoolean("d12",d12.isChecked()); spe.putBoolean("d22",d22.isChecked()); spe.putBoolean("d32",d32.isChecked()); spe.putBoolean("d42",d42.isChecked()); spe.putBoolean("d52",d52.isChecked()); spe.putBoolean("d62",d62.isChecked()); spe.putBoolean("d72",d72.isChecked()); spe.putBoolean("d82",d82.isChecked());
        spe.putBoolean("e12",e12.isChecked()); spe.putBoolean("e22",e22.isChecked()); spe.putBoolean("e32",e32.isChecked()); spe.putBoolean("e42",e42.isChecked()); spe.putBoolean("e52",e52.isChecked()); spe.putBoolean("e62",e62.isChecked()); spe.putBoolean("e72",e72.isChecked()); spe.putBoolean("e82", e82.isChecked());

        spe.putBoolean("notify",notify.isChecked());

        if(!sp.getBoolean("RoosterSaved",false)){
            spe.putBoolean("RoosterSaved",true);
        }

        spe.apply();

        Toast.makeText(this,getResources().getString(R.string.saved),Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        saveEditTextFields(getSharedPreferences("Main", MODE_PRIVATE));
        paused = true;
        super.onPause();
    }
}