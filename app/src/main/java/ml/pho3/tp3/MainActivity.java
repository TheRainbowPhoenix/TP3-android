package ml.pho3.tp3;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import ml.pho3.tp3.adapter.WeatherAdapter;
import ml.pho3.tp3.data.City;
import ml.pho3.tp3.data.WeatherDbHelper;

public class MainActivity extends Activity {

    private WeatherDbHelper wd;
    private WeatherAdapter wa;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wd = new WeatherDbHelper(this);
        Log.e("onCreate@WeatherDbHelpe", "Populate");
        if(!wd.isCreated()) wd.populate();

        Cursor c = wd.fetchAllCities();

        final ListView listview = (ListView) findViewById(R.id.listView);

        wa = new WeatherAdapter(this, c);

        listview.setAdapter(wa);
        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listview.setItemChecked(2, true);

        registerForContextMenu(listview);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                Cursor item = (Cursor) parent.getItemAtPosition(position);
                City city = wd.cursorToCity(item);
                Intent i = new Intent(getApplicationContext(), CityActivity.class);

                i.putExtra("edit", 1);
                i.putExtra(City.TAG, city);

                Log.e("city_name","> "+city.getName());

                startActivityForResult(i, 1);

            }
        });

        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if(resultCode==Activity.RESULT_OK) {
                    Log.w("result", "> city");
                }
                break;
            case 2:
                if(resultCode==Activity.RESULT_OK) {
                    Log.w("result", "> new");
                }
                break;
        }
        updateList();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.addbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_add:
                createNew();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void createNew() {
        Intent i = new Intent(getApplicationContext(), NewCityActivity.class);
        startActivityForResult(i, 2);
    }

    private void updateList() {
        Cursor c = wd.fetchAllCities();
        wa.changeCursor(c);
        wa.notifyDataSetChanged();
    }

}
