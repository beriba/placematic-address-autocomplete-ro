package pl.placematic.address.autocomplete.ro.util.normalizer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class StreetPrefixNormalizer implements NormalizerInterface {
    @Override
    public String normalize(String string) {
        HashMap<String, String> map = new HashMap<>();
        map.put("ulica", "ul");
        map.put("aleja", "al");
        map.put("plac", "pl");
        map.put("osiedle", "os");
        string = string.replaceAll("\\.", "").toLowerCase().trim();
        final List<String> tmp = Arrays.asList(string);
        map.forEach((key, value) -> tmp.set(0, tmp.get(0).replaceAll("\\b" + key + "\\b", value)));
        return tmp.get(0);
    }

    public String dropPrefix(String string) {
        String res = this.normalize(string)
                .replaceAll("\\b(ul|al|pl|os)\\b", " ")
                .replaceAll("\\s+", " ")
                .trim();
        return res;
    }
}
