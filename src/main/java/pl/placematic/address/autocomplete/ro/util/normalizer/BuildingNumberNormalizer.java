package pl.placematic.address.autocomplete.ro.util.normalizer;

public class BuildingNumberNormalizer implements NormalizerInterface {

    @Override
    public String normalize(String string) {
        string = string.replaceAll("[,.]", " ");
        string = string.replaceAll("\\s+", " ");
        return string.trim().toUpperCase();
    }
}
