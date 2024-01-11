package pl.placematic.address.autocomplete.ro.response.address.suggest;

import pl.placematic.address.autocomplete.ro.elastic.Address;
import pl.placematic.address.autocomplete.ro.response.address.suggest.model.ExtendedSchemaResponse;

public class ExtendedAdapter extends BasicAdapter {
    @Override
    protected ExtendedSchemaResponse adaptSingle(Address address) {
        ExtendedSchemaResponse extended = new ExtendedSchemaResponse(super.adaptSingle(address));
        extended.setMunicipalityType(address.getMunicipalityType());
        extended.setXyClassDesc(address.getWgs84AccuracyClassDescription());
        extended.setStreetChange(address.getStreetChange());

        return extended;
    }
}
