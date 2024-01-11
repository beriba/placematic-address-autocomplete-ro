package pl.placematic.address.autocomplete.ro.response;

import lombok.Data;
import pl.placematic.address.autocomplete.ro.util.vo.Location;

@Data
public class City {
    private String state;
    private String municipality;
    private String city;
    private String cityDistricted;
    private Location location;
}
