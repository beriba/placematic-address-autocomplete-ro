package pl.placematic.address.autocomplete.ro.response.address.suggest;

import pl.placematic.address.autocomplete.ro.elastic.Address;
import pl.placematic.address.autocomplete.ro.response.ResponseInterface;
import pl.placematic.address.autocomplete.ro.response.address.suggest.model.BasicSchemaResponse;
import pl.placematic.address.autocomplete.ro.util.Haversine;
import pl.placematic.address.autocomplete.ro.util.vo.Location;
import pl.placematic.address.autocomplete.ro.util.vo.LocationWithClass;

import java.util.ArrayList;

public class BasicAdapter implements AddressResponseAdapterInterface {

    protected Location queryPoint;

    @Override
    public final Iterable<ResponseInterface> adapt(Iterable<Address> addresses) {
        ArrayList<ResponseInterface> response = new ArrayList<>();
        for (Address address : addresses) {
            response.add(this.adaptSingle(address));
        }
        return response;
    }

    @Override
    public AddressResponseAdapterInterface setQueryPoint(Location queryPoint) {
        this.queryPoint = queryPoint;
        return this;
    }

    protected BasicSchemaResponse adaptSingle(Address address) {
        BasicSchemaResponse basic = new BasicSchemaResponse();
        basic.setUuid(address.getUuid());
        basic.setCity(address.getCity());
        basic.setCityAlternative(address.getCityAlternative());
        basic.setZip(address.getZip());
        basic.setStreet(address.getStreet());
        basic.setCityDistricted(address.getCityDistricted());
        basic.setHouseNumber(address.getBuildingNumber());
        basic.setHouseNumberApproximated(address.getBuildingNumberApproximated());
        basic.setLocation(new LocationWithClass(address.getWgs84Latitude(), address.getWgs84Longitude(), address.getWgs84AccuracyClass()));
        basic.setState(address.getVoivodeship());
        basic.setMunicipality(address.getMunicipality());
        basic.setStreetPrefix(address.getStreetPrefix());
        basic.setDistrict(address.getDistrict());
        basic.setDistrictAlternative(address.getDistrictAlternative());

        if (queryPoint != null) {
            basic.setDistance(Haversine.distance(
                    queryPoint.getLatitude(),
                    queryPoint.getLongitude(),
                    Double.parseDouble(address.getWgs84Latitude()),
                    Double.parseDouble(address.getWgs84Longitude())
            ));
        }

        return basic;
    }
}
