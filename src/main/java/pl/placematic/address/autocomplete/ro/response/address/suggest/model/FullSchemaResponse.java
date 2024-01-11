package pl.placematic.address.autocomplete.ro.response.address.suggest.model;

import lombok.Data;

@Data
public class FullSchemaResponse extends ExtendedSchemaResponse {
    private String mwg7d;
    private String mwg14;
    private String eurogrid62;
    private String municipalitySegment;
    private String populationSegment;
    private String cityDistrictedType;
    private String sym;
    private String symd;
    private String symul;
    private String buildingType;
    private String neighbourhood;
    private Integer masl;

    public FullSchemaResponse(ExtendedSchemaResponse extendedSchemaResponse) {
        super(extendedSchemaResponse);
        this.setMunicipalityType(extendedSchemaResponse.getMunicipalityType());
        this.setXyClassDesc(extendedSchemaResponse.getXyClassDesc());
        this.setStreetChange(extendedSchemaResponse.getStreetChange());
    }
}
