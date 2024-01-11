package pl.placematic.address.autocomplete.ro.controller.v1_0;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.placematic.address.autocomplete.ro.elastic.repository.AddressRepository;
import pl.placematic.address.autocomplete.ro.response.City;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

@RestController
@Validated
@RequestMapping(value = "/1.0")
public class SuggestCityController {

    @Autowired
    AddressRepository repository;

    @RequestMapping(value = "/suggest/city", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Iterable<City> suggestCity(
            @RequestParam String query,
            @Valid @Pattern(regexp = "^.*$") @RequestParam(required = false, defaultValue = "") String state,
            @Valid @Pattern(regexp = "^.*$") @RequestParam(required = false, defaultValue = "") String municipality
    ) {
        Iterable<City> cities = repository.findByCity(query, municipality, state);

        return cities;
    }
}
