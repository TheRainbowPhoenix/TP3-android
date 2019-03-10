package ml.pho3.tp3.data;

/**
 * Created by HP on 09/03/2019.
 */

public class SearchCity {
    private String name;
    private String country;

    public SearchCity() {}

    public SearchCity(String name, String country) {
        this.name = name;
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getFullName() { return name+"("+country+")"; }

    public void setName(String name) {
        this.name = name;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
