package ml.pho3.tp1;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CountryList {
    private static HashMap<String, Country> hashMap = init();

    private static HashMap<String, Country> init() {
        HashMap<String,Country> res = new HashMap<>();
        res.put("France", new Country("Paris", "flag_of_france", "français", "euro", 67348000, 640679));
        res.put("Allemagne", new Country("Berlin", "flag_of_germany", "allemand", "euro", 82887000, 357340));
        res.put("Espagne", new Country("Madrid", "flag_of_spain", "espagnol", "euro", 48958159, 505911));
        res.put("Afrique du Sud", new Country("Pretoria", "flag_of_south_africa", "anglais, afrikaans et 9 langues bantoues", "rand", 55653654, 1219912));
        res.put("États-Unis", new Country("Washington", "flag_of_the_united_states", "aucune", "dollar américain", 327167434, 9833517));
        res.put("Japon", new Country("Tokyo", "flag_of_japan", "japonais", "yen", 126168156, 377915));
        return res;
    }

    public static String[] getNameArray() {
        return hashMap.keySet().toArray(new String[hashMap.size()]);
    }

    public static Country getCountry(String name) {
        return hashMap.get(name);
    }

}
