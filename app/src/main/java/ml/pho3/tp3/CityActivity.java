package ml.pho3.tp3;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import android.os.AsyncTask;

import ml.pho3.tp3.data.City;
import ml.pho3.tp3.data.WeatherDbHelper;
import ml.pho3.tp3.webservice.JSONResponseHandler;
import ml.pho3.tp3.webservice.WebServiceUrl;
import ml.pho3.tp3.webservice.Updatable;

public class CityActivity extends Activity {

    private static final String TAG = CityActivity.class.getSimpleName();
    private TextView textCityName, textCountry, textTemperature, textHumdity, textWind, textCloudiness, textLastUpdate, textDescription;
    private ImageView imageWeatherCondition;

    private WeatherDbHelper wd;

    @NonNull
    private City city;
    private Menu menu;
    private _Updatable _update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        wd = new WeatherDbHelper(this);

        city = (City) getIntent().getParcelableExtra(City.TAG);

        Log.e("city_name","> "+city.getName());

        _update = new _Updatable(this);

        textCityName = (TextView) findViewById(R.id.nameCity);
        textCountry = (TextView) findViewById(R.id.country);
        textTemperature = (TextView) findViewById(R.id.editTemperature);
        textHumdity = (TextView) findViewById(R.id.editHumidity);
        textWind = (TextView) findViewById(R.id.editWind);
        textCloudiness = (TextView) findViewById(R.id.editCloudiness);
        textLastUpdate = (TextView) findViewById(R.id.editLastUpdate);
        textDescription = (TextView) findViewById(R.id.editDescription);

        imageWeatherCondition = (ImageView) findViewById(R.id.imageView);

        if(city.getTemperature() == null ||city.getTemperature().length() <1) doReload();

        updateView();

        final ImageButton but = (ImageButton) findViewById(R.id.refreshButton);

        but.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                doReload();
            }
        });


    }

    private String fillWith(String value) {
        return (value!=null)?(value):("0");
    }
    private String fillWith(String value, @NonNull String text) {
        return (value!=null)?(value):(text);
    }


    @Override
    public void onBackPressed() {

        Intent i = new Intent(getApplicationContext(), CityActivity.class);

        i.putExtra(City.TAG, city);

        Log.e("city_name","> "+city.getName());

        setResult(Activity.RESULT_OK, i);

        if(_update.getStatus() != AsyncTask.Status.FINISHED) _update.cancel(true);
        //TODO : prepare result for the main activity
        super.onBackPressed();
    }

    private void updateView() {
        textCityName.setText(city.getName());
        textCountry.setText(city.getCountry());
        textTemperature.setText(fillWith(city.getTemperature())+" °C");
        textHumdity.setText(fillWith(city.getHumidity())+" %");
        textWind.setText(fillWith(city.getFullWind()));
        textCloudiness.setText(fillWith(city.getCloudiness())+" %");
        textLastUpdate.setText(fillWith(city.getLastUpdate(),"01/01 00:00"));

        String desc = city.getDescription();
        if(desc != null && desc.length()>1) {
            String output = desc.substring(0, 1).toUpperCase() + desc.substring(1);
            textDescription.setText(output);
        } else {
            textDescription.setText("Empty");
        }

        if (city.getIcon()!=null && !city.getIcon().isEmpty()) {
            Log.d(TAG,"icon=" + city.getIcon());
            imageWeatherCondition.setImageDrawable(getResources().getDrawable(getResources()
                    .getIdentifier("@drawable/"+ city.getIcon(), null, getPackageName())));
            imageWeatherCondition.setContentDescription(city.getDescription());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.refresh, menu);
        return true;
    }

    private void setNavigationBar() {
        //make full transparent statusBar
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            );
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    private void setWindowFlag(final int bits, boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                //NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_reload:
                doReload();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void doReload() {
        Log.d("async", "Reloading");
        if(_update.getStatus() != AsyncTask.Status.RUNNING) {
            if(_update.getStatus() == AsyncTask.Status.FINISHED) {
                Log.d("",city.toString());
                _update = new _Updatable(this);
                /*_update = new Updatable(this, this.city) {
                    @Override
                    public String onSuccess(String result) {
                        updateView();
                        return result;
                    }
                };*/
            }
            _update.execute("");
        }
    }

    private class _Updatable extends AsyncTask<String, Void, String> {

        AlertDialog alertDialog;
        private Context c;
        private boolean hasFailed = false;

        public _Updatable(Context c) {
            this.c = c;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            alertDialog = new AlertDialog.Builder(c).create();
        }

        @Override
        protected String doInBackground(String... strings) {
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
                alertDialog.setMessage(getString(R.string.conFail));

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
                    alertDialog.setTitle(getString(R.string.ComError));
                    alertDialog.setMessage(getString(R.string.conIOError));
                    e.printStackTrace();
                    hasFailed = true;
                    return null;
                }


            } catch (UnknownHostException e) {

                alertDialog.setTitle(getString(R.string.ComError));
                alertDialog.setMessage(getString(R.string.noInternet));

                Log.e("async", "No internet");

                hasFailed = true;
                return null;
            } catch (IOException e) {

                alertDialog.setTitle(R.string.ComError);
                alertDialog.setMessage(getString(R.string.conIOError));

                Log.e("async", "No internet");

                hasFailed = true;

                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }

            return "Async :D";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(hasFailed) alertDialog.show();
            else {
                wd.updateCity(city);
                updateView();
            }
        }
    }

   
}
