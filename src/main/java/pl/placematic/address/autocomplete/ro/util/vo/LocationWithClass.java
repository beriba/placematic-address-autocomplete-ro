package pl.placematic.address.autocomplete.ro.util.vo;

import lombok.Getter;

@Getter
public class LocationWithClass extends Location {
    private Integer xyClass;

    public LocationWithClass(double latitude, double longitude, Integer xyClass) {
        super(latitude, longitude);
        this.xyClass = xyClass;
    }

    public LocationWithClass(String latitude, String longitude, Integer xyClass) {
        super(latitude, longitude);
        this.xyClass = xyClass;
    }
}
