package ml.pho3.tp3;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ml.pho3.tp3.data.City;
import ml.pho3.tp3.data.WeatherDbHelper;
import ml.pho3.tp3.service.UpdaterService;
import ml.pho3.tp3.webservice.Updater;
import ml.pho3.utils.Utils;

public class NewCityActivity extends Activity {

    private EditText textName, textCountry;

    private Menu menu;

    private City city;
    private boolean isValid = false;

    AlertDialog alertDialog;

    WeatherDbHelper wd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_city);

        wd = new WeatherDbHelper(this);

        textName = (EditText) findViewById(R.id.editNewName);
        textCountry = (EditText) findViewById(R.id.editNewCountry);

        textName.setText("");
        textCountry.setText("");

        //final Button but = (Button) findViewById(R.id.button);

        /*but.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                //NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_save:
                commit();
                //onBackPressed();
                return true;
            case R.id.action_search:
                Intent i= new Intent(this, UpdaterService.class);
                i.putExtra("KEY1", "Value to be used by the service");
                this.startService(i);

                //Utils.scheduleJob(this);
                /*Intent i = new Intent(getApplicationContext(), SearchActivity.class);
                i.putExtra("cityName", ""+textName.getText());
                i.putExtra("cityCountry", ""+textCountry.getText());
                startActivityForResult(i, 3);*/
        }
        return super.onOptionsItemSelected(item);
    }

    public void commit() {
        textName = (EditText) findViewById(R.id.editNewName);
        textCountry = (EditText) findViewById(R.id.editNewCountry);

        String name = ""+textName.getText();
        String country = ""+textCountry.getText();

        Intent i = new Intent(getApplicationContext(), NewCityActivity.class);

        city = new City(name, country);

        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(R.string.ParseError);
        alertDialog.setMessage(getString(R.string.cityNotFound));

        new TestCityAsync().execute("");

        //TODO: testCity !

        if(isValid) {
            wd.addCity(city);

            setResult(Activity.RESULT_OK, i);
            finish();
        } else {
            //alertDialog.show();
        }

    }

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    alertDialog.show();
                    return;
                default:
                    return;
            }
            // This is where you do your work in the UI thread.
            // Your worker tells you in the message what to do.
        }
    };

    private void dialogCallback() {
        Message message = mHandler.obtainMessage(1, "");
        message.sendToTarget();
    }

    private class TestCityAsync extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            Updater u = new Updater(getApplicationContext());

            City testCity = u.updateCity(city);
            if(testCity  != null) {
                Log.d("City",""+testCity.getFullName());
                isValid = true;

                wd.addCity(city);

                Intent i = new Intent(getApplicationContext(), NewCityActivity.class);

                setResult(Activity.RESULT_OK, i);
                finish();
            }
            else {
                dialogCallback();
                Log.e("NULL","NUL CITY");
            }

            return "Done";
        }

    }


}
