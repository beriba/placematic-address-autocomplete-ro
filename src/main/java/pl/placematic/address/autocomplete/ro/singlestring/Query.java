package pl.placematic.address.autocomplete.ro.singlestring;

import lombok.Getter;
import pl.placematic.address.autocomplete.ro.util.extractor.BuildingNumberExtractor;
import pl.placematic.address.autocomplete.ro.util.extractor.StreetPrefixExtractor;
import pl.placematic.address.autocomplete.ro.util.normalizer.StreetPrefixNormalizer;
import pl.placematic.address.autocomplete.ro.util.vo.selector.SelectorField;
import pl.placematic.address.autocomplete.ro.util.vo.sort.SortField;

@Getter
public class Query {

    // because Autowire doesn't work here, I'm not sure why
    private BuildingNumberExtractor buildingNumberExtractor = new BuildingNumberExtractor();
    private StreetPrefixExtractor streetPrefixExtractor = new StreetPrefixExtractor();

    private String rawQuery;
    private SortField sortBy;
    private final SelectorField selectBy;

    // extracted
    private String zip;
    private String streetPrefix;
    private String buildingNumber;
    private String buildingNumberFull;

    public Query(String rawQuery, SortField sortBy, SelectorField selectBy) {
        this.rawQuery = rawQuery;
        this.sortBy = sortBy;
        this.selectBy = selectBy;

        this.build();
    }

    private void build() {
        buildingNumber = buildingNumberExtractor.extractAsString(this.rawQuery);
        if (buildingNumber != null) {
            buildingNumberFull = buildingNumberExtractor.extractFullWithLetters(this.rawQuery);
        }

        streetPrefix = this.streetPrefixExtractor.extract(this.rawQuery);

//        zip = extractZip(this.rawQuery);
    }

//    private String extractZip(String query) {
//        Pattern pattern = Pattern.compile("(?i)\\b(\\d{2}-\\d{0,3})\\b");
//        Matcher matcher = pattern.matcher(query);
//        if (matcher.find()) {
//            return matcher.group();
//        }
//        return null;
//    }

    public String rawQueryWithoutHouseNumber() {
        if (buildingNumberFull != null) {
            return this.rawQuery.replace(buildingNumberFull, buildingNumber);
        }

        return this.rawQuery;
    }

    public String getQuery() {
        String query = this.rawQueryWithoutHouseNumber();
        if (streetPrefix != null) {
            query = new StreetPrefixNormalizer().dropPrefix(query);
        }
        if (zip != null) {
            query = query.replaceAll("(?i)" + zip + "", "");
        }

        return query;
    }

    public String getStreetPrefixNormalized() {
        return new StreetPrefixNormalizer().normalize(this.getStreetPrefix());
    }

    public String getZip() {
        return zip;
    }

    public boolean hasBuildingNumber() {
        if (this.buildingNumber != null) {
            return true;
        }

        return false;
    }
}
