package pl.placematic.address.autocomplete.ro.util.vo;

import lombok.Getter;

@Getter
public class Location {
    private double latitude;
    private double longitude;

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location(String latitude, String longitude) {
        this.latitude = Double.parseDouble(latitude);
        this.longitude = Double.parseDouble(longitude);
    }
}
