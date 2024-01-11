package pl.placematic.address.autocomplete.ro.response;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StreetTest {
    @Test
    public void givenStreetParams_thenCreateStreet() {
        Street street = new Street("state", "municipality", "city", "cityDistricted", "prefix", "street", "streetTeryt");

        assertEquals("state", street.getState());
        assertEquals("municipality", street.getMunicipality());
        assertEquals("city", street.getCity());
        assertEquals("cityDistricted", street.getCityDistricted());
        assertEquals("prefix", street.getPrefix());
        assertEquals("street", street.getStreet());
        assertEquals("Street(state=state, municipality=municipality, city=city, cityDistricted=cityDistricted, prefix=prefix, street=street, streetTeryt=streetTeryt)", street.toString());
    }
}
