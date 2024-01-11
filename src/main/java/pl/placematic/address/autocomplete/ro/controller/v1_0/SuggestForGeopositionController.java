package pl.placematic.address.autocomplete.ro.controller.v1_0;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.placematic.address.autocomplete.ro.elastic.Address;
import pl.placematic.address.autocomplete.ro.elastic.repository.AddressRepository;
import pl.placematic.address.autocomplete.ro.exception.UnsupportedSchemaException;
import pl.placematic.address.autocomplete.ro.response.ResponseInterface;
import pl.placematic.address.autocomplete.ro.response.address.suggest.AddressResponseSchemaStrategy;
import pl.placematic.address.autocomplete.ro.util.vo.Location;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

@RestController
@Validated
@RequestMapping(value = "/1.0")
public class SuggestForGeopositionController {

    @Autowired
    AddressRepository repository;

    @RequestMapping(value = "/suggest/address-for-geoposition", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Iterable<ResponseInterface> addressForGeoposition(
            @RequestParam String latitude,
            @RequestParam String longitude,
            @Valid @Pattern(regexp = "^(basic|extended|full)$") @RequestParam String outputSchema,
            Pageable pageable
    ) throws UnsupportedSchemaException {
        Iterable<Address> addresses = repository.findByLocation(latitude, longitude, pageable);

        return new AddressResponseSchemaStrategy(outputSchema)
                .decide()
                .setQueryPoint(new Location(latitude, longitude))
                .adapt(addresses);
    }
}
