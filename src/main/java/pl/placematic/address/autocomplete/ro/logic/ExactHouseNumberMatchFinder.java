package pl.placematic.address.autocomplete.ro.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.placematic.address.autocomplete.ro.elastic.Address;
import pl.placematic.address.autocomplete.ro.singlestring.Query;
import pl.placematic.address.autocomplete.ro.util.extractor.BuildingNumberExtractor;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExactHouseNumberMatchFinder {

    @Autowired
    private BuildingNumberExtractor buildingNumberExtractor;

    public List<Address> findAll(List<Address> input, Query query) {
        String extractedHouseNumber = buildingNumberExtractor.extractFullWithLetters(query.getRawQuery());
        List<Address> exacts = new ArrayList<>();
        for (Address address : input) {
            if (
                    address.getBuildingNumber() != null &&
                    address.getBuildingNumber().toUpperCase().equals(extractedHouseNumber.toUpperCase())
            ) {
                exacts.add(address);
            }
        }

        return exacts;
    }

    public List<Address> findByBuildingNumber(List<Address> input, String buildingNumber) {
        List<Address> list = new ArrayList<>();
        for (Address address : input) {
            if (
                    address.getBuildingNumber() != null &&
                    address.getBuildingNumber().toUpperCase().equals(buildingNumber.toUpperCase())
            ) {
                list.add(address);
            }
        }

        if (list.isEmpty()) {
            return null;
        }

        return list;
    }
}
