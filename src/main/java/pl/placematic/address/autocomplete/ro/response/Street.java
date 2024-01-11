package pl.placematic.address.autocomplete.ro.response;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class Street implements CityInterface {
    private String state;
    private String municipality;
    private String city;
    private String cityDistricted;
    private String prefix;
    private String street;
    private String streetTeryt;

    public Street(String state, String municipality, String city, String cityDistricted, String prefix, String street, String streetTeryt) {
        this.state = state;
        this.municipality = municipality;
        this.city = city;
        this.cityDistricted = cityDistricted;
        this.prefix = prefix;
        this.street = street;
        this.streetTeryt = streetTeryt;
    }
}
