package ml.pho3.tp3;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ml.pho3.tp3.data.SearchCity;

public class SearchActivity extends Activity implements SearchView.OnQueryTextListener,
        SearchView.OnCloseListener, Button.OnClickListener {

    private SearchView mSearchView;
    //private Button mOpenButton;
    //private Button mCloseButton;
    //private TextView mStatusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        setContentView(R.layout.searchview_actionbar);

        //mStatusView = (TextView) findViewById(R.id.status_text);
        //mOpenButton = (Button) findViewById(R.id.open_button);
       // mCloseButton = (Button) findViewById(R.id.close_button);
        //mOpenButton.setOnClickListener(this);
        //mCloseButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        mSearchView = (SearchView) menu.findItem(R.id.action_search_item).getActionView();
        setupSearchView();

        return true;
    }

    private void setupSearchView() {

        mSearchView.setIconifiedByDefault(false);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchManager != null) {
            List<SearchableInfo> searchables = searchManager.getSearchablesInGlobalSearch();

            // Try to use the "applications" global search provider
            SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
            for (SearchableInfo inf : searchables) {
                if (inf.getSuggestAuthority() != null
                        && inf.getSuggestAuthority().startsWith("applications")) {
                    info = inf;
                }
            }
            mSearchView.setSearchableInfo(info);
        }

        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnCloseListener(this);
    }

    public boolean onQueryTextChange(String newText) {
        //mStatusView.setText("Query = " + newText);
        return false;
    }

    public boolean onQueryTextSubmit(String query) {
        //TODO: Search !
        //mStatusView.setText("Query = " + query + " : submitted");
        return false;
    }

    public boolean onClose() {
        //mStatusView.setText("Closed!");
        return false;
    }

    public void onClick(View view) {
        /*if (view == mCloseButton) {
            mSearchView.setIconified(true);
        } else if (view == mOpenButton) {
            mSearchView.setIconified(false);
        }*/
    }
}
