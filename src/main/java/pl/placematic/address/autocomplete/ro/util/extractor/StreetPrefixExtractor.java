package pl.placematic.address.autocomplete.ro.util.extractor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StreetPrefixExtractor {
    public String extract(String query) {
        Pattern pattern = Pattern.compile("(?i)\\b(bd|b-dul|bdul|boulevard|bulevardul|Șos|Șoseaua|sos|soseaua|str|strada|calea|beco|aleea|drumul|piata|Piața)\\b");
        Matcher matcher = pattern.matcher(query);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
}
