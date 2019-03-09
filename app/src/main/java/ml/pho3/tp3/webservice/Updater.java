package ml.pho3.tp3.webservice;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import ml.pho3.tp3.R;
import ml.pho3.tp3.data.City;
import ml.pho3.tp3.data.WeatherDbHelper;

/**
 * Created by HP on 09/03/2019.
 */

public class Updater {

    private Context c;
    private boolean hasFailed = false;
    String err = "";

    public Updater(Context c) {
        this.c = c;
    }

    public City updateCity(City city) {

        Log.d("",city.toString());
        String name = city.getName();
        String country = city.getCountry();
        URL url;
        try {
            url = WebServiceUrl.build(name, country);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e("async", "Bad URL (city / country ?)");
            return null;
        }

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            err = c.getString(R.string.conFail);

            Log.e("async", "No internet");

            hasFailed = true;

            e.printStackTrace();
        }

        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                /*java.util.Scanner s = new java.util.Scanner(in).useDelimiter("\\A");
                if (s.hasNext()) Log.e("out : ", s.next());
                else Log.w("out : ", "NULL");*/

            try {
                JSONResponseHandler jr = new JSONResponseHandler(city);
                jr.readJsonStream(in);
            } catch (Exception e) {
                err = c.getString(R.string.conIOError);
                e.printStackTrace();
                hasFailed = true;
                return null;
            }


        } catch (UnknownHostException e) {
            err = c.getString(R.string.noInternet);

            Log.e("async", "No internet");

            hasFailed = true;
            return null;
        } catch (IOException e) {
            err = c.getString(R.string.conIOError);

            Log.e("async", "No internet");

            hasFailed = true;

            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }


        return city;
    }
}
