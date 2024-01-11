package pl.placematic.address.autocomplete.ro.elastic;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import pl.placematic.address.autocomplete.ro.AddressAutocompleteROApplication;
import pl.placematic.address.autocomplete.ro.response.CityInterface;
import pl.placematic.address.autocomplete.ro.util.extractor.BuildingNumberExtractor;

import java.util.ArrayList;
import java.util.Map;

@Data
@Document(indexName = "#{@indexName}", type = "_doc")
public class Address implements CityInterface {

    private static final Logger logger = LogManager.getLogger(AddressAutocompleteROApplication.class);

    @Id
    private final String uuid;
    private final String voivodeship;
    private final String county;
    private final String city;
    private final String cityDistricted;
    private final String cityDistrictedType;
    private final String cityAlternative;
    private final String district;
    private final String districtAlternative;
    private final String zip;
    private final String street;
    private final String streetPrefix;
    private final String streetChange;
    private String buildingNumber;
    private String buildingNumberApproximated;
    private final String buildingTypeDescription;
    private final String municipality;
    private final String municipalityType;
    private final String municipalitySegment;
    private final String populationSegment;
    private final String neighbourhoodDescription;
    private final String wgs84Latitude;
    private final String wgs84Longitude;
    private final Integer wgs84AccuracyClass;
    private String wgs84AccuracyClassDescription;
    private final String mgw7d;
    private final String mgw14d;
    private final String eurogrid62;
    private final Integer metersAboveSeaLevel;

    private Integer buildingNumberInteger;

    @JsonCreator
    public Address(
            @JsonProperty("uuid") String uuid,
            @JsonProperty("voivodeship") String voivodeship,
            @JsonProperty("county") String county,
            @JsonProperty("city") String city,
            @JsonProperty("city_districted") String cityDistricted,
            @JsonProperty("city_districted_type") String cityDistrictedType,
            @JsonProperty("city_teryt") String cityTeryt,
            @JsonProperty("district") String district,
            @JsonProperty("district_alternative") String districtAlternative,
            @JsonProperty("zip") String zip,
            @JsonProperty("street") String street,
            @JsonProperty("street_prefix") String streetPrefix,
            @JsonProperty("street_change") String streetChange,
            @JsonProperty("building_number") String buildingNumber,
            @JsonProperty("building_number_approximated") String buildingNumberApproximated,
            @JsonProperty("building_type_description") String buildingTypeDescription,
            @JsonProperty("municipality") String municipality,
            @JsonProperty("municipality_type") String municipalityType,
            @JsonProperty("municipality_segment") String municipalitySegment,
            @JsonProperty("neighbourhood_description") String neighbourhoodDescription,
            @JsonProperty("wgs84_location") Map<String, String> wgs84Location,
            @JsonProperty("wgs84_accuracy_class") Integer wgs84AccuracyClass,
            @JsonProperty("wgs84_accuracy_class_description") String wgs84AccuracyClassDescription,
            @JsonProperty("mwg7d_teryt") String mgw7d,
            @JsonProperty("mwg14d_teryt") String mgw14d,
            @JsonProperty("eurogrid62") String eurogrid62,
            @JsonProperty("meters_above_sea_level") Integer metersAboveSeaLevel,
            @JsonProperty("population_segment") String populationSegment
    ) {
        this.uuid = uuid;
        this.voivodeship = voivodeship;
        this.county = county;
        this.city = city;
        this.cityDistricted = cityDistricted;
        this.cityDistrictedType = cityDistrictedType;
        this.cityAlternative = cityTeryt;
        this.district = district;
        this.districtAlternative = districtAlternative;
        this.zip = zip;
        this.street = street;
        this.streetPrefix = streetPrefix;
        this.streetChange = streetChange;
        this.buildingNumber = buildingNumber;
        this.buildingNumberApproximated = buildingNumberApproximated;
        this.buildingTypeDescription = buildingTypeDescription;
        this.municipality = municipality;
        this.municipalityType = municipalityType;
        this.municipalitySegment = municipalitySegment;
        this.neighbourhoodDescription = neighbourhoodDescription;
        this.wgs84Latitude = wgs84Location.get("lat");
        this.wgs84Longitude = wgs84Location.get("lon");
        this.wgs84AccuracyClass = wgs84AccuracyClass;
        this.wgs84AccuracyClassDescription = wgs84AccuracyClassDescription;
        this.mgw7d = mgw7d;
        this.mgw14d = mgw14d;
        this.eurogrid62 = eurogrid62;
        this.metersAboveSeaLevel = metersAboveSeaLevel;
        this.populationSegment = populationSegment;
    }

    public Integer getBuildingNumberInteger() {
        if (buildingNumberInteger == null) {
            buildingNumberInteger = new BuildingNumberExtractor().extractAsInt(this.getBuildingNumber());
        }

        return buildingNumberInteger;
    }

    public boolean isRangeBuildingNumber() {
        if (buildingNumber.matches("^\\d+-\\d+[.]?$")) {
            return true;
        }

        return false;
    }

    public ArrayList<Integer> getBuildingNumbersInRange() {
        ArrayList<Integer> coll = new ArrayList<>();

        int start = Integer.parseInt(this.buildingNumber.replaceAll("(\\d+)-\\d+[.]?", "$1"));
        int end = Integer.parseInt(this.buildingNumber.replaceAll("\\d+-(\\d+)[.]?", "$1"));

        for (int i = start; i <= end; i++) {
            coll.add(i);
        }

        return coll;
    }
}
