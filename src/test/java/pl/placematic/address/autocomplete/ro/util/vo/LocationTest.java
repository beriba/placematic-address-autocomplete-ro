package pl.placematic.address.autocomplete.ro.util.vo;

import org.junit.Test;

import static org.junit.Assert.*;

public class LocationTest {

    @Test
    public void givenStringLatLon_thenCreateLocation() {
        String lat = "52.0";
        String lon = "16.0";

        Location location = new Location(lat, lon);

        assertEquals(52.0, location.getLatitude(), 0);
        assertEquals(16.0, location.getLongitude(), 0);
    }

    @Test
    public void givenDoublePrimitiveLatLon_thenCreateLocation() {
        double lat = 52.0;
        double lon = 16.0;

        Location location = new Location(lat, lon);

        assertEquals(52.0, location.getLatitude(), 0);
        assertEquals(16.0, location.getLongitude(), 0);
    }

    @SuppressWarnings("UnnecessaryBoxing")
    @Test
    public void givenDoubleObjectLatLon_thenCreateLocation() {
        Double lat = new Double(52.0);
        Double lon = new Double(16.0);

        Location location = new Location(lat, lon);

        assertEquals(52.0, location.getLatitude(), 0);
        assertEquals(16.0, location.getLongitude(), 0);
    }
}
