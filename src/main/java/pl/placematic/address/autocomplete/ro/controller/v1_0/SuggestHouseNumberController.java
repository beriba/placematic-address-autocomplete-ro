package pl.placematic.address.autocomplete.ro.controller.v1_0;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.placematic.address.autocomplete.ro.algorithm.AddressApproximationAlgorithm;
import pl.placematic.address.autocomplete.ro.elastic.Address;
import pl.placematic.address.autocomplete.ro.elastic.repository.AddressRepository;
import pl.placematic.address.autocomplete.ro.logic.ApproximationRangeFilter;
import pl.placematic.address.autocomplete.ro.logic.ExactCityMatchFilter;
import pl.placematic.address.autocomplete.ro.query.BuildingNumberQuery;
import pl.placematic.address.autocomplete.ro.response.ResponseInterface;
import pl.placematic.address.autocomplete.ro.response.address.suggest.AddressResponseSchemaStrategy;
import pl.placematic.address.autocomplete.ro.util.extractor.BuildingNumberExtractor;
import pl.placematic.address.autocomplete.ro.util.factory.ApproximationFactory;
import pl.placematic.address.autocomplete.ro.util.vo.Approximation;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@RestController
@Validated
@RequestMapping(value = "/1.0")
public class SuggestHouseNumberController {
    @Autowired
    AddressRepository repository;

    @Autowired
    ApproximationRangeFilter approximationRangeFilter;

    @Autowired
    ExactCityMatchFilter exactCityMatchFilter;

    @RequestMapping(value = "/suggest/housenumber", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Iterable<ResponseInterface> suggestBuildingNumber(
            @RequestParam String query,
            @Valid @Pattern(regexp = "^.*$") @RequestParam String street,
            @Valid @Pattern(regexp = "^.*$") @RequestParam String cityDistricted,
            @Valid @Pattern(regexp = "^.*$") @RequestParam(required = false, defaultValue = "") String streetPrefix,
            @Valid @Pattern(regexp = "^.*$") @RequestParam(required = false, defaultValue = "") String zip,
            @Valid @Pattern(regexp = "^.*$") @RequestParam(required = false, defaultValue = "") String city,
            @Valid @Pattern(regexp = "^.*$") @RequestParam(required = false, defaultValue = "") String state,
            @Valid @Pattern(regexp = "^.*$") @RequestParam(required = false, defaultValue = "") String municipality,
            @Valid @Pattern(regexp = "^(basic|extended|full)$") @RequestParam String outputSchema,
            @Valid @Pattern(regexp = "^(true|false)$") @RequestParam(required = false, defaultValue = "false") String approximate,
            @Valid @Pattern(regexp = "^(true|false)$") @RequestParam(required = false, defaultValue = "false") String exactMatchOnly,
            @Valid @Pattern(regexp = "^(true|false)$") @RequestParam(required = false, defaultValue = "true") String streetExactMatchOnly,
            @Valid @Pattern(regexp = "^.*$") @RequestParam(required = false) String approximationRange,
            Pageable pageable
    ) throws Exception {
        if (query.isEmpty()) {
            throw new Exception("Query MUST NOT be empty."); //TODO
        }

        Approximation approximation = new ApproximationFactory().create(approximate, approximationRange);

        BuildingNumberQuery buildingNumberQuery = new BuildingNumberQuery(query, street, streetPrefix, cityDistricted, zip, city, municipality, state, pageable);

        if (exactMatchOnly.equals("true")) {
            buildingNumberQuery.setExactMatchOnly();
        }

        if (streetExactMatchOnly.equals("true")) {
            buildingNumberQuery.setStreetExactMatchOnly();
        }

        List<Address> addresses = repository.findBuildingNumbers(buildingNumberQuery);
        addresses = this.exactCityMatchFilter.filter(addresses, city, cityDistricted);

        if (approximation.isOn() && addresses.isEmpty()) {
            Pageable customPageable = new PageRequest(0, 100);
            addresses = repository.findWithoutBuildingNumber(buildingNumberQuery, customPageable);
            approximation.setBuildingNumber(new BuildingNumberExtractor().extractAsInt(query));
            addresses = this.approximationRangeFilter.filter(addresses, approximation);
            Address addressApprox = new AddressApproximationAlgorithm().approximate(addresses, query);
            addresses = new ArrayList<>();
            if (addressApprox != null) {
                addresses.add(addressApprox);
            }
        }

        return new AddressResponseSchemaStrategy(outputSchema).decide().adapt(addresses);
    }
}
