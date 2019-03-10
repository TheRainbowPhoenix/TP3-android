package ml.pho3.tp3;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import ml.pho3.tp3.webservice.Updater;
import ml.pho3.tp3.webservice.WebServiceUrl;
import ml.pho3.tp3.webservice.Updatable;

public class CityActivity extends Activity {

    private static final String TAG = CityActivity.class.getSimpleName();
    private TextView textCityName, textCountry, textTemperature, textHumdity, textWind, textCloudiness, textLastUpdate, textDescription, textPressure;
    private ImageView imageWeatherCondition;

    private WeatherDbHelper wd;

    @NonNull
    private City city;
    private Menu menu;
    private _Updatable _update;

    private Animation loadAnim;
    private ImageButton but;
    private boolean loadComplete = true;
    private boolean canLoop = true;

    private boolean nightMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wd = new WeatherDbHelper(this);

        city = (City) getIntent().getParcelableExtra(City.TAG);

        Log.e("city_name","> "+city.getName());

        if(city.getNight() != 0) {
            nightMode = true;
            setTheme(R.style.Theme_Weather_Detail_Night);
            //getWindow().setNavigationBarColor(getResources().getColor(R.color.nightColor1, R.style.Theme_Weather_Detail_Night));
        }

        setContentView(R.layout.activity_city);

        if(nightMode) {
            findViewById(R.id.cont).setBackgroundResource(R.drawable.gradient_bg_night);
            getTheme().applyStyle(R.style.Theme_Weather_Detail_Night, true);
            findViewById(R.id.bot).setBackgroundColor(getResources().getColor(R.color.nightColor1));
        }

        /*long etim = System.currentTimeMillis() / 1000;
        //Log.w("epoch", ""+etim);
        long rise = city.getSunrise();
        long set = city.getSunset();
        Log.w("night", ""+city.getNight());
        //Log.w("epoch", "set>etim : "+(set>etim)+"  rise "+city.getSunrise() +  ",  set "+city.getSunset()+",  now "+etim);*/

        _update = new _Updatable(this);

        loadAnim = AnimationUtils.loadAnimation(CityActivity.this.getApplicationContext(), R.anim.rotate);

        loadAnim.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation arg0) {
                loadComplete = false;
                canLoop = false;
            }
            @Override
            public void onAnimationRepeat(Animation arg0) {
                if(loadComplete) {
                    but.clearAnimation();
                    canLoop = true;
                }
            }
            @Override
            public void onAnimationEnd(Animation arg0) {
                if(!loadComplete) but.startAnimation(loadAnim);
                else canLoop = true;
            }
        });

        textCityName = (TextView) findViewById(R.id.nameCity);
        textCountry = (TextView) findViewById(R.id.country);
        textTemperature = (TextView) findViewById(R.id.editTemperature);
        textHumdity = (TextView) findViewById(R.id.editHumidity);
        textWind = (TextView) findViewById(R.id.editWind);
        textCloudiness = (TextView) findViewById(R.id.editCloudiness);
        textPressure = (TextView) findViewById(R.id.editPressure);
        textLastUpdate = (TextView) findViewById(R.id.editLastUpdate);
        textDescription = (TextView) findViewById(R.id.editDescription);

        imageWeatherCondition = (ImageView) findViewById(R.id.imageView);

        if(city.getTemperature() == null ||city.getTemperature().length() <1) doReload();

        updateView();

        but = (ImageButton) findViewById(R.id.refreshButton);

        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                initReload();
            }

        });

        FrameLayout big = findViewById(R.id.big);
        Animation aniScale = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.scale_fade_in);
        aniScale.setStartOffset(250);
        big.startAnimation(aniScale);

        LinearLayout desc = findViewById(R.id.desc);
        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fadein);
        aniFade.setStartOffset(400);
        desc.startAnimation(aniFade);

        LinearLayout moar = findViewById(R.id.moar);
        Animation aniFader = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fadein);
        aniFader.setStartOffset(500);
        moar.startAnimation(aniFader);
    }

    private void initReload() {
        if(loadComplete && canLoop) {
            but.startAnimation(loadAnim);
            doReload();
        }
    }

    private void endReload() {
        loadComplete = true;
        //but.clearAnimation();
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
        textPressure.setText(fillWith(city.getPressure())+" mb");
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
                initReload();
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
            Updater u = new Updater(getApplicationContext());

            City c = u.updateCity(city);
            if(c != null) {
                city = c;
                wd.updateCity(city);
            }
            else {
                Log.e("NULL","NUL CITY");
            }

            return "Done";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(hasFailed) alertDialog.show();
            else {
                endReload();
                wd.updateCity(city);
                updateView();
            }
        }
    }

   
}
