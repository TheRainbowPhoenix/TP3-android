package ml.pho3.tp3.adapter;

import java.util.HashMap;

public class IconDefines {
    private static HashMap<String, String> hashMap = init();

    private static HashMap<String, String> init() {
        HashMap<String,String> res = new HashMap<>();
        res.put("01d", "weather_sunny");
        res.put("01n", "weather_night");
        res.put("02d", "weather_partlycloudy");
        res.put("02n", "weather_partlycloudy");
        res.put("03d", "weather_cloudy");
        res.put("03n", "weather_cloudy");
        res.put("04d", "weather_cloudy");
        res.put("04n", "weather_cloudy");
        res.put("09d", "weather_rainy");
        res.put("09n", "weather_rainy");
        res.put("10d", "weather_pouring");
        res.put("10n", "weather_pouring");
        res.put("11d", "weather_lightning");
        res.put("11n", "weather_lightning");
        res.put("13d", "weather_snowy");
        res.put("13n", "weather_snowy");
        res.put("50d", "weather_fog");
        res.put("50n", "weather_fog");
        return res;
    }

    public static String getIconName(String id) {
        return hashMap.get(id);
    }
}
