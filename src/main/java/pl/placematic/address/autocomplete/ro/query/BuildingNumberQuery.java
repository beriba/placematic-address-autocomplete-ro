package pl.placematic.address.autocomplete.ro.query;

import lombok.Getter;
import org.springframework.data.domain.Pageable;
import pl.placematic.address.autocomplete.ro.util.normalizer.BuildingNumberNormalizer;
import pl.placematic.address.autocomplete.ro.util.normalizer.StreetNormalizer;

@Getter
public class BuildingNumberQuery {
    private String buildingNumber;
    private String streetQuery;
    private String streetPrefix;
    private String cityDistrictedQuery;
    private String zipQuery;
    private String cityQuery;
    private String municipalityQuery;
    private String voivodeshipQuery;
    private Pageable pageable;
    private boolean exactMatchOnly = false;
    private boolean streetExactMatchOnly = false;

    public BuildingNumberQuery(
            String buildingNumber,
            String streetQuery,
            String streetPrefix,
            String cityDistrictedQuery,
            String zipQuery,
            String cityQuery,
            String municipalityQuery,
            String voivodeshipQuery,
            Pageable pageable
    ) {
        this.buildingNumber = buildingNumber;
        this.streetQuery = streetQuery;
        this.streetPrefix = streetPrefix;
        this.cityDistrictedQuery = cityDistrictedQuery;
        this.zipQuery = zipQuery;
        this.cityQuery = cityQuery;
        this.municipalityQuery = municipalityQuery;
        this.voivodeshipQuery = voivodeshipQuery;
        this.pageable = pageable;
    }

    public BuildingNumberQuery setExactMatchOnly() {
        this.exactMatchOnly = true;
        return this;
    }

    public BuildingNumberQuery setStreetExactMatchOnly() {
        this.streetExactMatchOnly = true;
        return this;
    }

    public String getBuildingNumberNormalized() {
        return new BuildingNumberNormalizer().normalize(buildingNumber);
    }

    public String getStreetNormalized() {
        return new StreetNormalizer().normalize(streetQuery);
    }
}
