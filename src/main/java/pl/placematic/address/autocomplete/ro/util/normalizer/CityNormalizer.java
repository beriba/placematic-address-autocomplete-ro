package pl.placematic.address.autocomplete.ro.util.normalizer;

public class CityNormalizer implements NormalizerInterface {

    @Override
    public String normalize(String string) {
        string = string.toUpperCase();
        string = string.replaceAll("(\\\\-)|[,./-]", " ");
        string = string.replaceAll("\\s+", " ");
        return string.trim();
    }
}
