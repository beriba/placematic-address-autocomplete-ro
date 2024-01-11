package pl.placematic.address.autocomplete.ro.response.address.suggest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import pl.placematic.address.autocomplete.ro.response.ResponseInterface;
import pl.placematic.address.autocomplete.ro.util.vo.LocationWithClass;

@Data
public class BasicSchemaResponse implements ResponseInterface {
    private String uuid;
    private String state;
    private String municipality;
    private String city;
    private String cityAlternative;
    private String cityDistricted;
    private String district;
    private String districtAlternative;
    private String zip;
    private String street;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String streetTeryt;
    private String streetPrefix;
    private String houseNumber;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String houseNumberApproximated;

    private LocationWithClass location;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double distance;

    public void setStreet(String street) {
        this.street = street;
        this.streetTeryt = street;
    }
}
