package pl.placematic.address.autocomplete.ro.response;

import org.junit.Test;

import static org.junit.Assert.*;

public class ZipTest {
    @Test
    public void givenZipString_thenCreateZip() {
        String zip = "00-999";

        Zip zipObject = new Zip(zip);

        assertEquals("00-999", zipObject.getZip());
        assertEquals("Zip(zip=00-999, approximated=false)", zipObject.toString());
    }
}
