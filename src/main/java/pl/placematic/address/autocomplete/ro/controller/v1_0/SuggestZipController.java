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
import pl.placematic.address.autocomplete.ro.response.Zip;
import pl.placematic.address.autocomplete.ro.singlestring.Query;
import pl.placematic.address.autocomplete.ro.util.factory.ApproximationFactory;
import pl.placematic.address.autocomplete.ro.util.vo.Approximation;
import pl.placematic.address.autocomplete.ro.util.vo.selector.NoSelectorField;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.List;

@RestController
@Validated
@RequestMapping(value = "/1.0")
public class SuggestZipController {
    @Autowired
    AddressRepository repository;

    @Autowired
    private ApproximationRangeFilter approximationRangeFilter;

    @RequestMapping(value = "/suggest/zip", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Iterable<Zip> suggestZip(
            @RequestParam String query,
            @Valid @Pattern(regexp = "^.*$") @RequestParam String street,
            @Valid @Pattern(regexp = "^.*$") @RequestParam(required = false, defaultValue = "") String houseNumber,
            @Valid @Pattern(regexp = "^.*$") @RequestParam(required = false, defaultValue = "") String city,
            @Valid @Pattern(regexp = "^.*$") @RequestParam(required = false, defaultValue = "") String cityDistricted,
            @Valid @Pattern(regexp = "^.*$") @RequestParam(required = false, defaultValue = "") String state,
            @Valid @Pattern(regexp = "^.*$") @RequestParam(required = false, defaultValue = "") String municipality,
            @Valid @Pattern(regexp = "^.*$") @RequestParam(required = false, defaultValue = "") String streetPrefix,
            @Valid @Pattern(regexp = "^(true|false)$") @RequestParam(required = false, defaultValue = "true") String streetExactMatchOnly,
            @Valid @Pattern(regexp = "^(true|false)$") @RequestParam(required = false, defaultValue = "false") String approximate,
            @Valid @Pattern(regexp = "^.*$") @RequestParam(required = false) String approximationRange
    ) {
        List<Zip> zips = repository.findZips(query, street, houseNumber, city, cityDistricted, municipality, state, streetPrefix.trim(), streetExactMatchOnly.equals("true"));

        if (zips.isEmpty()) {
            Approximation approximation = new ApproximationFactory().create(approximate, approximationRange);

            String buildingNumber = houseNumber.trim();
            if (approximation.isOn() && !buildingNumber.isEmpty()) {
                Integer buildingNumberInt = Integer.parseInt(buildingNumber);
                if (buildingNumberInt != null) {
                    approximation.setBuildingNumber(buildingNumberInt);
                    Pageable customPageable = new PageRequest(0, 2000);

                    String approxQuery = municipality + " " + city + " " + cityDistricted + " " + streetPrefix + " " + street + " " + houseNumber;
                    approxQuery = approxQuery.replaceAll("\\s+", " ");
                    Query singlestringQuery = new Query(approxQuery, null, new NoSelectorField());

                    List<Address> addresses = repository.findSingleStringWithoutBuildingNumber(singlestringQuery, customPageable);
                    addresses = this.approximationRangeFilter.filter(addresses, approximation);
                    Address addressApprox = new AddressApproximationAlgorithm().approximate(addresses, buildingNumber);

                    if (addressApprox != null) {
                        zips.add(new Zip(addressApprox.getZip()).setApproximated());
                    }
                }
            }
        }

        return zips;
    }
}
