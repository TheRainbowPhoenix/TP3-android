package ml.pho3.tp3;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ml.pho3.tp3.data.City;
import ml.pho3.tp3.data.WeatherDbHelper;

public class NewCityActivity extends Activity {

    private EditText textName, textCountry;

    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_city);

        textName = (EditText) findViewById(R.id.editNewName);
        textCountry = (EditText) findViewById(R.id.editNewCountry);

        textName.setText("");
        textCountry.setText("");

        final Button but = (Button) findViewById(R.id.button);

        but.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO
            }
        });
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
        }
        return super.onOptionsItemSelected(item);
    }

    public void commit() {
        WeatherDbHelper wd = new WeatherDbHelper(this);

        textName = (EditText) findViewById(R.id.editNewName);
        textCountry = (EditText) findViewById(R.id.editNewCountry);

        String name = ""+textName.getText();
        String country = ""+textCountry.getText();

        Intent i = new Intent(getApplicationContext(), NewCityActivity.class);

        City city = new City(name, country);
        wd.addCity(city);

        setResult(Activity.RESULT_OK, i);
        finish();
    }


}
