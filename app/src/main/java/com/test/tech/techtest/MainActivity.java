package com.test.tech.techtest;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ListFragment.Callbacks{
    private static final String URL_STRING = "http://ads.appia.com/getAds?id=236&password=OVUJ1DJN&siteId=4288&deviceId=4230&sessionId=techtestsession&totalCampaignsRequested=10";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (hasNetworkConnection()) {
            loadURL(URL_STRING);
        } else {
            //TODO handle network connection error
        }
    }

    private void loadURL(String url) {
        new DownloadXmlTask().execute(url);
    }

    private boolean hasNetworkConnection() {
        //TODO check shared prefs for WIFI vs Cellular connection preferences
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
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
        if (id == R.id.refresh) {
            refresh();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void refresh() {
        loadURL(URL_STRING);
    }

    @Override
    public void onItemClick(Ad ad) {
        Fragment frag = AdvertFragment.newInstance(ad);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, frag, AdvertFragment.TAG);
        transaction.addToBackStack(ListFragment.TAG);
        transaction.commit();
    }

    private class DownloadXmlTask extends AsyncTask<String, Void, List<Ad>> {

        @Override
        protected List<Ad> doInBackground(String... urls) {
            try {
                return loadXmlFromNetwork(urls[0]);
            } catch (IOException e) {
                return null;
            } catch (XmlPullParserException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Ad> result) {
            if (result != null) {
                ListFragment fragment = (ListFragment) getFragmentManager().findFragmentByTag(ListFragment.TAG);
                if (fragment == null) {
                    fragment = ListFragment.newInstance(result);
                    getFragmentManager().beginTransaction().replace(R.id.container, fragment, ListFragment.TAG).commit();
                } else {
                    fragment.update(result);
                }
            } else {
                handleNoAds();
            }
        }

        private List<Ad> loadXmlFromNetwork(String url) throws XmlPullParserException, IOException {
            InputStream stream = null;
            // Instantiate the parser
            DTAdXMLParser xmlParser = new DTAdXMLParser();
            List<Ad> ads = null;

            try {
                stream = downloadUrl(url);
                ads = xmlParser.parse(stream);
                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
            return ads;
        }
    }

    private void handleNoAds() {
        Snackbar.make(this.findViewById(R.id.container), "No Ads", Snackbar.LENGTH_SHORT).show();
    }

    private InputStream downloadUrl(String urlString) throws IOException{
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("lname", "Dishman");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }
}
