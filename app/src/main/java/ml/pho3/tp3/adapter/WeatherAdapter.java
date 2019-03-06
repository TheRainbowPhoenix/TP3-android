package ml.pho3.tp3.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ml.pho3.tp3.R;
import ml.pho3.tp3.data.WeatherDbHelper;

public class WeatherAdapter extends CursorAdapter {
    public WeatherAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.row, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView Name = (TextView) view.findViewById(R.id.cName);
        TextView Country = (TextView) view.findViewById(R.id.cCountry);
        TextView Temperature = (TextView) view.findViewById(R.id.temperature);
        ImageView bookCover = (ImageView) view.findViewById(R.id.imageViewRow);

        String name = cursor.getString(cursor.getColumnIndexOrThrow(WeatherDbHelper.COLUMN_CITY_NAME));
        String count = cursor.getString(cursor.getColumnIndexOrThrow(WeatherDbHelper.COLUMN_COUNTRY));
        String temp = cursor.getString(cursor.getColumnIndexOrThrow(WeatherDbHelper.COLUMN_TEMPERATURE));
        String ico = cursor.getString(cursor.getColumnIndexOrThrow(WeatherDbHelper.COLUMN_ICON));

        if(ico == null) ico = "50d";

        ico = IconDefines.getIconName(ico);

        int id = context.getResources().getIdentifier(ico, "drawable", context.getPackageName());

        bookCover.setImageResource(id);

        Name.setText(name);
        Country.setText(count);
        Temperature.setText(temp);
    }

}
