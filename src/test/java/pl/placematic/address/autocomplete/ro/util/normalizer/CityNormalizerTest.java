package pl.placematic.address.autocomplete.ro.util.normalizer;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.placematic.address.autocomplete.ro.util.DataProviderRunnerWithSpring;

import static org.junit.Assert.*;

@RunWith(DataProviderRunnerWithSpring.class)
public class CityNormalizerTest {

    private CityNormalizer cityNormalizer;

    @Before
    public void setUp() throws Exception {
        cityNormalizer = new CityNormalizer();
    }

    @Test
    @UseDataProvider("dataInput")
    public void sth(String input, String expected) {
        assertEquals(expected, cityNormalizer.normalize(input));
    }

    @DataProvider
    public static Object[][] dataInput() {
        return new Object[][]{
                {"sth", "STH"},
                {"Łódź", "ŁÓDŹ"},
                {"Tarnowo Podgórne", "TARNOWO PODGÓRNE"},
                {"Bielsko-Biała", "BIELSKO BIAŁA"},
                {"|        |", "| |"}, //multiple spaces
                {"     ", ""}, //multiple spaces
                {" ", ""},
                {"", ""},
                {"coś/coś", "COŚ COŚ"},
                {"coś,coś", "COŚ COŚ"},
                {"coś.coś", "COŚ COŚ"},
                {"coś-coś", "COŚ COŚ"},
                {"coś-", "COŚ"},
                {"coś,", "COŚ"},
                {"coś.", "COŚ"},
                {"coś/", "COŚ"},
        };
    }
}
