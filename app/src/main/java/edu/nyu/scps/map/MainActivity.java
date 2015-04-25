package edu.nyu.scps.map;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements LocationListener {
    LocationManager locationManager;
    boolean heardFromGPS = false;
    boolean heardFromWebView = false;
    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        WebView webView = (WebView)findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setVerticalScrollBarEnabled(false);
        webView.setWebViewClient(new WebViewClient() {
            //Called when the WebView has finished loading the page of HTML.
            @Override
            public void onPageFinished(WebView view, String url) {
                heardFromWebView = true;
                if (heardFromGPS && heardFromWebView) {
                    int zoom = 18;
                    String javascript = String.format("javascript:mapFunction(%f, %f, %d)",
                            latitude, longitude, zoom);
                    view.loadUrl(javascript);
                }
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            //Called when the JavaScript alert function is called.
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Toast toast = Toast.makeText(MainActivity.this, "onJsAlert " + url + " " + message,
                        Toast.LENGTH_LONG);
                toast.show();

                //Cause the JavaScript alert function to return.
                result.confirm();
                return true;
            }
        });

        webView.loadUrl("file:///android_asset/map.html");
    }

    @Override
    public void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,	//or LocationManager.NETWORK_PROVIDER, or both
                60000L,		//minimum time interval (milliseconds) between notifications
                100.0f,		//minimum change in distance (meters) between notifications
                this
        );
    }

    @Override
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }


    @Override
    public void onLocationChanged(Location location) {
        // Called when a new location is found by the network location provider.
        heardFromGPS = true;
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        if (heardFromGPS && heardFromWebView) {
            int zoom = 18;
            String javascript = String.format("javascript:mapFunction(%f, %f,  %d)",
                    latitude, longitude, zoom);
            WebView webView = (WebView)findViewById(R.id.webView);
            webView.loadUrl(javascript);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
