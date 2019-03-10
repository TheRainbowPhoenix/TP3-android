package ml.pho3.tp3.webservice;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

public class WebServiceUrl {

    // TODO: complete with your own API_KEY
    private static final String API_KEY = "53cf5bf7a3a04b3889498725e3db8ae6";

    // https://samples.openweathermap.org/data/2.5/weather?q=London,uk&appid=53cf5bf7a3a04b3889498725e3db8ae6
    //private static final String HOST = "samples.openweathermap.org";
    private static final String HOST = "api.openweathermap.org";
    private static final String PATH_1 = "data";
    private static final String PATH_2 = "2.5";
    private static final String PATH_3 = "weather";
    private static final String URL_PARAM1 = "q";
    private static final String URL_PARAM2 = "appid";

    //Find cities
    //https://samples.openweathermap.org/data/2.5/find?q= {city_name} &appid=53cf5bf7a3a04b3889498725e3db8ae6

    public static URL build(String cityName, String countryName) throws MalformedURLException {

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority(HOST)
                .appendPath(PATH_1)
                .appendPath(PATH_2)
                .appendPath(PATH_3)
                .appendQueryParameter(URL_PARAM1, cityName + "," + countryName)
                .appendQueryParameter(URL_PARAM2, API_KEY);
        URL url = new URL(builder.build().toString());
        return url;
    }

}
