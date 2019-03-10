package ml.pho3.tp3;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import ml.pho3.tp3.adapter.WeatherAdapter;
import ml.pho3.tp3.data.City;
import ml.pho3.tp3.data.WeatherDbHelper;
import ml.pho3.tp3.webservice.Updater;
import ml.pho3.utils.Utils;

public class MainActivity extends Activity {

    private WeatherDbHelper wd;
    private WeatherAdapter wa;
    private Menu menu;

    private ListView listview;

    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wd = new WeatherDbHelper(this);
        Log.e("onCreate@WeatherDbHelpe", "Populate");
        if(!wd.isCreated()) wd.populate();

        Cursor c = wd.fetchAllCities();

        listview = (ListView) findViewById(R.id.listView);

        wa = new WeatherAdapter(this, c);

        listview.setAdapter(wa);
        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listview.setMultiChoiceModeListener(new ModeCallback());
        listview.setItemsCanFocus(false);

        //registerForContextMenu(listview);

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

        initSwipeRefresh();

        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
    }

    private void deleteKey(int k) {
        Log.i("Item",""+k);
        if(k<wa.getCount()) {
            Cursor c = (Cursor) wa.getItem(k);
            City city = wd.cursorToCity(c);
            if(city != null && c != null) {
                wd.deleteCity(c);
            }
        }

    }

    private class ModeCallback implements ListView.MultiChoiceModeListener {

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.delete, menu);
            mode.setTitle("1");
            mode.setSubtitle(null);
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:

                    SparseBooleanArray checkedItems = listview.getCheckedItemPositions();

                    for(int i=checkedItems.size()-1 ;i>= 0;i--){

                        if(checkedItems.valueAt(i)){
                            deleteKey(checkedItems.keyAt(i));
                        }
                    }

                    updateList();

                    mode.finish();
                    break;
                default:
                    break;
            }
            return true;
        }

        public void onDestroyActionMode(ActionMode mode) {
        }

        public void onItemCheckedStateChanged(ActionMode mode,
                                              int position, long id, boolean checked) {
            final int checkedCount = listview.getCheckedItemCount();
            switch (checkedCount) {
                case 0:
                    mode.setSubtitle(null);
                    break;
                case 1:
                    mode.setTitle("1");
                    break;
                default:
                    mode.setTitle("" + checkedCount);
                    break;
            }
        }
    }

    private void initSwipeRefresh() {
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        int pos = getResources().getDimensionPixelOffset(R.dimen.swipeRefreshSize);
        swipeRefresh.setDistanceToTriggerSync(Callback.DEFAULT_SWIPE_ANIMATION_DURATION);
        swipeRefresh.setProgressViewOffset(true, pos, pos+1);
        swipeRefresh.setNestedScrollingEnabled(true);
        swipeRefresh.requestDisallowInterceptTouchEvent(true);
        swipeRefresh.setColorSchemeResources(R.color.swipeColor1, R.color.swipeColor2);

        swipeRefresh.setOnRefreshListener(new OnRefreshListener() {
            public void onRefresh() {
                Log.d("Main","Refresh Action");
                if(Utils.isNetworkConnected(getApplicationContext())) {
                    Log.d("Main","Haz internet");
                    refreshCityList();
                    return;
                }
                swipeRefresh.setRefreshing(false);
                Toast toast = Toast.makeText(getApplicationContext(), R.string.noInternet, Toast.LENGTH_SHORT);
                toast.show();
            }
        });

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
        if(Utils.isNetworkConnected(getApplicationContext())) {
            Intent i = new Intent(getApplicationContext(), NewCityActivity.class);
            startActivityForResult(i, 2);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), R.string.noInternet, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void updateList() {
        Cursor c = wd.fetchAllCities();
        wa.changeCursor(c);
        wa.notifyDataSetChanged();
    }

    private void refreshCityList() {
        new UpdateCitiesAsync().execute("");
    }

    private class UpdateCitiesAsync extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            Cursor c = wd.fetchAllCities();
            Updater u = new Updater(getApplicationContext());

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
            updateList();
            swipeRefresh.setRefreshing(false);
        }

    }

}
