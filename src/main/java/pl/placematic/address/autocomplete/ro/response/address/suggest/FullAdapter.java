package pl.placematic.address.autocomplete.ro.response.address.suggest;

import pl.placematic.address.autocomplete.ro.elastic.Address;
import pl.placematic.address.autocomplete.ro.response.address.suggest.model.FullSchemaResponse;

public class FullAdapter extends ExtendedAdapter {
    @Override
    protected FullSchemaResponse adaptSingle(Address address) {
        FullSchemaResponse full = new FullSchemaResponse(super.adaptSingle(address));
        full.setMwg7d(address.getMgw7d());
        full.setMwg14(address.getMgw14d());
        full.setEurogrid62(address.getEurogrid62());
        full.setMunicipalitySegment(address.getMunicipalitySegment());
        full.setCityDistrictedType(address.getCityDistrictedType());
        full.setBuildingType(address.getBuildingTypeDescription());
        full.setNeighbourhood(address.getNeighbourhoodDescription());
        full.setMasl(address.getMetersAboveSeaLevel());
        full.setPopulationSegment(address.getPopulationSegment());

        return full;
    }
}
