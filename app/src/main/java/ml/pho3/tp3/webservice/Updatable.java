package ml.pho3.tp3.webservice;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
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

public class Updatable extends AsyncTask<String, Void, String> {

    AlertDialog alertDialog;
    private Context c;
    private boolean hasFailed = false;
    WeatherDbHelper wd;
    City city;

    public Updatable(Context c) {
        this.c = c;
        wd = new WeatherDbHelper(c.getApplicationContext());
    }
    public Updatable(Context c, @NonNull City ct) {
        this.c = c;
        wd = new WeatherDbHelper(c.getApplicationContext());
        city = new City(ct);
    }

    public void setCity(@NonNull City city) {
        this.city = city;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        alertDialog = new AlertDialog.Builder(c).create();
    }

    @Override
    protected String doInBackground(String... strings) {
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
            alertDialog.setTitle(R.string.ComError);
            alertDialog.setMessage(c.getString(R.string.conFail));

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
                alertDialog.setTitle(c.getString(R.string.ComError));
                alertDialog.setMessage(c.getString(R.string.conIOError));
                e.printStackTrace();
                hasFailed = true;
                return null;
            }


        } catch (UnknownHostException e) {

            alertDialog.setTitle(c.getString(R.string.ComError));
            alertDialog.setMessage(c.getString(R.string.noInternet));

            Log.e("async", "No internet");

            hasFailed = true;
            return null;
        } catch (IOException e) {

            alertDialog.setTitle(R.string.ComError);
            alertDialog.setMessage(c.getString(R.string.conIOError));

            Log.e("async", "No internet");

            hasFailed = true;

            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }

        return "Async :D";
    }

    public String onSuccess(String result) {
        return result;
    };

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(hasFailed) alertDialog.show();
        else {
            wd.updateCity(city);
            onSuccess(result);

            //updateView();
        }
    }
}