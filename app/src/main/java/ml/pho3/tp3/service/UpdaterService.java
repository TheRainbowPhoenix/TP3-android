package ml.pho3.tp3.service;


import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import ml.pho3.tp3.data.City;
import ml.pho3.tp3.data.WeatherDbHelper;
import ml.pho3.tp3.webservice.Updater;
import ml.pho3.utils.Utils;

public class UpdaterService extends IntentService {

    private WeatherDbHelper wd;

    public UpdaterService() {
        this("UpdaterService");
    }

    public UpdaterService(String name) {
        super(name);
        initDb();
    }

    private void initDb() {
        wd = new WeatherDbHelper(this);
    }

    private void updateCities() {
        if(Utils.isNetworkConnected(getApplicationContext())) {
            new UpdateCitiesAsync().execute("");
        } else {
            Log.w("service", "No internet !");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful

        Log.w("service", "StartCommand");

        if(serviceLock.isAvailable()) {
            serviceLock.lock();
            if(wd != null) updateCities();
            else Log.e("service", "db not inited");
        } else {
            Log.w("service", "already running / busy");
        }

        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.w("service", "Bind");
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.w("service", "Bind");
    }

    private class UpdateCitiesAsync extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            Cursor c = wd.fetchAllCities();
            Updater u = new Updater(getApplicationContext());

            Log.w("service", "Update begin");

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                City city = u.updateCity(wd.cursorToCity(c));
                if(city != null) {
                    wd.updateCity(city);
                }
                else Log.e("NULL","NUL CITY");
            }

            return "Done";
        }

        @Override
        protected void onPostExecute(String result) {
            serviceLock.free();
        }


    }
}
