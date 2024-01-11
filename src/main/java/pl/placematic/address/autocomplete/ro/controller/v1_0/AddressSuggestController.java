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
import pl.placematic.address.autocomplete.ro.exception.UnsupportedSchemaException;
import pl.placematic.address.autocomplete.ro.logic.ApproximationRangeFilter;
import pl.placematic.address.autocomplete.ro.response.ResponseInterface;
import pl.placematic.address.autocomplete.ro.response.address.suggest.AddressResponseSchemaStrategy;
import pl.placematic.address.autocomplete.ro.singlestring.Query;
import pl.placematic.address.autocomplete.ro.util.extractor.BuildingNumberExtractor;
import pl.placematic.address.autocomplete.ro.util.factory.ApproximationFactory;
import pl.placematic.address.autocomplete.ro.util.strategy.SelectorFieldStrategy;
import pl.placematic.address.autocomplete.ro.util.strategy.SortFieldStrategy;
import pl.placematic.address.autocomplete.ro.util.vo.Approximation;
import pl.placematic.address.autocomplete.ro.util.vo.selector.SelectorField;
import pl.placematic.address.autocomplete.ro.util.vo.sort.SortField;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@RestController
@Validated
@RequestMapping(value = "/1.0")
public class AddressSuggestController {

    @Autowired
    AddressRepository repository;

    @Autowired
    private ApproximationRangeFilter approximationRangeFilter;

    @Autowired
    private BuildingNumberExtractor buildingNumberExtractor;

    //somehow autowire doesn't work
    private SortFieldStrategy sortFieldStrategy = new SortFieldStrategy();
    private SelectorFieldStrategy selectFieldStrategy = new SelectorFieldStrategy();

    @RequestMapping(value = "/suggest/address", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Iterable<ResponseInterface> singleString(
            @RequestParam String query,
            @Valid @Pattern(regexp = "^(basic|extended|full)$") @RequestParam String outputSchema,
            @Valid @Pattern(regexp = "^(true|false)$") @RequestParam(required = false, defaultValue = "false") String approximate,
            @Valid @Pattern(regexp = "^.*$") @RequestParam(required = false) String approximationRange,
            @Valid @Pattern(regexp = "^(municipalityClass|populationClass)$") @RequestParam(required = false) String sortBy,
            @Valid @Pattern(regexp = "^(oneForEachCity)$") @RequestParam(required = false) String selector,
            Pageable pageable
    ) throws UnsupportedSchemaException {
        SortField sortByField = sortFieldStrategy.decide(sortBy);
        SelectorField selectByField = selectFieldStrategy.decide(selector);

        Query singlestringQuery = new Query(query, sortByField, selectByField);

        Approximation approximation = new ApproximationFactory().create(approximate, approximationRange);

        List<Address> addresses = repository.findSingleString(singlestringQuery, pageable);

        if (approximation.isOn() && addresses.isEmpty()) {
            String buildingNumberFull = buildingNumberExtractor.extractAsString(query);
            Integer buildingNumber = buildingNumberExtractor.extractAsInt(query);
            if (buildingNumber != null) {
                approximation.setBuildingNumber(buildingNumber);
                Pageable customPageable = new PageRequest(0, 2000);
                addresses = repository.findSingleStringWithoutBuildingNumber(singlestringQuery, customPageable);
                addresses = this.approximationRangeFilter.filter(addresses, approximation);
                Address addressApprox = new AddressApproximationAlgorithm().approximate(addresses, buildingNumberFull);
                addresses = new ArrayList<>();
                if (addressApprox != null) {
                    addresses.add(addressApprox);
                }
            }
        }

        return new AddressResponseSchemaStrategy(outputSchema).decide().adapt(addresses);
    }

}
