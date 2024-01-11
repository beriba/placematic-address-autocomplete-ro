package pl.placematic.address.autocomplete.ro.controller.v1_0;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.placematic.address.autocomplete.ro.elastic.repository.AddressRepository;
import pl.placematic.address.autocomplete.ro.logic.ExactCityMatchFilter;
import pl.placematic.address.autocomplete.ro.logic.ExactStreetMatchDecorator;
import pl.placematic.address.autocomplete.ro.response.Street;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.List;

@RestController
@Validated
@RequestMapping(value = "/1.0")
public class SuggestStreetController {

    @Autowired
    AddressRepository repository;

    @Autowired
    ExactCityMatchFilter exactCityMatchFilter;

    @Autowired
    ExactStreetMatchDecorator exactStreetMatchDecorator;

    @RequestMapping(value = "/suggest/street", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Iterable<Street> suggestStreet(
            @RequestParam String query,
            @Valid @Pattern(regexp = "^.*$") @RequestParam String city,
            @Valid @Pattern(regexp = "^.*$") @RequestParam(required = false, defaultValue = "") String state,
            @Valid @Pattern(regexp = "^.*$") @RequestParam(required = false, defaultValue = "") String municipality,
            @Valid @Pattern(regexp = "^.*$") @RequestParam(required = false, defaultValue = "") String streetPrefix
    ) {
        List<Street> streets = repository.findStreets(query, city, municipality, state, streetPrefix);
        streets = this.exactCityMatchFilter.filter(streets, city, city);
        streets = this.exactStreetMatchDecorator.decorate(streets, query);

        return streets;
    }
}
