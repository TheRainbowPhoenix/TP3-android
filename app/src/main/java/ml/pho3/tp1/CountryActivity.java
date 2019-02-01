package ml.pho3.tp1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import 	android.content.Context;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

public class CountryActivity extends Activity {

    int position = 0;
    String name;
    Country c;

    private Menu menu;

    int onStartCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* anim */
        onStartCount = 1;
        if (savedInstanceState == null) {
            this.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
        } else {
            onStartCount = 2;
        }

        /* layout fill */

        setContentView(R.layout.activity_country);

        Intent i = getIntent();
        position = i.getExtras().getInt("Position");

        Bundle bundle = i.getExtras();

        name = bundle.getString("Name");
        c = (Country) bundle.getSerializable("Country");


        //fianl CustomAdapter adapter = new CustomAdapter(this);
        final ImageView im = (ImageView) findViewById(R.id.countryFlag);
        final TextView cname = (TextView) findViewById(R.id.countryName);
        final EditText capital = (EditText) findViewById(R.id.captialEdit);
        final EditText lang = (EditText) findViewById(R.id.langEdit);
        final EditText currency = (EditText) findViewById(R.id.currencyEdit);
        final EditText population = (EditText) findViewById(R.id.populationEdit);
        final EditText size = (EditText) findViewById(R.id.sizeEdit);

        cname.setText(name);
        capital.setText(c.getmCapital());
        lang.setText(c.getmLanguage());
        currency.setText(c.getmCurrency());
        population.setText(""+c.getmPopulation());
        size.setText(""+c.getmArea());
        //im.setImageResource();
        Context context = im.getContext();
        int id = context.getResources().getIdentifier(c.getmImgFile(), "drawable", context.getPackageName());
        im.setImageResource(id);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (onStartCount > 1) {
            this.overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
        } else {
            onStartCount++;
        }
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
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.actionbar, menu);
        return true;
    }

    /*@Overrides
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/


}
