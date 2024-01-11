package pl.placematic.address.autocomplete.ro.elastic.repository;

import org.springframework.data.domain.Pageable;
import pl.placematic.address.autocomplete.ro.elastic.Address;
import pl.placematic.address.autocomplete.ro.query.BuildingNumberQuery;
import pl.placematic.address.autocomplete.ro.response.City;
import pl.placematic.address.autocomplete.ro.response.Street;
import pl.placematic.address.autocomplete.ro.response.Zip;
import pl.placematic.address.autocomplete.ro.singlestring.Query;

import java.util.List;

public interface AddressRepositoryCustom {

    List<Address> findSingleString(Query singlestringQuery, Pageable pageable);

    List<Address> findSingleStringWithoutBuildingNumber(Query singlestringQuery, Pageable pageable);

    List<City> findByCity(String cityQuery, String municipality, String voivodeship);

    List<Street> findStreets(String streetQuery, String city, String municipality, String voivodeship, String streetPrefix);

    List<Zip> findZips(String query, String street, String buildingNumber, String city, String cityDistricted, String municipality, String state, String streetPrefix, boolean streetExactMatchOnly);

    List<Address> findBuildingNumbers(BuildingNumberQuery buildingNumberQuery);

    List<Address> findWithoutBuildingNumber(BuildingNumberQuery buildingNumberQuery, Pageable pageable);

    List<Address> findByLocation(String latitude, String longitude, Pageable pageable);
}
