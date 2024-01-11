package pl.placematic.address.autocomplete.ro.util;

import org.junit.Test;
import pl.placematic.address.autocomplete.ro.util.extractor.BuildingNumberExtractor;

import static org.junit.Assert.assertEquals;

public class BuildingNumberExtractorTest {
    @Test
    public void givenStringWithHouseNumber_thenReturnHouseNumber() {
        BuildingNumberExtractor extractor = new BuildingNumberExtractor();

        assertEquals("7", extractor.extractFullWithLetters("taśmowa 7"));
        assertEquals("7", extractor.extractFullWithLetters("7"));
        assertEquals("7", extractor.extractFullWithLetters("sth 7 sth"));
        assertEquals("7", extractor.extractFullWithLetters("sth 7 sth 8"));
        assertEquals("16/22", extractor.extractFullWithLetters("warszawa krucza 16/22"));
    }
}
