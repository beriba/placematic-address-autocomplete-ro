package pl.placematic.address.autocomplete.ro.response.address.suggest.model;

import lombok.Data;

@Data
public class ExtendedSchemaResponse extends BasicSchemaResponse {
    private String municipalityType;
    private String xyClassDesc;
    private String streetChange;

    public ExtendedSchemaResponse(BasicSchemaResponse basicSchemaResponse) {
        this.setUuid(basicSchemaResponse.getUuid());
        this.setCity(basicSchemaResponse.getCity());
        this.setZip(basicSchemaResponse.getZip());
        this.setStreet(basicSchemaResponse.getStreet());
        this.setCityDistricted(basicSchemaResponse.getCityDistricted());
        this.setHouseNumber(basicSchemaResponse.getHouseNumber());
        this.setHouseNumberApproximated(basicSchemaResponse.getHouseNumberApproximated());
        this.setLocation(basicSchemaResponse.getLocation());
        this.setState(basicSchemaResponse.getState());
        this.setMunicipality(basicSchemaResponse.getMunicipality());
        this.setStreetPrefix(basicSchemaResponse.getStreetPrefix());
        this.setDistance(basicSchemaResponse.getDistance());
    }
}
